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
import com.prosabdev.common.persistence.models.SleepTimerSP
import com.prosabdev.fluidmusic.media.MediaEventsListener

class MediaPlayerDataViewModel: ViewModel() {

    //Mutable Variables
    val isPlaying = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val playbackState = MutableLiveData<Int>(Player.STATE_IDLE)
    val positionMs = MutableLiveData<Long>(0)
    val currentMediaItem = MutableLiveData<MediaItem?>(null)
    val mediaItems = MutableLiveData<List<MediaItem?>?>(listOf())
    val currentMediaItemIndex = MutableLiveData<Int>(-1)
    val repeatMode = MutableLiveData<Int>(Player.REPEAT_MODE_OFF)
    val shuffleModeEnabled = MutableLiveData<Boolean>(false)
    val playbackSpeed = MutableLiveData<Float>(0f)
    val playbackPitch = MutableLiveData<Float>(0f)
    val volume = MutableLiveData<Float>(0F)
    val deviceVolume = MutableLiveData<Int>(0)
    val audioAttributes = MutableLiveData<AudioAttributes?>(null)
    val seekForwardIncrement = MutableLiveData<Long>(0)
    val seekBackIncrement = MutableLiveData<Long>(0)
    val sleepTimer = MutableLiveData<SleepTimerSP?>(null)
    val currentTimeline = MutableLiveData<Timeline?>(null)

    val queueListSource = MutableLiveData<String>(null)
    val queueListSourceColumnIndex = MutableLiveData<String>(null)
    val queueListSourceColumnValue = MutableLiveData<String>(null)
    val queueListSortBy = MutableLiveData<String>(null)
    val queueListIsInverted = MutableLiveData<Boolean>(false)

    //Public variables
    var mediaEventsListener: MediaEventsListener = object : MediaEventsListener() {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@MediaPlayerDataViewModel.isPlaying.value = isPlaying
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            this@MediaPlayerDataViewModel.isLoading.value = isLoading
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            this@MediaPlayerDataViewModel.playbackState.value = playbackState
        }

        override fun onMediaItemTransition(
            currentMediaItem: MediaItem?,
            currentMediaItemIndex: Int
        ) {
            this@MediaPlayerDataViewModel.currentMediaItem.value = currentMediaItem
            this@MediaPlayerDataViewModel.currentMediaItemIndex.value = currentMediaItemIndex
        }

        override fun onMediaMetaDataChanged(mediaMetadata: MediaMetadata) {
            //
        }

        override fun onPositionChanged(position: Long) {
            this@MediaPlayerDataViewModel.positionMs.value = position
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@MediaPlayerDataViewModel.repeatMode.value = repeatMode
        }

        override fun onShuffleModeChanged(shuffleModeEnabled: Boolean) {
            this@MediaPlayerDataViewModel.shuffleModeEnabled.value = shuffleModeEnabled
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            this@MediaPlayerDataViewModel.playbackSpeed.value = playbackParameters.speed
            this@MediaPlayerDataViewModel.playbackPitch.value = playbackParameters.pitch
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrement: Long) {
            this@MediaPlayerDataViewModel.seekForwardIncrement.value = seekForwardIncrement
        }

        override fun onSeekBackIncrementChanged(seekBackIncrement: Long) {
            this@MediaPlayerDataViewModel.seekBackIncrement.value = seekBackIncrement
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            this@MediaPlayerDataViewModel.audioAttributes.value = audioAttributes
        }

        override fun onVolumeChanged(volume: Float) {
            this@MediaPlayerDataViewModel.volume.value = volume
        }

        override fun onDeviceVolumeChanged(deviceVolume: Int) {
            this@MediaPlayerDataViewModel.deviceVolume.value = deviceVolume
        }

        override fun onTimelineChanged(currentTimeline: Timeline) {
            this@MediaPlayerDataViewModel.currentTimeline.value = currentTimeline
        }

        override fun onMediaItemsChanged(mediaItems: List<MediaItem>) {
            this@MediaPlayerDataViewModel.mediaItems.value = mediaItems
        }

    }

    companion object {
        const val TAG = "MediaPlayerStateViewModel"
    }
}