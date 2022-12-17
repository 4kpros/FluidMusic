package com.prosabdev.fluidmusic.service

import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.lang.ref.WeakReference


class CustomPlayerHandler(private val mLooper: Looper, private val mService : WeakReference<MediaPlaybackService>) : Handler(mLooper) {
    private val mVolume = 1.0f

    override fun handleMessage(msg: Message) {

        val service: MediaPlaybackService = mService.get() ?: return

        Log.i(ConstantValues.TAG, "handleMessage $service")
        synchronized(service) {
            when (msg.what) {
                ConstantValues.FADE_UP ->
                    Log.i(ConstantValues.TAG, "FADE_UP")
                ConstantValues.FADE_DOWN ->
                    Log.i(ConstantValues.TAG, "FADE_DOWN")
                ConstantValues.FOCUS_CHANGE -> {
                    Log.i(ConstantValues.TAG, "FOCUS_CHANGE")
                    when (msg.arg1) {
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                            Log.i(ConstantValues.TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                        }
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            Log.i(ConstantValues.TAG, "AUDIOFOCUS_LOSS")
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                            Log.i(ConstantValues.TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                        }
                        AudioManager.AUDIOFOCUS_GAIN ->
                            Log.i(ConstantValues.TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                        else -> {}
                    }
                }
                else -> {}
            }
        }
        super.handleMessage(msg)
    }
}