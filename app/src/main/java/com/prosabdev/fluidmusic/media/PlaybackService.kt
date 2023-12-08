package com.prosabdev.fluidmusic.media

import android.app.PendingIntent
import android.content.Intent
import android.media.audiofx.Equalizer
import android.os.Build
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.prosabdev.fluidmusic.MainActivity
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.ui.custom.visualizer.FFTAudioProcessor

@UnstableApi
open class PlaybackService : MediaLibraryService() {

    private var mMediaLibrarySession: MediaLibrarySession? = null
    private var mPlayer: ExoPlayer? = null
    private var mEqualizer: Equalizer? = null
    private val mFftAudioProcessor = FFTAudioProcessor()
    private var mRenderersFactory: DefaultRenderersFactory? = null

    //Linked activities
    fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            IMMUTABLE_FLAG or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            addNextIntent(Intent(this@PlaybackService, EqualizerActivity::class.java))
            getPendingIntent(0, IMMUTABLE_FLAG or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    // Create player and media session in the onCreate lifecycle event
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
        setListener(MediaSessionServiceListener(this))
    }

    private fun initializeSessionAndPlayer() {
        try {
            mRenderersFactory =
                EqualizerFFTransform.initFFTransform(this@PlaybackService, mFftAudioProcessor)
            mRenderersFactory?.let { renderersFactory ->
                mPlayer = ExoPlayer.Builder(this@PlaybackService, renderersFactory)
                    .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                    .build()
                mPlayer!!.addAnalyticsListener(EventLogger())
                mEqualizer = Equalizer(0, mPlayer!!.audioSessionId)
            }
        } catch (error: Throwable) {
            error.printStackTrace()
        }
        mMediaLibrarySession =
            MediaLibrarySession.Builder(this@PlaybackService, mPlayer!!, MediaSessionCallback())
                .also { builder -> getSingleTopActivity()?.let {
                    builder.setSessionActivity(it)
                }}
                .build()
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        mPlayer?.let {
            if (it.playWhenReady || it.mediaItemCount == 0) {
                // Stop the service if not playing, continue playing in the background
                // otherwise.
                stopSelf()
            }
        }
    }

    //Provide access to the media session to other clients
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mMediaLibrarySession
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        mMediaLibrarySession?.run {
            getBackStackedActivity()?.let {
                this.setSessionActivity(it)
            }
            player.release()
            release()
            mMediaLibrarySession = null
        }
        clearListener()
        super.onDestroy()
    }

    companion object {
        const val TAG = "PlaybackService"

        const val NOTIFICATION_ID = 2023992
        const val CHANNEL_ID = "fluidmusic_notifification_channel_id_131231"

        private val IMMUTABLE_FLAG =
            if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
    }
}
