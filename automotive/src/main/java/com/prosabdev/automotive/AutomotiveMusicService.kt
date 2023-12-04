package com.prosabdev.automotive

import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.os.Bundle
import android.service.media.MediaBrowserService

import java.util.ArrayList

class AutomotiveMusicService : MediaBrowserService() {
    private lateinit var session: MediaSession

    private val callback = object : MediaSession.Callback() {
        override fun onPlay() {}

        override fun onSkipToQueueItem(queueId: Long) {}

        override fun onSeekTo(position: Long) {}

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {}

        override fun onPause() {}

        override fun onStop() {}

        override fun onSkipToNext() {}

        override fun onSkipToPrevious() {}

        override fun onCustomAction(action: String, extras: Bundle?) {}

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {}
    }

    override fun onCreate() {
        super.onCreate()
        session = MediaSession(this, TAG)
        sessionToken = session.sessionToken
        session.setCallback(callback)
        session.setFlags(
            (
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
        )
    }

    override fun onDestroy() {
        session.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowser.MediaItem>>) {
        result.sendResult(ArrayList())
    }

    companion object {
        private const val TAG = "AutomotiveMusicService"
        private const val ROOT_ID = "root_id"
    }
}