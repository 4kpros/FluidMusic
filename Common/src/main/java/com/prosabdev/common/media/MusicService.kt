package com.prosabdev.common.media

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.Util.constrainValue
import com.google.android.gms.cast.framework.CastContext
import com.prosabdev.common.R
import com.prosabdev.common.extensions.*
import com.prosabdev.common.library.MusicSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

open class MusicService : MediaBrowserServiceCompat() {

    private lateinit var notificationManager: FluidMusicNotificationManager
    private lateinit var mediaSource: MusicSource
    private lateinit var packageValidator: PackageValidator

    // The current player will either be an ExoPlayer or a CastPlayer
    private lateinit var currentPlayer: Player

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
    private var currentMediaItemIndex: Int = 0

    private var isForegroundService: Boolean = false

    private val fluidMusicAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
//        .setFlags(C.FLAG_AUDIBILITY_ENFORCED) //Note this flag should only be used for sounds subject to regulatory behaviors in some countries, such as for camera shutter sound, and not for routing behaviors.
        .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
//        .setSpatializationBehavior(C.SPATIALIZATION_BEHAVIOR_AUTO)
        .build()

    private val playerListener = PlayerEventListener()

    /**
     * Configure ExoPlayer to handle audio focus for us.
     */
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(fluidMusicAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    /**
     * If Cast is available, create a CastPlayer to handle communication with a Cast session.
     */
    private val castPlayer: CastPlayer? by lazy {
        try {
            val castContext = CastContext.getSharedInstance(this)
            CastPlayer(castContext, CastMediaItemConverter()).apply {
                setSessionAvailabilityListener(FluidMusicCastSessionAvailabilityListener())
                addListener(playerListener)
            }
        } catch (error : Throwable) {
            error.printStackTrace()
            null
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }
        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, TAG)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
        sessionToken = mediaSession.sessionToken
        // Create notification manager.
        notificationManager = FluidMusicNotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )
        // Load the media library out of main thread
        serviceScope.launch {
//            mediaSource.load()
        }
        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(FluidMusicPlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(FluidMusicQueueNavigator(mediaSession))
        // Switch to new player(try to user castPlayer first else use exoPlayer.
        switchToPlayer(
            previousPlayer = null,
            newPlayer = if (castPlayer?.isCastSessionAvailable == true) castPlayer!! else exoPlayer
        )
        // Show notification.
        notificationManager.showNotificationForPlayer(currentPlayer)
        // Create package validator.
        packageValidator = PackageValidator(this, R.xml.allowed_media_browser_callers)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
//        /**
//         * By stopping playback, the player will transition to [Player.STATE_IDLE] triggering
//         * [Player.EventListener.onPlayerStateChanged] to be called. This will cause the
//         * notification to be hidden and trigger
//         * [PlayerNotificationManager.NotificationListener.onNotificationCancelled] to be called.
//         * The service will then remove itself as a foreground service, and will call
//         * [stopSelf].
//         */
//        currentPlayer.stop()
    }

