package com.prosabdev.fluidmusic.media

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks

abstract class MediaEventsListener {

    open fun onIsPlayingChanged(isPlaying: Boolean) {

    }

    open fun onIsLoadingChanged(isLoading: Boolean) {

    }

    open fun onPlaybackStateChanged(playbackState: Int) {

    }

    open fun onMediaItemTransition(currentMediaItem: MediaItem?, currentMediaItemIndex: Int) {

    }

    open fun onMediaMetaDataChanged(mediaMetadata: MediaMetadata){

    }

    open fun onPositionDiscontinuity(currentPosition: Long) {

    }

    open fun onRepeatModeChanged(repeatMode: Int) {

    }

    open fun onShuffleModeChanged(shuffleModeEnabled: Boolean) {

    }

    open fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    open fun onSeekForwardIncrementChanged(seekForwardIncrement: Long) {

    }

    open fun onSeekBackIncrementChanged(seekBackIncrement: Long) {

    }

    open fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {

    }

    open fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {

    }

    open fun onTracksChanged(tracks: Tracks) {

    }

    open fun onVolumeChanged(volume: Float) {

    }
    open fun onDeviceVolumeChanged(deviceVolume: Int) {

    }

    open fun onTimelineChanged(currentTimeline: Timeline) {

    }
}