package com.prosabdev.fluidmusic.services

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.lang.ref.WeakReference


class CustomMediaPlayer(private var mMediaPlaybackService : WeakReference<MediaPlaybackService>) : MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private var mMediaPlayer: MediaPlayer? = MediaPlayer()
    private var mIsMediaPlayerPrepared = false
    private var mHandler: Handler? = null
    private var mVolume = 0.0f

    fun setHandler(handler: Handler) {
        mHandler = handler
    }

    fun setupQueueListExtras(extras: Bundle): String? {
        val tempQueueList : ArrayList<String>? = extras.getStringArrayList(ConstantValues.BUNDLE_QUEUE_LIST)
        val tempSourceFrom : String? = extras.getString(ConstantValues.BUNDLE_SOURCE_FROM, null)
        val tempSourceFromValue : String? = extras.getString(ConstantValues.BUNDLE_SOURCE_FROM_VALUE, null)
        val tempCurrentSongIndex : Int = extras.getInt(ConstantValues.BUNDLE_CURRENT_SONG_ID, -1)
        return if(tempQueueList == null || tempQueueList.isEmpty() || tempCurrentSongIndex < 0) null else tempQueueList[tempCurrentSongIndex]
    }

    fun prepareMediaPlayerWithPath(path: String): Boolean {
        mIsMediaPlayerPrepared = tryToPrepareMediaPlayer(path)
        return mIsMediaPlayerPrepared
    }
    private fun tryToPrepareMediaPlayer(path: String): Boolean {
        if (path.isNotEmpty()) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(path)
//            setupAudioAttributes()
            try {
                mMediaPlayer!!.prepare()
            } finally {
                mMediaPlayer!!.setOnErrorListener(this)
                mMediaPlayer!!.setOnCompletionListener(this)
            }
            return true
        }
        return false
    }

    fun playMediaPlayer(): Boolean {
        var result = false
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer!!.start()
                mMediaPlayer!!.setOnCompletionListener(this)
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
                mMediaPlayer!!.stop()
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
                mMediaPlayer!!.pause()
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
                mMediaPlayer!!.release()
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
            mMediaPlayer!!.isPlaying
        } else false
    }

    fun getDuration(): Long {
        var result: Long = -1
        if (mMediaPlayer != null && isMediaPlayerPrepared()) {
            try {
                result = mMediaPlayer!!.duration.toLong()
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
                result = mMediaPlayer!!.currentPosition.toLong()
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
                mMediaPlayer!!.setVolume(volume, volume)
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
                mMediaPlayer!!.seekTo(whereToSeek.toInt())
                result = true
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        Log.i(ConstantValues.TAG, "onCompletion")
    }

    override fun onError(mediaPlayer: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.i(ConstantValues.TAG, "onError")
        return false
    }
}