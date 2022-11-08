package com.prosabdev.fluidmusic.services

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.Log
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.lang.ref.WeakReference


class CustomMediaPlayer() : MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private val mMediaPlayer: MediaPlayer? = MediaPlayer()
    private var mIsMediaPlayerPrepared = false
    private var mHandler: Handler? = null
    private var mVolume = 0.0f

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        Log.i(ConstantValues.TAG, "onCompletion")
    }

    override fun onError(mediaPlayer: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.i(ConstantValues.TAG, "onError")
        return false
    }

    fun setHandler(handler: Handler?) {
        mHandler = handler
    }
    fun onPrepareMediaPlayer(path: String = "") {
        mIsMediaPlayerPrepared = prepareMediaPlayer(mMediaPlayer)
    }

    private fun prepareMediaPlayer(mediaPlayer: MediaPlayer?, path: String = ""): Boolean {
        if (mediaPlayer != null && path.isNotEmpty()) {
            mediaPlayer.reset()
            if (path.isNotEmpty()) {
                mediaPlayer.setDataSource(path)
            }
            setupAudioAttributes(mediaPlayer)
            try {
                mediaPlayer.prepare()
            } finally {
                mediaPlayer.setOnErrorListener(this)
                mediaPlayer.setOnCompletionListener(this)
            }
            return true
        }
        return false
    }

    private fun setupAudioAttributes(mediaPlayer: MediaPlayer) {
        try {
            mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun startMediaPlayer(): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.start()
                mMediaPlayer.setOnCompletionListener(this)
                result = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun stopMediaPlayer(): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop()
                result = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun pauseMediaPlayer(): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.pause()
                result = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun releaseMediaPlayer(): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release()
                mIsMediaPlayerPrepared = false
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun isMediaPlayerPrepared(): Boolean {
        return mIsMediaPlayerPrepared
    }

    fun isPlaying(): Boolean {
        return if (mMediaPlayer != null && isMediaPlayerPrepared()) {
            mMediaPlayer.isPlaying
        } else false
    }

    fun getDuration(): Long {
        var result: Long = -1
        if (mMediaPlayer != null && isMediaPlayerPrepared()) {
            try {
                result = mMediaPlayer.duration.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun getProgressValue(): Long {
        var result: Long = -1
        if (mMediaPlayer != null && isMediaPlayerPrepared()) {
            try {
                result = mMediaPlayer.currentPosition.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun setVolume(volume: Float): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setVolume(volume, volume)
                mVolume = volume
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun seekTo(whereToSeek: Long): Boolean {
        var result = false
        if (mMediaPlayer != null && isMediaPlayerPrepared()) {
            try {
                mMediaPlayer.seekTo(whereToSeek.toInt())
                result = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return result
    }
}