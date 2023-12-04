package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import android.content.Context
import android.media.audiofx.Equalizer
import android.os.Handler
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import com.prosabdev.common.models.equalizer.EqualizerPresetItem
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.ui.custom.visualizer.ExoVisualizer
import com.prosabdev.fluidmusic.ui.custom.visualizer.FFTAudioProcessor
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.launch

@UnstableApi class EqualizerActivityViewModel(app: Application) : AndroidViewModel(app)  {
    //Equalizer bands
    val equalizerState = MutableLiveData<Boolean>(null)
    val currentPreset = MutableLiveData<CharSequence?>(null)
    val presetBandsLevels = MutableLiveData<Array<Short>?>(null)
    val customPresets = MutableLiveData<List<EqualizerPresetItem>?>(null)
    //Tone
    val toneState = MutableLiveData<Boolean>(null)
    val bassBoostProgress = MutableLiveData<Int>(null)
    val visualizerProgress = MutableLiveData<Int>(null)
    val volumeProgress = MutableLiveData<Int>(null)

    private var mEqualizer: Equalizer? = null

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

    fun stopEqualizer() {
        //
    }

    fun setEqualizerState(value: Boolean){
        viewModelScope.launch {
            mEqualizer?.enabled = value
            equalizerState.value = value
        }
    }

    fun setCurrentPreset(preset: CharSequence?, viewModel: EqualizerPresetBandLevelItemViewModel?){
        viewModelScope.launch {
            currentPreset.value = preset
            if(preset.isNullOrEmpty()) return@launch

            if(viewModel == null) return@launch
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
        viewModelScope.launch {
            presetBandsLevels.value = tempPresetBandsLevels
        }
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
        Log.i("TAG", "CHECK ON CUSTOM PRESETS")
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
}