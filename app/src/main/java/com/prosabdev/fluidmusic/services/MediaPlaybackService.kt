package com.prosabdev.fluidmusic.services

import android.app.Notification
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
import android.media.session.MediaSession
import android.os.Build
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.ConstantValues

private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private val mIntentFilterAudioBecomingNoisy = IntentFilter(ACTION_AUDIO_BECOMING_NOISY)
    private var mMediaSession: MediaSessionCompat? = null
    private lateinit var mStateBuilder: PlaybackStateCompat.Builder
    private var mMediaPlayer: CustomMediaPlayer = CustomMediaPlayer()

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
        override fun onPlay() {
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
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(mAudioFocusRequest)
            } else {
                audioManager.requestAudioFocus(mOnAudioFocusChangeListener, STREAM_MUSIC, 1)
            }
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                startService(Intent(applicationContext, MediaBrowserService::class.java))
                mMediaSession?.isActive = true
//                mMediaPlayer.startMediaPlayer()
                registerReceiver(mBecomingNoisyReceiver, mIntentFilterAudioBecomingNoisy)
                val builder = createNotification()
                startForeground(ConstantValues.NOTIFICATION_REQUEST_CODE, builder.build())
            }
        }

        public override fun onStop() {
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

        public override fun onPause() {
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

        private fun createNotification(): androidx.core.app.NotificationCompat.Builder {
            val controller = mMediaSession?.controller
            val mediaMetadata = controller?.metadata
            val description = mediaMetadata?.description

            val builder = androidx.core.app.NotificationCompat.Builder(applicationContext, ConstantValues.CHANNEL_ID).apply {
                setStyle(
                    NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession?.sessionToken)
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                applicationContext,
                                PlaybackStateCompat.ACTION_STOP
                            )
                        )
                )

                //Add the metadata for the currently playing track
                setContentTitle(description?.title)
                setContentText(description?.subtitle)
                setSubText(description?.description)
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
                priority = androidx.core.app.NotificationCompat.PRIORITY_LOW
                setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)

                setSmallIcon(R.drawable.ic_fluid_music_icon_with_padding)
//                color = ContextCompat.getColor(applicationContext, androidx.appcompat.R.color.primary_dark_material_dark)
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
                        R.drawable.skip_previous,
                        getString(R.string.previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            applicationContext,
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        )
                    )
                )
            }
            return builder
        }
    }

    override fun onCreate() {
        super.onCreate()
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
        setupNotificationChannel()
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.fluidmusic)
            val descriptionText = getString(R.string.musicplayer)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(ConstantValues.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (allowBrowsing(clientPackageName, clientUid)) {
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
        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }
        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()

        if (MY_MEDIA_ROOT_ID == parentId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
            Log.i(ConstantValues.TAG, "MY_MEDIA_ROOT_ID != parentId")
            Log.i(ConstantValues.TAG, "MY_MEDIA_ROOT_ID : $parentId")
            Log.i(ConstantValues.TAG, "onLoadChildren : $result")
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
            Log.i(ConstantValues.TAG, "MY_MEDIA_ROOT_ID == parentId")
            Log.i(ConstantValues.TAG, "MY_MEDIA_ROOT_ID : $parentId")
            Log.i(ConstantValues.TAG, "onLoadChildren : $result")
        }
        result.sendResult(mediaItems as MutableList<MediaBrowserCompat.MediaItem>?)
    }
    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        var result = true
        Log.i(ConstantValues.TAG, "allowBrowsing clientPackageName = $clientPackageName, clientUid = $clientUid")
        return result
    }
}