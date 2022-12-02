package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val mMutableCurrentPlayingSong = MutableLiveData<SongItem?>(null)
    private val mMutableIsPlaying = MutableLiveData<Boolean>(false)
    private val mMutablePlayingProgressValue = MutableLiveData<Long>(0)
    private val mMutableQueueListSource = MutableLiveData<String>(ConstantValues.EXPLORE_ALL_SONGS)
    private val mMutableQueueListSourceValue = MutableLiveData<String>("")
    private val mMutableShuffle = MutableLiveData<Int>(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    private val mMutableRepeat = MutableLiveData<Int>(PlaybackStateCompat.REPEAT_MODE_NONE)
    private val mMutableSleepTimer = MutableLiveData<SleepTimerSP?>(null)
    private val mMutableSleepTimerStateStarted = MutableLiveData<Boolean>(false)
    private val mMutableSkipNextTrackCounter = MutableLiveData<Int>(0)
    private val mMutableSkipPrevTrackCounter = MutableLiveData<Int>(0)

    private val mMutableShowEqualizerFragmentCounter = MutableLiveData<Int>(0)
    private val mMutableShowMediaScannerFragmentCounter = MutableLiveData<Int>(0)

    private val mCurrentPlayingSong: LiveData<SongItem?> get() = mMutableCurrentPlayingSong
    private val mIsPlaying: LiveData<Boolean> get() = mMutableIsPlaying
    private val mPlayingProgressValue: LiveData<Long> get() = mMutablePlayingProgressValue
    private val mQueueListSource: LiveData<String> get() = mMutableQueueListSource
    private val mQueueListSourceValue: LiveData<String> get() = mMutableQueueListSourceValue
    private val mShuffle: LiveData<Int> get() = mMutableShuffle
    private val mRepeat: LiveData<Int> get() = mMutableRepeat
    private val mSleepTimer: LiveData<SleepTimerSP?> get() = mMutableSleepTimer
    private val mSleepTimerStateStarted: LiveData<Boolean> get() = mMutableSleepTimerStateStarted
    private val mSkipNextTrackCounter: LiveData<Int> get() = mMutableSkipNextTrackCounter
    private val mSkipPrevTrackCounter: LiveData<Int> get() = mMutableSkipPrevTrackCounter

    private val mShowEqualizerFragmentCounter: LiveData<Int> get() = mMutableShowEqualizerFragmentCounter
    private val mShowMediaScannerFragmentCounter: LiveData<Int> get() = mMutableShowMediaScannerFragmentCounter

    fun setCurrentPlayingSong(songItem : SongItem?){
        MainScope().launch {
            mMutableCurrentPlayingSong.value = songItem
        }
    }
    fun getCurrentPlayingSong(): LiveData<SongItem?> {
        return mCurrentPlayingSong
    }
    fun setIsPlaying(value : Boolean){
        MainScope().launch {
            mMutableIsPlaying.value = value
        }
    }
    fun getIsPlaying(): LiveData<Boolean> {
        return mIsPlaying
    }
    fun toggleIsPlaying() {
        if(mCurrentPlayingSong.value == null || mCurrentPlayingSong.value?.uri == null) return
        MainScope().launch {
            val tempIsPlaying: Boolean = mIsPlaying.value ?: false
            mMutableIsPlaying.value = !tempIsPlaying
        }
    }
    fun setPlayingProgressValue(value : Long){
        MainScope().launch {
            mMutablePlayingProgressValue.value = value
        }
    }
    fun getPlayingProgressValue(): LiveData<Long> {
        return mPlayingProgressValue
    }
    fun setQueueListSource(source : String){
        MainScope().launch {
            mMutableQueueListSource.value = source
        }
    }
    fun getQueueListSource(): LiveData<String> {
        return mQueueListSource
    }
    fun setQueueListSourceValue(source : String?){
        MainScope().launch {
            mMutableQueueListSourceValue.value = source ?: ""
        }
    }
    fun getSourceOfQueueListValue(): LiveData<String> {
        return mQueueListSourceValue
    }
    fun setShuffle(newShuffleValue : Int){
        MainScope().launch {
            mMutableShuffle.value = newShuffleValue
        }
    }
    fun getShuffle(): LiveData<Int> {
        return mShuffle
    }
    fun setRepeat(newRepeatValue : Int){
        MainScope().launch {
            mMutableRepeat.value = newRepeatValue
        }
    }
    fun getRepeat(): LiveData<Int> {
        return mRepeat
    }
    fun setSleepTimer(value : SleepTimerSP?){
        MainScope().launch {
            mMutableSleepTimer.value = value
        }
    }
    fun getSleepTimer(): LiveData<SleepTimerSP?> {
        return mSleepTimer
    }
    fun setSleepTimerStateStarted(state : Boolean) {
        MainScope().launch {
            mMutableSleepTimerStateStarted.value = state
        }
    }
    fun getSleepTimerStateStarted(): LiveData<Boolean> {
        return mSleepTimerStateStarted
    }

    fun setSkipNextTrackCounter(){
        MainScope().launch {
            var tempCounter: Int = mSkipNextTrackCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableSkipNextTrackCounter.value = tempCounter
        }
    }
    fun getSkipNextTrackCounter(): LiveData<Int> {
        return mSkipNextTrackCounter
    }
    fun setSkipPrevTrackCounter(){
        MainScope().launch {
            var tempCounter: Int = mSkipPrevTrackCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableSkipPrevTrackCounter.value = tempCounter
        }
    }
    fun getSkipPrevTrackCounter(): LiveData<Int> {
        return mSkipPrevTrackCounter
    }

    fun setShowEqualizerFragmentCounter(){
        MainScope().launch {
            var tempCounter: Int = mShowEqualizerFragmentCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableShowEqualizerFragmentCounter.value = tempCounter
        }
    }
    fun getShowEqualizerFragmentCounter(): LiveData<Int> {
        return mShowEqualizerFragmentCounter
    }
}
