package com.prosabdev.fluidmusic.viewmodels.activities

import android.annotation.SuppressLint
import android.app.Application
import android.media.audiofx.Equalizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.prosabdev.common.models.equalizer.EqualizerPresetItem
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.ui.custom.visualizer.ExoVisualizer
import com.prosabdev.fluidmusic.ui.custom.visualizer.FFTAudioProcessor
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel

@SuppressLint("LongLogTag")
class EqualizerActivityViewModel(app: Application) : AndroidViewModel(app)  {

    //Equalizer bands
    val equalizerState = MutableLiveData<Boolean>(null)
    val currentPreset = MutableLiveData<CharSequence?>(null)
    val presetBandsLevels = MutableLiveData<Array<Short>?>(null)
    val customPresets = MutableLiveData<List<EqualizerPresetItem>?>(null)
    //Tone
    val toneState = MutableLiveData<Boolean>(null)
    val bassBoost = MutableLiveData<Int>(null)
    val visualizer = MutableLiveData<Int>(null)
    val volume = MutableLiveData<Int>(null)

    private var mEqualizer: Equalizer? = null
    var mediaEventsListener: MediaEventsListener = object : MediaEventsListener() {

    }

    fun initSilentEqualizer(visualizerView: ExoVisualizer) {
        try {
            visualizerView.processor = getFFTAudioProcessor()
        }catch (error: Throwable){
            error.printStackTrace()
        }
    }
    private fun getFFTAudioProcessor(): FFTAudioProcessor? {
        //
        return null
    }

    suspend fun loadPresets(owner: LifecycleOwner, viewModel: EqualizerPresetItemViewModel){
        viewModel.getAll()?.observe(owner) {
            customPresets.value = it
        }
    }

    fun stopEqualizer() {
        //
    }

    suspend fun setCurrentPreset(preset: CharSequence?, viewModel: EqualizerPresetBandLevelItemViewModel?){
        currentPreset.value = preset
        if(preset.isNullOrEmpty()) return

        if(viewModel == null) return
        val presetId = getPresetIdFromName(preset)
        val presetsCount = mEqualizer?.numberOfPresets ?: 0
        if(presetId < presetsCount){
            mEqualizer?.let { equ ->
                equ.usePreset(presetId)
                val tempPresetsLevels = Array<Short>(equ.numberOfBands.toInt()){0}
                for(i in 0 until equ.numberOfBands){
                    tempPresetsLevels[i] = equ.getBandLevel(i.toShort())
                }
                presetBandsLevels.value = tempPresetsLevels
            }
        }else{
            val tempCustomPresets = customPresets.value
            tempCustomPresets?.let { cP ->
                for(element in cP){
                    if(preset == element.presetName){
                        val tempCustomPresetBandsLevels = viewModel.getAllAtPresetName(preset as String?)
                        val tempPresetsLevels = Array<Short>(tempCustomPresetBandsLevels?.size ?: return@let){0}
                        for (i in tempCustomPresetBandsLevels.indices){
                            tempPresetsLevels[tempCustomPresetBandsLevels[i].bandId.toInt()] = tempCustomPresetBandsLevels[i].bandLevel
                        }
                        presetBandsLevels.value = tempPresetsLevels
                    }
                }
            }
        }
    }
    suspend fun getBandLevel(preset: CharSequence?, bandId: Short, viewModel: EqualizerPresetBandLevelItemViewModel): Short {
        if(preset.isNullOrEmpty()) {
            presetBandsLevels.value?.let {
                return it[bandId.toInt()]
            }
            return 0
        }

        val presetsCount = mEqualizer?.numberOfPresets ?: 0
        val presetId = getPresetIdFromName(preset)
        return if(presetId < presetsCount){
            mEqualizer?.usePreset(presetId)
            mEqualizer?.getBandLevel(bandId) ?: 0
        }else{
            val tempCustomPresetBandsLevels = viewModel.getBandAtPresetName(preset as String?, bandId)
            tempCustomPresetBandsLevels?.bandLevel ?: 0
        }
    }
    fun setBandLevel(bandId: Short, level: Short) {
        mEqualizer?.setBandLevel(bandId, level)
        val tempPresetBandsLevels = presetBandsLevels.value
        tempPresetBandsLevels?.let {
            it[bandId.toInt()] = level
        }
        presetBandsLevels.value = tempPresetBandsLevels
    }

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
        return mEqualizer?.getPresetName(presetId)
    }
    fun getPresets(): Array<CharSequence>? {
        mEqualizer?.let { equ ->
            val totalPreset: Short = mEqualizer?.numberOfPresets ?: 0
            val tempPresets = Array<CharSequence>(totalPreset.toInt()){""}
            for (i in 0 until totalPreset){
                tempPresets[i] = equ.getPresetName(i.toShort())
            }
            return tempPresets
        }
        return null
    }
    fun getCenterFrequency(bandId: Short): Int {
        return (mEqualizer?.getCenterFreq(bandId) ?: 0)
    }
    fun getPresetIdFromName(presetName: CharSequence?): Short {
        if(presetName.isNullOrEmpty()) return -1

        val presetsCount: Short = mEqualizer?.numberOfPresets ?: 0
        //Check on integrated presets
        for (i in 0 until presetsCount){
            if(presetName == mEqualizer?.getPresetName(i.toShort())){
                return i.toShort()
            }
        }
        //Check on custom presets
        val customPresetsCount: Int = customPresets.value?.size ?: return -1
        for (i in 0 until customPresetsCount){
            if(presetName == customPresets.value?.get(i)?.presetName){
                return (presetsCount+i).toShort()
            }
        }
        return -1
    }

    fun checkIfPresetExists(presetName: String): Boolean {
        val presetsCount: Short = mEqualizer?.numberOfPresets ?: 0
        //Check on integrated presets
        for (i in 0 until presetsCount){
            val tempItemPreset: String = mEqualizer?.getPresetName(i.toShort()) ?: ""
            if(presetName == tempItemPreset){
                return true
            }
        }
        //Check on custom presets
        val customPresetsCount: Int = customPresets.value?.size ?: return false
        for (i in 0 until customPresetsCount){
            val tempItemPreset: String = customPresets.value?.get(i)?.presetName ?: ""
            if(presetName == tempItemPreset){
                return true
            }
        }
        return false
    }

    class Factory(
        private val app: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EqualizerActivityViewModel(app) as T
        }
    }

    companion object {
        const val TAG = "EqualizerActivityViewModel"
    }
}