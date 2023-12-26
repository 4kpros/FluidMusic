package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.media.PlaybackService

class MainActivityViewModel(
    application: Application,
) : AndroidViewModel(application) {

    var mediaEventsListener: MediaEventsListener = object : MediaEventsListener() {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            TODO("Not yet implemented")
        }

        override fun onMediaItemTransition(
            currentMediaItem: MediaItem?,
            currentMediaItemIndex: Int
        ) {
            TODO("Not yet implemented")
        }

        override fun onMediaMetaDataChanged(mediaMetadata: MediaMetadata) {
            TODO("Not yet implemented")
        }

        override fun onPositionChanged(position: Long) {
            TODO("Not yet implemented")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            TODO("Not yet implemented")
        }

        override fun onShuffleModeChanged(shuffleModeEnabled: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            TODO("Not yet implemented")
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrement: Long) {
            TODO("Not yet implemented")
        }

        override fun onSeekBackIncrementChanged(seekBackIncrement: Long) {
            TODO("Not yet implemented")
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            TODO("Not yet implemented")
        }

        override fun onVolumeChanged(volume: Float) {
            TODO("Not yet implemented")
        }

        override fun onDeviceVolumeChanged(deviceVolume: Int) {
            TODO("Not yet implemented")
        }

        override fun onTimelineChanged(currentTimeline: Timeline) {
            TODO("Not yet implemented")
        }

        override fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
            TODO("Not yet implemented")
        }
    }

    class Factory(
        private val app: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(app) as T
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}

