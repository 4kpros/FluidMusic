package com.prosabdev.fluidmusic.media

import android.content.Intent
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Rating
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.prosabdev.fluidmusic.ui.custom.visualizer.FFTAudioProcessor

@UnstableApi class PlaybackService : MediaLibraryService() {
    private var mMediaLibrarySession: MediaLibrarySession? = null
    private var mPlayer: ExoPlayer? = null
    private var mEqualizer: Equalizer? = null
    private val mFftAudioProcessor = FFTAudioProcessor()
    private var mRenderersFactory: DefaultRenderersFactory? = null

    // Create player and media session in the onCreate lifecycle event
    @UnstableApi override fun onCreate() {
        super.onCreate()
        try {
            mRenderersFactory = EqualizerBuilder.initFFTransform(this@PlaybackService, mFftAudioProcessor)
            mRenderersFactory?.let { renderersFactory ->
                mPlayer = ExoPlayer.Builder(this@PlaybackService, renderersFactory)
                    .build()
                mEqualizer = Equalizer(0, mPlayer!!.audioSessionId)
            }
        }catch (error: Throwable){
            error.printStackTrace()
        }
        mMediaLibrarySession = MediaLibrarySession.Builder(this@PlaybackService, mPlayer!!, MyCallback()).build()
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (mPlayer!!.playWhenReady || mPlayer!!.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    //Provide access to the media session to other clients
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mMediaLibrarySession
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        mMediaLibrarySession?.run {
            player.release()
            release()
            mMediaLibrarySession = null
        }
        super.onDestroy()
    }

    private inner class MyCallback : MediaLibrarySession.Callback {

        private val TAG = "MyCallback"

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Log.d(TAG, "onConnect")
            // Default commands with default custom layout for all controllers.
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            Log.d(TAG, "onGetChildren")
            return super.onGetChildren(session, browser, parentId, page, pageSize, params)
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            Log.d(TAG, "onAddMediaItems")
            return super.onAddMediaItems(mediaSession, controller, mediaItems)
        }

        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            Log.d(TAG, "onSetMediaItems")
            return super.onSetMediaItems(
                mediaSession,
                controller,
                mediaItems,
                startIndex,
                startPositionMs
            )
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            Log.d(TAG, "onPlaybackResumption")
            return super.onPlaybackResumption(mediaSession, controller)
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            Log.d(TAG, "onDisconnected")
            super.onDisconnected(session, controller)
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            Log.d(TAG, "onSubscribe")
            return super.onSubscribe(session, browser, parentId, params)
        }

        override fun onUnsubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String
        ): ListenableFuture<LibraryResult<Void>> {
            Log.d(TAG, "onUnsubscribe")
            return super.onUnsubscribe(session, browser, parentId)
        }

        override fun onSearch(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            Log.d(TAG, "onSearch")
            return super.onSearch(session, browser, query, params)
        }

        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            Log.d(TAG, "onMediaButtonEvent")
            return super.onMediaButtonEvent(session, controllerInfo, intent)
        }

        override fun onSetRating(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaId: String,
            rating: Rating
        ): ListenableFuture<SessionResult> {
            Log.d(TAG, "onSetRating")
            return super.onSetRating(session, controller, mediaId, rating)
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            Log.d(TAG, "onCustomCommand")
            return super.onCustomCommand(session, controller, customCommand, args)
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetItem")
            return super.onGetItem(session, browser, mediaId)
        }

        override fun onGetSearchResult(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            Log.d(TAG, "onGetSearchResult")
            return super.onGetSearchResult(session, browser, query, page, pageSize, params)
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetLibraryRoot")
            return super.onGetLibraryRoot(session, browser, params)
        }
    }

    companion object {
        const val TAG = "PlaybackService"

        private const val ACTION_FFT_AUDIO_PROCESSING = "ACTION_FFT_AUDIO_PROCESSING"
    }
}
