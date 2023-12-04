package com.prosabdev.fluidmusic.media

import android.content.Context
import android.os.Handler
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import com.prosabdev.fluidmusic.ui.custom.visualizer.FFTAudioProcessor

object EqualizerBuilder {
    @OptIn(UnstableApi::class) fun initFFTransform(ctx: Context, fftAudioProcessor: FFTAudioProcessor): DefaultRenderersFactory {
        val renderersFactory: DefaultRenderersFactory?
        renderersFactory = object : DefaultRenderersFactory(ctx) {
            override fun buildAudioRenderers(
                context: Context,
                extensionRendererMode: Int,
                mediaCodecSelector: MediaCodecSelector,
                enableDecoderFallback: Boolean,
                audioSink: AudioSink,
                eventHandler: Handler,
                eventListener: AudioRendererEventListener,
                out: ArrayList<Renderer>
            ) {
                out.add(
                    MediaCodecAudioRenderer(
                        context,
                        mediaCodecSelector,
                        eventHandler,
                        eventListener,
                        DefaultAudioSink.Builder(ctx).build().apply {
                            arrayListOf(fftAudioProcessor)
                        }
                    )
                )

                super.buildAudioRenderers(
                    context,
                    extensionRendererMode,
                    mediaCodecSelector,
                    enableDecoderFallback,
                    audioSink,
                    eventHandler,
                    eventListener,
                    out
                )
            }
        }
        return renderersFactory
    }
}