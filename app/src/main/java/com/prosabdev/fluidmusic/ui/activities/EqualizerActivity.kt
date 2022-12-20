package com.prosabdev.fluidmusic.ui.activities

import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityEqualizerBinding
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class EqualizerActivity : AppCompatActivity() {
    private lateinit var mDataBidingView : ActivityEqualizerBinding

    private var mEqualizer: Equalizer? = null
    private var mBands: ArrayList<Int>? = null
    private var mNumberOfBands: Short? = 0
    private var mNumberOfPresets: Short? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_equalizer)

        initViews()
        initEqualizer()
        checkInteractions()
        registerOnBackPressedCallback()
    }

    private fun registerOnBackPressedCallback() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }


    private fun initEqualizer() {
        val mediaSessionId: Int = getMediaSessionId() ?: return
        mEqualizer = Equalizer(0, mediaSessionId)
        mEqualizer?.enabled = true

        setupEqualizer()
    }

    private fun setupEqualizer() {
        try {
            mEqualizer?.let { equ ->
                mNumberOfBands = equ.numberOfBands
                mNumberOfPresets = equ.numberOfPresets
                Log.i(TAG, "EQUALIZER BAND numberOfBands : ${equ.numberOfBands}")
                Log.i(TAG, "EQUALIZER BAND numberOfPresets : ${equ.numberOfPresets}")
                Log.i(TAG, "EQUALIZER BAND bandLevelRange min : ${equ.bandLevelRange[0]}")
                Log.i(TAG, "EQUALIZER BAND bandLevelRange max : ${equ.bandLevelRange[1]}")
                Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                mNumberOfBands?.let { bands ->
                    for(i in 0 until bands){
                        Log.i(TAG, "EQUALIZER BAND $i getCenterFreq : ${equ.getCenterFreq(i.toShort())}")
                        Log.i(TAG, "EQUALIZER BAND $i getBandFreqRange 0 : ${equ.getBandFreqRange(i.toShort())[0]}")
                        Log.i(TAG, "EQUALIZER BAND $i getBandFreqRange 1 : ${equ.getBandFreqRange(i.toShort())[1]}")
                        Log.i(TAG, "EQUALIZER BAND $i getBandLevel : ${equ.getBandLevel(i.toShort())}")
                        Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                    }
                }
                Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                Log.i(TAG, "EQUALIZER BAND --------------------------------->")
                mNumberOfPresets?.let { presets ->
                    for(i in 0 until presets){
                        Log.i(TAG, "EQUALIZER BAND ${i} getPresetName : ${equ.getPresetName(i.toShort())}")
                    }
                }
            }
        }catch (error: Throwable){
            error.printStackTrace()
        }
    }

    private fun setBandLevel(bandId: Short, level: Short) {
        mEqualizer?.setBandLevel(bandId, level)
    }

    private fun getMediaSessionId(): Int? {
        var mediaSessionId : Int? = null
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.second_of_silence)
            mediaPlayer.start()
            mediaSessionId = mediaPlayer.audioSessionId
        }catch (error: Throwable){
            error.printStackTrace()
        }
        return mediaSessionId
    }

    private fun checkInteractions() {
    }

    private fun initViews() {
        InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.constraintContainer)
    }

    companion object {
        const val TAG = "EqualizerActivity"
    }
}