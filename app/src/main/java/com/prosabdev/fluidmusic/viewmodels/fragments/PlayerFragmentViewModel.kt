package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.models.PlaySongAtRequest
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val mMutableCurrentPlayingSong = MutableLiveData<SongItem?>(null)
    private val mMutableIsPlaying = MutableLiveData<Boolean>(false)
    private val mMutablePlayingProgressValue = MutableLiveData<Long>(0)
    private val mMutableShuffle = MutableLiveData<Int>(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    private val mMutableRepeat = MutableLiveData<Int>(PlaybackStateCompat.REPEAT_MODE_NONE)
    //
    private val mMutableQueueListSource = MutableLiveData<String?>(null)
    private val mMutableQueueListSourceColumnValue = MutableLiveData<String?>(null)
    private val mMutableQueueListSourceColumnIndex = MutableLiveData<String?>(null)
    private val mMutableSortBy = MutableLiveData<String?>(null)
    private val mMutableIsInverted = MutableLiveData<Boolean>(false)
    //
    private val mMutableSleepTimer = MutableLiveData<SleepTimerSP?>(null)
    private val mMutableSleepTimerStateStarted = MutableLiveData<Boolean>(false)
    //
    private val mMutableSkipNextTrackCounter = MutableLiveData<Int>(0)
    private val mMutableSkipPrevTrackCounter = MutableLiveData<Int>(0)
    private val mMutablePlaySongAtRequest = MutableLiveData<PlaySongAtRequest>(null)
    private val mMutableRequestPlaySongShuffleCounter = MutableLiveData<Int>(null)
    //
    private val mMutableUpdatePlaylistCounter = MutableLiveData<Int>(0)
    private val mMutableCanScrollSmoothViewpager = MutableLiveData<Boolean>(false)
    private val mMutableCanScrollCurrentPlayingSong = MutableLiveData<Boolean>(false)


    private val mCurrentPlayingSong: LiveData<SongItem?> get() = mMutableCurrentPlayingSong
    private val mIsPlaying: LiveData<Boolean> get() = mMutableIsPlaying
    private val mPlayingProgressValue: LiveData<Long> get() = mMutablePlayingProgressValue
    private val mShuffle: LiveData<Int> get() = mMutableShuffle
    private val mRepeat: LiveData<Int> get() = mMutableRepeat
    //
    private val mQueueListSource: LiveData<String?> get() = mMutableQueueListSource
    private val mQueueListSourceColumnValue: LiveData<String?> get() = mMutableQueueListSourceColumnValue
    private val mQueueListSourceColumnIndex: LiveData<String?> get() = mMutableQueueListSourceColumnIndex
    private val mSortBy: LiveData<String?> get() = mMutableSortBy
    private val mIsInverted: LiveData<Boolean> get() = mMutableIsInverted
    //
    private val mSleepTimer: LiveData<SleepTimerSP?> get() = mMutableSleepTimer
    private val mSleepTimerStateStarted: LiveData<Boolean> get() = mMutableSleepTimerStateStarted
    //
    private val mSkipNextTrackCounter: LiveData<Int> get() = mMutableSkipNextTrackCounter
    private val mSkipPrevTrackCounter: LiveData<Int> get() = mMutableSkipPrevTrackCounter
    private val mPlaySongAtRequest: LiveData<PlaySongAtRequest> get() = mMutablePlaySongAtRequest
    private val mUpdatePlaylistCounter: LiveData<Int> get() = mMutableUpdatePlaylistCounter
    //
    private val mCanScrollSmoothViewpager: LiveData<Boolean> get() = mMutableCanScrollSmoothViewpager
    private val mCanScrollCurrentPlayingSong: LiveData<Boolean> get() = mMutableCanScrollCurrentPlayingSong
    private val mRequestPlaySongShuffleCounter: LiveData<Int> get() = mMutableRequestPlaySongShuffleCounter

    fun getCurrentPlayingSong(): LiveData<SongItem?> {
        return mCurrentPlayingSong
    }
    fun setCurrentPlayingSong(songItem : SongItem?){
        MainScope().launch {
            mMutableCurrentPlayingSong.value = songItem
        }
    }
    fun toggleIsPlaying() {
        if(mCurrentPlayingSong.value == null || mCurrentPlayingSong.value?.uri == null) return
        MainScope().launch {
            val tempIsPlaying: Boolean = mIsPlaying.value ?: false
            mMutableIsPlaying.value = !tempIsPlaying
        }
    }
    fun getIsPlaying(): LiveData<Boolean> {
        return mIsPlaying
    }
    fun setIsPlaying(value : Boolean){
        MainScope().launch {
            mMutableIsPlaying.value = value
        }
    }
    fun getPlayingProgressValue(): LiveData<Long> {
        return mPlayingProgressValue
    }
    fun setPlayingProgressValue(value : Long){
        MainScope().launch {
            mMutablePlayingProgressValue.value = value
        }
    }
    fun getQueueListSource(): LiveData<String?> {
        return mQueueListSource
    }
    fun setQueueListSource(source : String?){
        MainScope().launch {
            mMutableQueueListSource.value = source
        }
    }
    fun getQueueListSourceColumnIndex(): LiveData<String?> {
        return mQueueListSourceColumnIndex
    }
    fun setQueueListSourceColumnIndex(source : String?){
        MainScope().launch {
            mMutableQueueListSourceColumnIndex.value = source
        }
    }
    fun getQueueListSourceColumnValue(): LiveData<String?> {
        return mQueueListSourceColumnValue
    }
    fun setQueueListSourceColumnValue(source : String?){
        MainScope().launch {
            mMutableQueueListSourceColumnValue.value = source
        }
    }
    fun getShuffle(): LiveData<Int> {
        return mShuffle
    }
    fun setShuffle(newShuffleValue : Int){
        MainScope().launch {
            mMutableShuffle.value = newShuffleValue
        }
    }
    fun getRepeat(): LiveData<Int> {
        return mRepeat
    }
    fun setRepeat(newRepeatValue : Int){
        MainScope().launch {
            mMutableRepeat.value = newRepeatValue
        }
    }
    fun getSleepTimer(): LiveData<SleepTimerSP?> {
        return mSleepTimer
    }
    fun setSleepTimer(value : SleepTimerSP?){
        MainScope().launch {
            mMutableSleepTimer.value = value
        }
    }
    fun getSleepTimerStateStarted(): LiveData<Boolean> {
        return mSleepTimerStateStarted
    }
    fun setSleepTimerStateStarted(state : Boolean) {
        MainScope().launch {
            mMutableSleepTimerStateStarted.value = state
        }
    }
    fun getSkipNextTrackCounter(): LiveData<Int> {
        return mSkipNextTrackCounter
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
    fun getSkipPrevTrackCounter(): LiveData<Int> {
        return mSkipPrevTrackCounter
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
    fun getUpdatePlaylistCounter(): LiveData<Int> {
        return mUpdatePlaylistCounter
    }
    fun setUpdatePlaylistCounter(){
        MainScope().launch {
            var tempCounter: Int = mUpdatePlaylistCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableUpdatePlaylistCounter.value = tempCounter
        }
    }
    fun getSortBy(): LiveData<String?> {
        return mSortBy
    }
    fun setSortBy(sortBy : String?) {
        if(sortBy == mSortBy.value) return
        MainScope().launch {
            mMutableSortBy.value = sortBy ?: "name"
        }
    }
    fun getIsInverted(): LiveData<Boolean> {
        return mIsInverted
    }
    fun setIsInverted(isInverted : Boolean) {
        if(isInverted == mIsInverted.value) return
        MainScope().launch {
            mMutableIsInverted.value = isInverted
        }
    }
    fun getCanScrollCurrentPlayingSong(): LiveData<Boolean> {
        return mCanScrollSmoothViewpager
    }
    fun setCanScrollCurrentPlayingSong(value: Boolean) {
        MainScope().launch {
            mMutableCanScrollSmoothViewpager.value = value
        }
    }
    fun getCanScrollSmoothViewpager(): LiveData<Boolean> {
        return mCanScrollCurrentPlayingSong
    }
    fun setCanScrollSmoothViewpager(value: Boolean) {
        MainScope().launch {
            mMutableCanScrollCurrentPlayingSong.value = value
        }
    }
    fun getRequestPlaySongAt(): LiveData<PlaySongAtRequest> {
        return mPlaySongAtRequest
    }
    fun setRequestPlaySongAt(playSongAtRequest: PlaySongAtRequest) {
        MainScope().launch {
            mMutablePlaySongAtRequest.value = playSongAtRequest
        }
    }
    fun getRequestPlaySongShuffleCounter(): LiveData<Int> {
        return mRequestPlaySongShuffleCounter
    }
    fun setRequestPlaySongShuffleCounter() {
        MainScope().launch {
            var tempCounter: Int = mUpdatePlaylistCounter.value ?: 0
            if(tempCounter >= 100)
                tempCounter = 0
            tempCounter++
            mMutableRequestPlaySongShuffleCounter.value = tempCounter
        }
    }
}
