package com.prosabdev.fluidmusic.media

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks

abstract class MediaEventsListener {

    abstract fun onIsPlayingChanged(isPlaying: Boolean)

    abstract fun onIsLoadingChanged(isLoading: Boolean)

    abstract fun onPlaybackStateChanged(playbackState: Int)

    abstract fun onMediaItemTransition(currentMediaItem: MediaItem?, currentMediaItemIndex: Int)

    abstract fun onMediaMetaDataChanged(mediaMetadata: MediaMetadata)

    abstract fun onPositionChanged(position: Long)

    abstract fun onRepeatModeChanged(repeatMode: Int)

    abstract fun onShuffleModeChanged(shuffleModeEnabled: Boolean)

    abstract fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters)

    abstract fun onSeekForwardIncrementChanged(seekForwardIncrement: Long)

    abstract fun onSeekBackIncrementChanged(seekBackIncrement: Long)

    abstract fun onAudioAttributesChanged(audioAttributes: AudioAttributes)

    abstract fun onVolumeChanged(volume: Float)
    abstract fun onDeviceVolumeChanged(deviceVolume: Int)

    abstract fun onTimelineChanged(currentTimeline: Timeline)

    abstract fun onMediaItemsChanged(mediaItems: List<MediaItem>)
}