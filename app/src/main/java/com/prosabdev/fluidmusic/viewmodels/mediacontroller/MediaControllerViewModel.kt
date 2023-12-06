package com.prosabdev.fluidmusic.viewmodels.mediacontroller

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.media.PlaybackService

class MediaControllerViewModel(private val mMediaEventsListener: MediaEventsListener): ViewModel() {

    val mediaControllerFuture = MutableLiveData<ListenableFuture<MediaController>?>(null)
    val mediaController: MediaController?
        get() =
            if (mediaControllerFuture.value?.isDone == true && mediaControllerFuture.value?.isCancelled == false)
                mediaControllerFuture.value?.get()
            else null

    @UnstableApi
    fun setupMediaController(ctx: Context) {
        val sessionToken = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))
        mediaControllerFuture.value = MediaController.Builder(ctx, sessionToken).buildAsync()
        mediaControllerFuture.value?.addListener(
            {
                listenMediaEvents()
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun listenMediaEvents() {
        val controller = mediaController ?: return
        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                        mMediaEventsListener.onIsPlayingChanged(player.isPlaying)
                    }
                    if (events.contains(Player.EVENT_IS_LOADING_CHANGED)) {
                        mMediaEventsListener.onIsLoadingChanged(player.isLoading)
                    }
                    if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                        mMediaEventsListener.onPlaybackStateChanged(player.playbackState)
                    }
                    if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
                        mMediaEventsListener.onPositionDiscontinuity(player.currentPosition)
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        mMediaEventsListener.onMediaItemTransition(player.currentMediaItem, player.currentMediaItemIndex)
                    }
                    if (events.contains(Player.EVENT_PLAYBACK_PARAMETERS_CHANGED)) {
                        mMediaEventsListener.onPlaybackParametersChanged(player.playbackParameters)
                    }
                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                        mMediaEventsListener.onMediaMetaDataChanged(player.mediaMetadata)
                    }
                    if (events.contains(Player.EVENT_REPEAT_MODE_CHANGED)) {
                        mMediaEventsListener.onRepeatModeChanged(player.repeatMode)
                    }
                    if (events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)) {
                        mMediaEventsListener.onShuffleModeChanged(player.shuffleModeEnabled)
                    }
                    if (events.contains(Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED)) {
                        mMediaEventsListener.onSeekForwardIncrementChanged(player.seekForwardIncrement)
                    }
                    if (events.contains(Player.EVENT_SEEK_BACK_INCREMENT_CHANGED)) {
                        mMediaEventsListener.onSeekBackIncrementChanged(player.seekBackIncrement)
                    }
                    if (events.contains(Player.EVENT_AUDIO_ATTRIBUTES_CHANGED)) {
                        mMediaEventsListener.onAudioAttributesChanged(player.audioAttributes)
                    }
                    if (events.contains(Player.EVENT_PLAYLIST_METADATA_CHANGED)) {
                        mMediaEventsListener.onPlaylistMetadataChanged(player.playlistMetadata)
                    }
                    if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                        mMediaEventsListener.onTracksChanged(player.currentTracks)
                    }
                    if (events.contains(Player.EVENT_VOLUME_CHANGED)) {
                        mMediaEventsListener.onVolumeChanged(player.volume)
                    }
                    if (events.contains(Player.EVENT_DEVICE_VOLUME_CHANGED)) {
                        mMediaEventsListener.onDeviceVolumeChanged(player.deviceVolume)
                    }
                    if (events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                        mMediaEventsListener.onTimelineChanged(player.currentTimeline)
                    }
                }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        releaseController()
    }

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture.value!!)
    }

    class Factory(
        private val listener: MediaEventsListener,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MediaControllerViewModel(listener) as T
        }
    }
}