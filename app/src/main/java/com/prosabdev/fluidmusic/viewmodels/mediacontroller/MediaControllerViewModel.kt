package com.prosabdev.fluidmusic.viewmodels.mediacontroller

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.net.MediaType
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.utils.MathComputations
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.media.PlaybackService
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel

class MediaControllerViewModel(private val mMediaEventsListener: MediaEventsListener): ViewModel() {

    private lateinit var mPositionHandler: Handler
    private val mPositionRunnable = object : Runnable {
        override fun run() {
            mMediaEventsListener.onPositionChanged(mediaController?.currentPosition ?: 0)
            mPositionHandler.postDelayed(this, 1000)
        }
    }

    private val mediaControllerFuture = MutableLiveData<ListenableFuture<MediaController>?>(null)
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
                mPositionHandler = Handler(Looper.getMainLooper())
            },
            MoreExecutors.directExecutor()
        )
    }

    fun toggleRepeatMode(value: Int = Player.REPEAT_MODE_OFF){
        when(value){
            Player.REPEAT_MODE_OFF -> mediaController?.repeatMode = Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> mediaController?.repeatMode = Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> mediaController?.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun toggleShuffleModeEnabled(value: Boolean = false){
        mediaController?.shuffleModeEnabled = !value
    }

    fun togglePlayPause(isPlaying: Boolean = false){
        when(isPlaying){
            true -> mediaController?.pause()
            else -> mediaController?.play()
        }
    }

    fun playContent(
        mediaItems: List<MediaItem>,
        startIndex: Int = 0,
        startPosition: Long = 0,
        repeatMode: Int = Player.REPEAT_MODE_OFF,
        shuffleModeEnabled: Boolean = false,
        isCompleteList: Boolean = true,
        contentSource: String,
        contentColumnIndex: String? = null,
        contentColumnValue: String? = null
    ){
        mediaController?.repeatMode = repeatMode
        mediaController?.shuffleModeEnabled = shuffleModeEnabled
        if(shuffleModeEnabled){
            mediaController?.setMediaItems(mediaItems, true)
        }else{
            mediaController?.setMediaItems(mediaItems, startIndex, startPosition)
        }

        val extras = Bundle()
        extras.putString(KEY_CONTENT_TYPE, contentSource)
        extras.putString(KEY_CONTENT_COLUMN_INDEX, contentColumnIndex)
        extras.putString(KEY_CONTENT_COLUMN_VALUE, contentColumnValue)
        extras.putBoolean(KEY_IS_COMPLETE_LIST, isCompleteList)
        mediaController?.playlistMetadata = MediaMetadata.Builder()
            .setExtras(extras)
            .build()

        mMediaEventsListener.onMediaItemsChanged(mediaItems)
    }

    private fun listenMediaEvents() {
        val controller = mediaController ?: return

        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                        mMediaEventsListener.onIsPlayingChanged(player.isPlaying)
                        if(player.isPlaying){
                            mPositionHandler.post(mPositionRunnable)
                        }else{
                            mPositionHandler.removeCallbacks(mPositionRunnable)
                        }
                    }
                    if (events.contains(Player.EVENT_IS_LOADING_CHANGED)) {
                        mMediaEventsListener.onIsLoadingChanged(player.isLoading)
                    }
                    if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                        mMediaEventsListener.onPlaybackStateChanged(player.playbackState)
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        mMediaEventsListener.onMediaItemTransition(player.currentMediaItem, player.currentMediaItemIndex)
                        mMediaEventsListener.onPositionChanged(player.currentPosition)
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

    private fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture.value!!)
    }

    override fun onCleared() {
        super.onCleared()
        mPositionHandler.removeCallbacks(mPositionRunnable)
        releaseController()
    }

    companion object {
        const val KEY_CONTENT_TYPE = "KEY_CONTENT_TYPE"
        const val KEY_CONTENT_COLUMN_INDEX = "KEY_CONTENT_COLUMN_INDEX"
        const val KEY_CONTENT_COLUMN_VALUE = "KEY_CONTENT_COLUMN_VALUE"

        const val KEY_IS_COMPLETE_LIST = "KEY_IS_COMPLETE_LIST"
    }

    class Factory(
        private val listener: MediaEventsListener,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MediaControllerViewModel(listener) as T
        }
    }
}