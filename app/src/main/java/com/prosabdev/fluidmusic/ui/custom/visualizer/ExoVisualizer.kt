package com.prosabdev.fluidmusic.ui.custom.visualizer

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * The visualizer is a view which listens to the FFT changes and forwards it to the band view.
 */
class ExoVisualizer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), FFTAudioProcessor.FFTListener {

    var processor: FFTAudioProcessor? = null

    private var mCurrentWaveform: FloatArray? = null

    private val mBandView = FFTBandView(context, attrs)

    init {
        addView(mBandView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun updateProcessorListenerState(enable: Boolean) {
        if (enable) {
            processor?.listener = this
        } else {
            processor?.listener = null
            mCurrentWaveform = null
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateProcessorListenerState(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updateProcessorListenerState(false)
    }

    override fun onFFTReady(sampleRateHz: Int, channelCount: Int, fft: FloatArray) {
        mCurrentWaveform = fft
        mBandView.onFFT(fft)
    }

}