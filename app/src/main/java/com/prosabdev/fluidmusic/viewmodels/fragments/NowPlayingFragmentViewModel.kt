package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.media.MediaDescription
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.prosabdev.common.persistence.models.SleepTimerSP

class NowPlayingFragmentViewModel(
    app: Application
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
    val mediaDescription = MutableLiveData<MediaDescription>()
    val playbackState = MutableLiveData<PlaybackState>()
    val shuffleState = MutableLiveData<Int>()
    val repeatState = MutableLiveData<Int>()
    val sleepTimer = MutableLiveData<SleepTimerSP?>(null)
    val sleepTimerStateStarted = MutableLiveData<Boolean>(false)

    //Local variables
    private var mCanUpdateCurrentPlayingDuration = true
    private val mHandler = Handler(Looper.getMainLooper())

    private fun checkPlaybackPosition() {
    }

    //Player actions
    fun prepare() {
    }
    fun play() {
    }
    fun pause() {
    }
    fun stop() {
    }
    fun skipNext() {
    }
    fun skipPrev() {
    }
    fun playFromUri(mediaUri: Uri, extras: Bundle? = null, pauseAllowed: Boolean = true) {
    }
    fun playFromMediaId(mediaId: String, extras: Bundle? = null) {
    }
    fun setShuffleMode(mode: Int) {
    }
    fun setRepeatMode(mode: Int) {
    }
    fun seekTo(pos: Long) {
    }
    fun fastForward() {
    }
    fun rewind() {
    }
    fun setPlaybackSpeed(speed: Float) {
    }

    override fun onCleared() {
        super.onCleared()
        // Stop updating the current playing duration
        mCanUpdateCurrentPlayingDuration = false
    }

    class Factory(
        private val app: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NowPlayingFragmentViewModel(app) as T
        }
    }

    companion object {
        const val TAG = "NowPlayingFVM"

        const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }
}