    override fun onDestroy() {
        // Stop media session.
        mediaSession.run {
            isActive = false
            release()
        }
        // Cancel coroutines when the service is going away.
        serviceJob.cancel()
        // Free ExoPlayer resources.
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItem]s to browse/play.
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        /*
         * By default, all known clients are permitted to search, but only tell unknown callers
         * about search if permitted by the [BrowseTree].
         */
//        val isKnownCaller = packageValidator.isKnownCaller(clientPackageName, clientUid)
//        val rootExtras = Bundle().apply {
//            putBoolean(
//                MEDIA_SEARCH_SUPPORTED,
//                isKnownCaller || browseTree.searchableByUnknownCaller
//            )
//            putBoolean(CONTENT_STYLE_SUPPORTED, true)
//            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
//            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST)
//        }
        return null
//        return if (isKnownCaller) {
//            /**
//             * By default return the browsable root. Treat the EXTRA_RECENT flag as a special case
//             * and return the recent root instead.
//             */
//            val isRecentRequest = rootHints?.getBoolean(EXTRA_RECENT) ?: false
//            val browserRootPath = if (isRecentRequest) FluidMusic_RECENT_ROOT else FluidMusic_BROWSABLE_ROOT
//            BrowserRoot(browserRootPath, rootExtras)
//        } else {
//            /**
//             * Unknown caller. There are two main ways to handle this:
//             * 1) Return a root without any content, which still allows the connecting client
//             * to issue commands.
//             * 2) Return `null`, which will cause the system to disconnect the app.
//             *
//             * FluidMusic takes the first approach for a variety of reasons, but both are valid
//             * options.
//             */
//            BrowserRoot(FluidMusic_EMPTY_ROOT, rootExtras)
//        }
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentMediaId]. See [BrowseTree] for more details on
     * how this is build/more details about the relationships.
     */
    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaItem>>
    ) {
        Log.i(TAG, "Player onLoadChildren")
        /**
         * If the caller requests the recent root, return the most recently played song.
         */
//        if (parentMediaId == FLUID_MUSIC_RECENT_ROOT) {
//            result.sendResult(storage.loadRecentSong()?.let { song -> listOf(song) })
//        } else {
//            // If the media source is ready, the results will be set synchronously here.
//            val resultsSent = mediaSource.whenReady { successfullyInitialized ->
//                if (successfullyInitialized) {
//                    val children = browseTree[parentMediaId]?.map { item ->
//                        MediaItem(item.description, item.flag)
//                    }
//                    result.sendResult(children)
//                } else {
//                    mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
//                    result.sendResult(null)
//                }
//            }
//
//            // If the results are not ready, the service must "detach" the results before
//            // the method returns. After the source is ready, the lambda above will run,
//            // and the caller will be notified that the results are ready.
//            //
//            // See [MediaItemFragmentViewModel.subscriptionCallback] for how this is passed to the
//            // UI/displayed in the [RecyclerView].
//            if (!resultsSent) {
//                result.detach()
//            }
//        }
    }

    /**
     * Returns a list of [MediaItem]s that match the given search query
     */
    override fun onSearch(
        query: String,
        extras: Bundle?,
        result: Result<List<MediaItem>>
    ) {
        Log.i(TAG, "Player onSearch: query $query")
        val resultsSent = mediaSource.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val resultsList = mediaSource.search(query, extras ?: Bundle.EMPTY)
                    .map { mediaMetadata ->
                        MediaItem(mediaMetadata.description, mediaMetadata.flag)
                    }
                result.sendResult(resultsList)
            }
        }
        if (!resultsSent) {
            result.detach()
        }
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList

        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.stop()
        // Set playlist and prepare.
        currentPlayer.setMediaItems(
            metadataList.map { it.toMediaItem() }, initialWindowIndex, playbackStartPositionMs)
        currentPlayer.prepare()
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.clearMediaItems()
                currentPlayer.stop()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    metadataList = currentPlaylistItems,
                    itemToPlay = currentPlaylistItems[currentMediaItemIndex],
                    playWhenReady = previousPlayer.playWhenReady,
                    playbackStartPositionMs = previousPlayer.currentPosition
                )
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop()
    }

    private inner class FluidMusicCastSessionAvailabilityListener : SessionAvailabilityListener {
        override fun onCastSessionAvailable() {
            Log.i(TAG, "Player onCastSessionAvailable")
            switchToPlayer(currentPlayer, castPlayer!!)
        }
        override fun onCastSessionUnavailable() {
            Log.i(TAG, "Player onCastSessionUnavailableIndex")
            switchToPlayer(currentPlayer, exoPlayer)
        }
    }

    private inner class FluidMusicQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            Log.i(TAG, "Player getMediaDescription: windowIndex $windowIndex")
            if (windowIndex < currentPlaylistItems.size) {
                return currentPlaylistItems[windowIndex].description
            }
            return MediaDescriptionCompat.Builder().build()
        }
    }

    private inner class FluidMusicPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        /**
         * FluidMusic supports preparing (and playing) from search, as well as media ID, so those
         * capabilities are declared here.
         */
        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE or
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
            PlaybackStateCompat.ACTION_PREPARE_FROM_URI or

            PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
            PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
            PlaybackStateCompat.ACTION_PLAY_FROM_URI or

            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_STOP or

            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or

            PlaybackStateCompat.ACTION_SEEK_TO or

            PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or

            PlaybackStateCompat.ACTION_SET_PLAYBACK_SPEED or
            PlaybackStateCompat.ACTION_FAST_FORWARD or
            PlaybackStateCompat.ACTION_REWIND or
            PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED or
            PlaybackStateCompat.ACTION_SET_RATING


            override fun onPrepare(playWhenReady: Boolean) {
            Log.i(TAG, "Player onPrepare: playWhenReady $playWhenReady")
//            val recentSong = storage.loadRecentSong() ?: return
//            onPrepareFromMediaId(
//                recentSong.mediaId!!,
//                playWhenReady,
//                recentSong.description.extras
//            )
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.i(TAG, "Player onPrepareFromMediaId: mediaId $mediaId, playWhenReady $playWhenReady, extras $extras")
            mediaSource.whenReady {
                val itemToPlay: MediaMetadataCompat? = mediaSource.find { item ->
                    item.id == mediaId
                }
                if (itemToPlay == null) {
                    Log.w(TAG, "Content not found: MediaID=$mediaId")
                    // TODO: Notify caller of the error.
                } else {

                    val playbackStartPositionMs =
                        extras?.getLong(MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS, C.TIME_UNSET)
                            ?: C.TIME_UNSET

                    preparePlaylist(
                        buildPlaylist(itemToPlay),
                        itemToPlay,
                        playWhenReady,
                        playbackStartPositionMs
                    )
                }
            }
        }

        /**
         * This method is used by the Google Assistant to respond to requests such as:
         * - Play Geisha from Wake Up on FluidMusic
         * - Play electronic music on FluidMusic
         * - Play music on FluidMusic
         */
        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            Log.i(TAG, "Player onPrepareFromSearch: query $query, playWhenReady $playWhenReady, extras $extras")
            mediaSource.whenReady {
                val metadataList = mediaSource.search(query, extras ?: Bundle.EMPTY)
                if (metadataList.isNotEmpty()) {
                    preparePlaylist(
                        metadataList,
                        metadataList[0],
                        playWhenReady,
                        playbackStartPositionMs = C.TIME_UNSET
                    )
                }
            }
        }
        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?){
            Log.i(TAG, "Player onPrepareFromUri: ongoing $uri, playWhenReady $playWhenReady, extras $extras")
        }
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            Log.i(TAG, "Player onCommand: command $command, extras $extras")
            return true
        }

        /**
         * Builds a playlist based on a [MediaMetadataCompat]
         */
        private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
            mediaSource.filter { it.album == item.album }.sortedBy { it.trackNumber }
    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            Log.i(TAG, "Player onNotificationPosted: ongoing $ongoing")
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            Log.i(TAG, "Player onNotificationCancelled: dismissedByUser $dismissedByUser")
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    /**
     * Listen for events from ExoPlayer.
     */
    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.i(TAG, "Player onPlaybackStateChanged: playbackState $playbackState")
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(currentPlayer)
                    if (playbackState == Player.STATE_READY) {
                        // When playing/paused save the current media item in persistent
                        // storage so that playback can be resumed between device reboots.
                        // Search for "media resumption" for more information.
//                        saveRecentSongToStorage()
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            Log.i(TAG, "Player onPlayWhenReadyChanged: playWhenReady $playWhenReady, reason $reason")
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.i(TAG, "Player onIsPlayingChanged: isPlaying $isPlaying")
        }
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            Log.i(TAG, "Player onPlaybackParametersChanged: playbackParameters $playbackParameters")
        }
        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            Log.i(TAG, "Player onPlaylistMetadataChanged: mediaMetadata $mediaMetadata")
        }
        override fun onEvents(player: Player, events: Player.Events) {
            Log.i(TAG, "Player onEvents: $events")
            if (events.contains(EVENT_POSITION_DISCONTINUITY)
                || events.contains(EVENT_MEDIA_ITEM_TRANSITION)
                || events.contains(EVENT_PLAY_WHEN_READY_CHANGED)) {
                currentMediaItemIndex = if (currentPlaylistItems.isNotEmpty()) {
                    constrainValue(
                        player.currentMediaItemIndex,
                        0,
                        currentPlaylistItems.size - 1
                    )
                } else 0
            }
        }
        override fun onPlayerError(error: PlaybackException) {
            var message = R.string.generic_error;
            Log.e(TAG, "Player error: " + error.errorCodeName + " (" + error.errorCode + ")")
            if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
                || error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND) {
                message = R.string.error_media_not_found
            }
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        /** Media session events */
        const val NETWORK_FAILURE = "NETWORK_FAILURE"

        /** Content styling constants */
        private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
        private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
        private const val CONTENT_STYLE_LIST = 1
        private const val CONTENT_STYLE_GRID = 2

        private const val USER_AGENT = "USER_AGENT"

        const val MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS = "playback_start_position_ms"

        private const val TAG = "MusicService"
    }
}
