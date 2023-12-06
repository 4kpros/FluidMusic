package com.prosabdev.fluidmusic.viewmodels.mediacontroller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import com.prosabdev.fluidmusic.media.MediaEventsListener

class MediaPlayerStateViewModel: ViewModel() {

    //Mutable Variables
    val isPlaying = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val playbackState = MutableLiveData<Int>(Player.STATE_IDLE)
    val positionMs = MutableLiveData<Long>(0)
    val currentMediaItem = MutableLiveData<MediaItem?>(null)
    val currentMediaItemIndex = MutableLiveData<Int>(-1)
    val playlistMediaMetadata = MutableLiveData<MediaMetadata?>(null)
    val repeatMode = MutableLiveData<Int>(Player.REPEAT_MODE_OFF)
    val shuffleModeEnabled = MutableLiveData<Boolean>(false)
    val playbackSpeed = MutableLiveData<Float>(0f)
    val playbackPitch = MutableLiveData<Float>(0f)
    val tracks = MutableLiveData<Tracks?>(null)
    val volume = MutableLiveData<Float>(0F)
    val deviceVolume = MutableLiveData<Int>(0)
    val audioAttributes = MutableLiveData<AudioAttributes?>(null)
    val seekForwardIncrement = MutableLiveData<Long>(0)
    val seekBackIncrement = MutableLiveData<Long>(0)
    val currentTimeline = MutableLiveData<Timeline?>(null)

    //Public variables
    var mediaEventsListener: MediaEventsListener = object : MediaEventsListener() {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@MediaPlayerStateViewModel.isPlaying.value = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@MediaPlayerStateViewModel.isLoading.value = isLoading
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            this@MediaPlayerStateViewModel.playbackState.value = playbackState
        }

        override fun onMediaItemTransition(
            currentMediaItem: MediaItem?,
            currentMediaItemIndex: Int
        ) {
            this@MediaPlayerStateViewModel.currentMediaItem.value = currentMediaItem
            this@MediaPlayerStateViewModel.currentMediaItemIndex.value = currentMediaItemIndex
        }

        override fun onPositionDiscontinuity(currentPosition: Long) {
            this@MediaPlayerStateViewModel.positionMs.value = currentPosition
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@MediaPlayerStateViewModel.repeatMode.value = repeatMode
        }

        override fun onShuffleModeChanged(shuffleModeEnabled: Boolean) {
            this@MediaPlayerStateViewModel.shuffleModeEnabled.value = shuffleModeEnabled
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            this@MediaPlayerStateViewModel.playbackSpeed.value = playbackParameters.speed
            this@MediaPlayerStateViewModel.playbackPitch.value = playbackParameters.pitch
        }

        override fun onTracksChanged(tracks: Tracks) {
            this@MediaPlayerStateViewModel.tracks.value = tracks
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrement: Long) {
            this@MediaPlayerStateViewModel.seekForwardIncrement.value = seekForwardIncrement
        }

        override fun onSeekBackIncrementChanged(seekBackIncrement: Long) {
            this@MediaPlayerStateViewModel.seekBackIncrement.value = seekBackIncrement
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            this@MediaPlayerStateViewModel.audioAttributes.value = audioAttributes
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            this@MediaPlayerStateViewModel.playlistMediaMetadata.value = mediaMetadata
        }

        override fun onVolumeChanged(volume: Float) {
            this@MediaPlayerStateViewModel.volume.value = volume
        }

        override fun onDeviceVolumeChanged(deviceVolume: Int) {
            this@MediaPlayerStateViewModel.deviceVolume.value = deviceVolume
        }

        override fun onTimelineChanged(currentTimeline: Timeline) {
            this@MediaPlayerStateViewModel.currentTimeline.value = currentTimeline
        }
    }

    companion object {
        const val TAG = "MediaPlayerStateViewModel"
    }
}