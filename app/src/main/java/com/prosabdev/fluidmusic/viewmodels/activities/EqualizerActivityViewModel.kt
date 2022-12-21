package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.equalizer.EqualizerPresetItem
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class EqualizerActivityViewModel(app: Application) : AndroidViewModel(app)  {
    //Equalizer bands
    private val mMutableEqualizerState = MutableLiveData<Boolean>(null)
    private val mMutableCurrentPreset = MutableLiveData<CharSequence?>(null)
    private val mMutablePresetBandsLevels = MutableLiveData<Array<Short>?>(null)
    private val mMutableCustomPresets = MutableLiveData<List<EqualizerPresetItem>?>(null)
    //Tone
    private val mMutableToneState = MutableLiveData<Boolean>(null)
    private val mMutableBassBoostProgress = MutableLiveData<Int>(null)
    private val mMutableVisualizerProgress = MutableLiveData<Int>(null)

    //Equalizer bands
    private val mEqualizerState: LiveData<Boolean> get() = mMutableEqualizerState
    private val mCurrentPreset: LiveData<CharSequence?> get() = mMutableCurrentPreset
    private val mPresetBandsLevels: LiveData<Array<Short>?> get() = mMutablePresetBandsLevels
    private val mCustomPresets: MutableLiveData<List<EqualizerPresetItem>?> get() = mMutableCustomPresets
    //Tone
    private val mToneState: LiveData<Boolean> get() = mMutableToneState
    private val mBassBoostProgress: LiveData<Int> get() = mMutableBassBoostProgress
    private val mVisualizerProgress: LiveData<Int> get() = mMutableVisualizerProgress

    //On time assign variables
    private var mLocalMediaPlayer: MediaPlayer? = null
    private var mEqualizer: Equalizer? = null
    private var mPresets: Array<CharSequence>? = null

    fun initSilentEqualizer(ctx: Context) {
        try {
            mLocalMediaPlayer = MediaPlayer.create(ctx, R.raw.monaldin_hold_me)
            mLocalMediaPlayer?.let { mPlayer ->
                mPlayer.start()
                mEqualizer = Equalizer(0, mPlayer.audioSessionId)
            }
        }catch (error: Throwable){
            error.printStackTrace()
        }
    }
    fun stopLocalMediaPlayer(){
        try {
            mEqualizer = null
            mLocalMediaPlayer?.stop()
            mLocalMediaPlayer?.release()
            mLocalMediaPlayer = null
        }catch (error: Throwable){
            error.printStackTrace()
        }
    }

    suspend fun listenCustomPresets(owner: LifecycleOwner, viewModel: EqualizerPresetItemViewModel){
        viewModel.getAll()?.observe(owner, Observer {
            MainScope().launch {
                mMutableCustomPresets.value = it
            }
        })
    }
    fun getEqualizerState(): LiveData<Boolean> {
        return mEqualizerState
    }
    fun setEqualizerState(value: Boolean){
        MainScope().launch {
            mEqualizer?.enabled = value
            mMutableEqualizerState.value = value
        }
    }
    fun getToneState(): LiveData<Boolean> {
        return mToneState
    }
    fun setToneState(value: Boolean){
        MainScope().launch {
            mMutableToneState.value = value
        }
    }
    fun getCurrentPreset(): LiveData<CharSequence?> {
        return mCurrentPreset
    }
    fun setCurrentPreset(preset: CharSequence?, viewModel: EqualizerPresetBandLevelItemViewModel?){
        MainScope().launch {
            mMutableCurrentPreset.value = preset
            if(preset == null || preset.isEmpty()) return@launch

            if(viewModel == null) return@launch
            val presetId = getPresetIdFromName(preset)
            if(presetId >= 0){
                mEqualizer?.let { equ ->
                    equ.usePreset(presetId)
                    val tempPresetsLevels = Array<Short>(equ.numberOfBands.toInt()){0}
                    for(i in 0 until equ.numberOfBands){
                        tempPresetsLevels[i] = equ.getBandLevel(i.toShort())
                    }
                    mMutablePresetBandsLevels.value = tempPresetsLevels
                }
            }else{
                val tempCustomPresets = mCustomPresets.value
                tempCustomPresets?.let { cP ->
                    for(element in cP){
                        if(preset == element.presetName){
                            val tempCustomPresetBandsLevels = viewModel.getAllAtPresetName(preset as String?)
                            val tempPresetsLevels = Array<Short>(tempCustomPresetBandsLevels?.size ?: return@let){0}
                            for (i in tempCustomPresetBandsLevels.indices){
                                tempPresetsLevels[tempCustomPresetBandsLevels[i].bandId.toInt()] = tempCustomPresetBandsLevels[i].bandLevel
                            }
                            mMutablePresetBandsLevels.value = tempPresetsLevels
                        }
                    }
                }
            }
        }
    }
    suspend fun getBandLevel(preset: CharSequence?, bandId: Short, viewModel: EqualizerPresetBandLevelItemViewModel): Short {
        if(preset == null || preset.isEmpty()) {
            mPresetBandsLevels.value?.let {
                return it[bandId.toInt()]
            }
            return 0
        }

        val presetId = getPresetIdFromName(preset)
        return if(presetId >= 0){
            mEqualizer?.usePreset(presetId)
            mEqualizer?.getBandLevel(bandId) ?: 0
        }else{
            val tempCustomPresetBandsLevels = viewModel.getBandAtPresetName(preset as String?, bandId)
            tempCustomPresetBandsLevels?.bandLevel ?: 0
        }
    }
    fun setBandLevel(bandId: Short, level: Short) {
        mEqualizer?.setBandLevel(bandId, level)
        val tempPresetBandsLevels = mPresetBandsLevels.value
        tempPresetBandsLevels?.let {
            it[bandId.toInt()] = level
        }
        MainScope().launch {
            mMutablePresetBandsLevels.value = tempPresetBandsLevels
        }
    }
    /**
     * Getters
     */
    fun getBandLevelRangeMin(): Short {
        return mEqualizer?.let {
            it.bandLevelRange[0]
        } ?: 0
    }
    fun getBandLevelRangeMax(): Short {
        return mEqualizer?.let {
            it.bandLevelRange[1]
        } ?: 0
    }
    fun getBandsCount(): Short {
        return mEqualizer?.numberOfBands ?: 0
    }
    fun getPresetName(presetId: Short): CharSequence? {
        if(mPresets != null){
            return mPresets?.let {
                it[presetId.toInt()]
            }
        }
        return mEqualizer?.getPresetName(presetId)
    }
    fun getPresets(): Array<CharSequence>? {
        if(mPresets == null){
            mEqualizer?.let { equ ->
                val totalPreset: Short = mEqualizer?.numberOfPresets ?: 0
                mPresets = Array(totalPreset.toInt()){""}
                for (i in 0 until totalPreset){
                    mPresets?.let{
                        it[i] = equ.getPresetName(i.toShort())
                    }
                }
            }
        }
        return mPresets
    }
    fun getCenterFrequency(bandId: Short): Int {
        return (mEqualizer?.getCenterFreq(bandId) ?: 0)
    }
    fun getPresetIdFromName(presetName: CharSequence?): Short {
        if(presetName == null || presetName.isEmpty()) return -1
        if(mPresets == null){
            val presetsCount = mEqualizer?.numberOfPresets ?: 0
            for (i in 0 until presetsCount){
                if(presetName == mEqualizer?.getPresetName(i.toShort())){
                    return i.toShort()
                }
            }
        }else{
            for (i in mPresets?.indices ?: return -1){
                if(presetName == mPresets?.get(i)){
                    return i.toShort()
                }
            }
        }
        return -1
    }
    fun getPresetBandsLevels(): LiveData<Array<Short>?> {
        return mPresetBandsLevels
    }
}