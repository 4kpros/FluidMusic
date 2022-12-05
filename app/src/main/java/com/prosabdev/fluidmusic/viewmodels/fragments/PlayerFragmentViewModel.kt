package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val mMutablePlayerLoadSessionCount = MutableLiveData<Int>(0)
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
    private val mMutableUpdatePlaylistCounter = MutableLiveData<Int>(0)
    private val mMutableIsQueueMusicUpdated = MutableLiveData<Boolean>(true)
    private val mMutableSortBy = MutableLiveData<String>("id")
    private val mMutableIsInverted = MutableLiveData<Boolean>(false)
    private val mMutableCanScrollSmoothViewpager = MutableLiveData<Boolean>(false)
    private val mMutableCanScrollCurrentPlayingSong = MutableLiveData<Boolean>(false)

    private val mPlayerLoadSessionCount: LiveData<Int> get() = mMutablePlayerLoadSessionCount
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
    private val mUpdatePlaylistCounter: LiveData<Int> get() = mMutableUpdatePlaylistCounter
    private val mIsQueueMusicUpdated: LiveData<Boolean> get() = mMutableIsQueueMusicUpdated
    private val mSortBy: LiveData<String> get() = mMutableSortBy
    private val mIsInverted: LiveData<Boolean> get() = mMutableIsInverted
    private val mCanScrollSmoothViewpager: LiveData<Boolean> get() = mMutableCanScrollSmoothViewpager
    private val mCanScrollCurrentPlayingSong: LiveData<Boolean> get() = mMutableCanScrollCurrentPlayingSong

    fun setPlayerLoadSessionCounter(){
        MainScope().launch {
            var tempCounter: Int = mPlayerLoadSessionCount.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutablePlayerLoadSessionCount.value = tempCounter
        }
    }
    fun getPlayerLoadSessionCounter(): LiveData<Int> {
        return mPlayerLoadSessionCount
    }
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

    fun setUpdatePlaylistCounter(){
        MainScope().launch {
            var tempCounter: Int = mUpdatePlaylistCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableIsQueueMusicUpdated.value = false
            mMutableUpdatePlaylistCounter.value = tempCounter
        }
    }
    fun getUpdatePlaylistCounter(): LiveData<Int> {
        return mUpdatePlaylistCounter
    }
    fun setIsQueueMusicUpdated() {
        mMutableIsQueueMusicUpdated.value = true
    }
    fun getIsQueueMusicUpdated(): LiveData<Boolean> {
        return mIsQueueMusicUpdated
    }
    fun setSortBy(sortBy : String) {
        if(sortBy == mSortBy.value) return
        MainScope().launch {
            mMutableSortBy.value = sortBy
        }
    }
    fun getSortBy(): LiveData<String> {
        return mSortBy
    }
    fun setIsInverted(isInverted : Boolean) {
        if(isInverted == mIsInverted.value) return
        MainScope().launch {
            mMutableIsInverted.value = isInverted
        }
    }
    fun getIsInverted(): LiveData<Boolean> {
        return mIsInverted
    }

    fun setCanScrollCurrentPlayingSong(value: Boolean) {
        mMutableCanScrollSmoothViewpager.value = value
    }
    fun getCanScrollCurrentPlayingSong(): LiveData<Boolean> {
        return mCanScrollSmoothViewpager
    }

    fun setCanScrollSmoothViewpager(value: Boolean) {
        mMutableCanScrollCurrentPlayingSong.value = value
    }
    fun getCanScrollSmoothViewpager(): LiveData<Boolean> {
        return mCanScrollCurrentPlayingSong
    }
}
