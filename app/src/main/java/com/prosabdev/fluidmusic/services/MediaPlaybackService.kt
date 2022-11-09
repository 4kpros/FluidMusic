package com.prosabdev.fluidmusic.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY
import android.media.AudioManager.STREAM_MUSIC
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.provider.CallLog.Calls.PRIORITY_NORMAL
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.QueueItem
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.lang.ref.WeakReference

private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private val mIntentFilterAudioBecomingNoisy = IntentFilter(ACTION_AUDIO_BECOMING_NOISY)
    private lateinit var mNotificationManager: NotificationManager
    private var mMediaSession: MediaSessionCompat? = null
    private lateinit var mStateBuilder: PlaybackStateCompat.Builder
    private var mHandlerThread : HandlerThread? = null
    private var mCustomPlayerHandler : CustomPlayerHandler? = null
    private lateinit var mMediaPlayer: CustomMediaPlayer

    private lateinit var mAudioFocusRequest: AudioFocusRequest

    private var mOnAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = object :
        AudioManager.OnAudioFocusChangeListener{
        override fun onAudioFocusChange(audioFocusChange: Int) {
            Log.i(ConstantValues.TAG, "onAudioFocusChange $audioFocusChange")
        }
    }
    private val mBecomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.i(ConstantValues.TAG, "BroadcastReceiver mBecomingNoisyReceiver")
        }
    }
    private val mCallback = object: MediaSessionCompat.Callback() {

        override fun onPrepare() {
            super.onPrepare()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            onPlayFrom(extras)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            onPlayFrom(extras)
        }

        private fun onPlayFrom(extras: Bundle?) {
            if (extras != null) {
                setupMediaSessionWithBundleData(extras)
            }
            if(requestAudioFocus()){
                startService(Intent(applicationContext, MediaBrowserService::class.java))

                createNotification()
                registerReceivers()
                setupMediaPlayer(extras!!)
            }
        }
        fun createNotification(){
            val controller = mMediaSession?.controller
            val mediaMetadata = controller?.metadata
            val description = mediaMetadata?.description

            val builder = androidx.core.app.NotificationCompat.Builder(applicationContext, ConstantValues.CHANNEL_ID).apply {
                setStyle(
                    NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession?.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                applicationContext,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                )

                //Add the metadata for the currently playing track
                setContentTitle(description?.title ?: applicationContext.getString(R.string.unknown_title))
                setContentText(description?.subtitle ?: applicationContext.getString(R.string.unknown_artist))
                setSubText("0/0")
                setLargeIcon(description?.iconBitmap)

                //Enable launching the player by clicking the notification
                setContentIntent(controller?.sessionActivity)

                //Stop the service when the notification is swiped away
                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        applicationContext,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                priority = androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
                setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)

                setSmallIcon(R.drawable.ic_fluid_music_icon)

                addAction(
                    androidx.core.app.NotificationCompat.Action(
                        R.drawable.skip_previous,
                        getString(R.string.previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            applicationContext,
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        )
                    )
                )
                addAction(
                    androidx.core.app.NotificationCompat.Action(
                        R.drawable.pause,
                        getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            applicationContext,
                            PlaybackStateCompat.ACTION_PLAY_PAUSE
                        )
                    )
                )
                addAction(
                    androidx.core.app.NotificationCompat.Action(
                        R.drawable.skip_next,
                        getString(R.string.next),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            applicationContext,
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        )
                    )
                )
                addAction(
                    androidx.core.app.NotificationCompat.Action(
                        R.drawable.close,
                        getString(R.string.close),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            applicationContext,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
                )
            }
            startForeground(ConstantValues.NOTIFICATION_REQUEST_CODE, builder.build())
        }

        fun setupMediaPlayer(extras : Bundle){
            val currentSongPath : String? = mMediaPlayer.setupQueueListExtras(extras)

            if(currentSongPath != null && currentSongPath.isNotEmpty())
                if(mMediaPlayer.prepareMediaPlayerWithPath(currentSongPath))
                    mMediaPlayer.playMediaPlayer()
        }
        fun registerReceivers() {
            registerReceiver(mBecomingNoisyReceiver, mIntentFilterAudioBecomingNoisy)
        }
        fun setupMediaSessionWithBundleData(extras: Bundle) {
            val tempShuffle : Int = extras.getInt(ConstantValues.BUNDLE_SHUFFLE_VALUE, 0)
            val tempRepeat : Int = extras.getInt(ConstantValues.BUNDLE_REPEAT_VALUE, 0)
            val tempCurrentSongMetaData : MediaMetadataCompat? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(ConstantValues.BUNDLE_CURRENT_SONG_META_DATA, MediaMetadataCompat::class.java)
            } else {
                extras.getParcelable(ConstantValues.BUNDLE_CURRENT_SONG_META_DATA)
            }
            mMediaSession?.setRepeatMode(tempRepeat)
            mMediaSession?.setShuffleMode(tempShuffle)
            mMediaSession?.setMetadata(tempCurrentSongMetaData)
            mMediaSession?.isActive = true
        }

        fun requestAudioFocus(): Boolean {
            var result : Boolean = false
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                    setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                    setAudioAttributes(AudioAttributes.Builder().run {
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        build()
                    })
                    build()
                }
            }
            val audioFocusResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(mAudioFocusRequest)
            } else {
                audioManager.requestAudioFocus(mOnAudioFocusChangeListener, STREAM_MUSIC, 1)
            }
            if (audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                result = true
            }
            return result
        }

        override fun onPlay() {
            requestAudioFocus()
        }

        override fun onStop() {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Abandon audio focus
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(mAudioFocusRequest)
            }else{
                audioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
            }
            unregisterReceiver(mBecomingNoisyReceiver)
            // Stop the service
            stopSelf()
            // Set the session inactive  (and update metadata and state)
            mMediaSession?.isActive = false
            // stop the player (custom call)
            mMediaPlayer.stopMediaPlayer()
            // Take the service out of the foreground
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }else{
                stopForeground(false)
            }
        }

        override fun onPause() {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Update metadata and state
            // pause the player (custom call)
            mMediaPlayer.pauseMediaPlayer()
            // unregister BECOME_NOISY BroadcastReceiver
            unregisterReceiver(mBecomingNoisyReceiver)
            // Take the service out of the foreground, retain the notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }else{
                stopForeground(false)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        setupMediaSession()
        setupPlayerHandler()
        setupNotificationChannel()
    }

    private fun setupPlayerHandler() {
        mHandlerThread = HandlerThread(ConstantValues.HANDLER_THREAD, Thread.NORM_PRIORITY)
        mHandlerThread?.start()
        mCustomPlayerHandler = CustomPlayerHandler(mHandlerThread?.looper!!, WeakReference(this))
        mMediaPlayer = CustomMediaPlayer(WeakReference(this))
        mMediaPlayer.setHandler(mCustomPlayerHandler!!)
    }

    private fun setupMediaSession() {
        mMediaSession = MediaSessionCompat(applicationContext, ConstantValues.TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            mStateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(mStateBuilder.build())
            setCallback(mCallback)
            setSessionToken(sessionToken)
        }
    }
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.musicplayer)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(ConstantValues.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (allowBrowsing(clientPackageName, clientUid)) {
            Log.i(ConstantValues.TAG, "onGetRoot")
            Log.i(ConstantValues.TAG, "onGetRoot : $clientPackageName")
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            MediaBrowserServiceCompat.BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierarchy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            MediaBrowserServiceCompat.BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }
    }
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.i(ConstantValues.TAG, "onLoadChildren")
        Log.i(ConstantValues.TAG, "onLoadChildren : $parentId")
        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }
        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()

        if (MY_MEDIA_ROOT_ID == parentId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems as MutableList<MediaBrowserCompat.MediaItem>?)
    }
    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        var result = true
        Log.i(ConstantValues.TAG, "allowBrowsing clientPackageName = $clientPackageName, clientUid = $clientUid")
        return result
    }
}