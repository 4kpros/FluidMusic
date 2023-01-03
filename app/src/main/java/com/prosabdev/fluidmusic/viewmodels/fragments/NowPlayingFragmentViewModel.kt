package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.prosabdev.common.extensions.*
import com.prosabdev.common.media.MusicServiceConnection
import com.prosabdev.common.sharedprefs.models.SleepTimerSP

class NowPlayingFragmentViewModel(
    app: Application,
    musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(app) {

    data class NowPlayingMetadata(
        val id: String?,
        val uri: Uri?,
        val coverArtUri: Uri?,
        val title: String?,
        val subtitle: String?,
        val duration: Long
    )

    //Mutable Variables
    private val mPlaybackState = MutableLiveData<PlaybackStateCompat>()
    private val mShuffleState = MutableLiveData<Int>()
    private val mRepeatState = MutableLiveData<Int>()
    private val mMediaMetadata = MutableLiveData<NowPlayingMetadata>()
    private val mCurrentDuration = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    private val mQueueListSource = MutableLiveData<String?>(null)
    private val mQueueListSourceColumnValue = MutableLiveData<String?>(null)
    private val mQueueListSourceColumnIndex = MutableLiveData<String?>(null)
    private val mSortBy = MutableLiveData<String?>(null)
    private val mIsInverted = MutableLiveData<Boolean>(false)
    private val mSleepTimer = MutableLiveData<SleepTimerSP?>(null)
    private val mSleepTimerStateStarted = MutableLiveData<Boolean>(false)
    private var mCanUpdateCurrentPlayingDuration = true
    private val mHandler = Handler(Looper.getMainLooper())

    //Observers
    private val mPlaybackStateObserver = Observer<PlaybackStateCompat> { playbackStateCompat ->
        mPlaybackState.postValue(playbackStateCompat ?: MusicServiceConnection.EMPTY_PLAYBACK_STATE)
    }
    private val mShuffleStateObserver = Observer<Int> { shuffleMode ->
        mShuffleState.postValue(shuffleMode)
    }
    private val mRepeatStateObserver = Observer<Int> { repeatMode ->
        mRepeatState.postValue(repeatMode)
    }
    private val mMediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetadata ->
        if (mediaMetadata != null && mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingMetadata = NowPlayingMetadata(
                mediaMetadata.id,
                mediaMetadata.mediaUri,
                mediaMetadata.albumArtUri,
                mediaMetadata.title?.trim(),
                mediaMetadata.displaySubtitle?.trim(),
                mediaMetadata.duration
            )
            mMediaMetadata.postValue(nowPlayingMetadata)
        }
    }
    private val mMusicServiceConnection = musicServiceConnection.also {
        it.playbackState.observeForever(mPlaybackStateObserver)
        it.shuffleState.observeForever(mShuffleStateObserver)
        it.repeatState.observeForever(mRepeatStateObserver)
        it.metaDataPlaying.observeForever(mMediaMetadataObserver)
        checkPlaybackPosition()
    }
    private fun checkPlaybackPosition() {
        mHandler.postDelayed(
            {
                val currentPosition = mPlaybackState.value?.currentPlayBackPosition
                if (mCurrentDuration.value != currentPosition)
                    mCurrentDuration.postValue(currentPosition)
                if (mCanUpdateCurrentPlayingDuration)
                    checkPlaybackPosition()
            },
            POSITION_UPDATE_INTERVAL_MILLIS
        )
    }

    //Player actions
    fun prepare() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.prepare()
    }
    fun play() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.play()
    }
    fun pause() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.pause()
    }
    fun stop() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.stop()
    }
    fun skipToQueueItem(id: Long) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.skipToQueueItem(id)
    }
    fun skipNext() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.skipToNext()
    }
    fun skipPrev() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.skipToPrevious()
    }
    fun playFromUri(mediaUri: Uri, extras: Bundle? = null, pauseAllowed: Boolean = true) {
        val nowPlaying = mMusicServiceConnection.metaDataPlaying.value
        val transportControls = mMusicServiceConnection.transportControls

        val isPrepared = mMusicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaUri == nowPlaying?.mediaUri) {
            mMusicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) transportControls.pause() else Unit
                    playbackState.isPlayEnabled ->
                        transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaUri=${mediaUri})"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromUri(mediaUri, extras)
        }
    }
    fun playFromMediaId(mediaId: String, extras: Bundle? = null) {
        val nowPlaying = mMusicServiceConnection.metaDataPlaying.value
        val transportControls = mMusicServiceConnection.transportControls

        val isPrepared = mMusicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            mMusicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=$mediaId)"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, extras)
        }
    }
    fun setShuffleMode(mode: Int) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.setShuffleMode(mode)
    }
    fun setRepeatMode(mode: Int) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.setRepeatMode(mode)
    }
    fun seekTo(pos: Long) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.seekTo(pos)
    }
    fun fastForward() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.fastForward()
    }
    fun rewind() {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.rewind()
    }
    fun setPlaybackSpeed(speed: Float) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.setPlaybackSpeed(speed)
    }
    fun setRating(rating: RatingCompat) {
        val transportControls = mMusicServiceConnection.transportControls
        transportControls.setRating(rating)
    }

    override fun onCleared() {
        super.onCleared()
        // Remove the permanent observers from the MusicServiceConnection.
        mMusicServiceConnection.playbackState.removeObserver(mPlaybackStateObserver)
        mMusicServiceConnection.shuffleState.removeObserver(mShuffleStateObserver)
        mMusicServiceConnection.repeatState.removeObserver(mRepeatStateObserver)
        mMusicServiceConnection.metaDataPlaying.removeObserver(mMediaMetadataObserver)
        // Stop updating the current playing duration
        mCanUpdateCurrentPlayingDuration = false
    }
    class Factory(
        private val app: Application,
        private val musicServiceConnection: MusicServiceConnection
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NowPlayingFragmentViewModel(app, musicServiceConnection) as T
        }
    }

    companion object {
        const val TAG = "NowPlayingFVM"
        const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }
}
