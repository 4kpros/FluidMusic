package com.prosabdev.fluidmusic.media

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSessionService
import com.prosabdev.fluidmusic.R

@UnstableApi
class MediaSessionServiceListener(private val service: PlaybackService) :
    MediaSessionService.Listener {

    /**
     * This method is only required to be implemented on Android 12 or above when an attempt is made
     * by a media controller to resume playback when the {@link MediaSessionService} is in the
     * background.
     */
    override fun onForegroundServiceStartNotAllowedException() {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(service, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notificationManagerCompat = NotificationManagerCompat.from(service)
        ensureNotificationChannel(notificationManagerCompat)
        val builder =
            NotificationCompat.Builder(service, PlaybackService.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fluid_music_icon)
                .setContentTitle(service.getString(R.string.notification_content_title))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(service.getString(R.string.notification_content_text))
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .also { builder ->
                    service.getBackStackedActivity()?.let { builder.setContentIntent(it) }
                }
        notificationManagerCompat.notify(PlaybackService.NOTIFICATION_ID, builder.build())
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (
            Build.VERSION.SDK_INT < 26 ||
            notificationManagerCompat.getNotificationChannel(PlaybackService.CHANNEL_ID) != null
        ) {
            return
        }

        val channel =
            NotificationChannel(
                PlaybackService.CHANNEL_ID,
                service.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }
}