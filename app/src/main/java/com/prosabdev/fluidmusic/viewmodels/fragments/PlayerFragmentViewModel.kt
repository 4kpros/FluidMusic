package com.prosabdev.fluidmusic.viewmodels.fragments

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP

class PlayerFragmentViewModel : ViewModel()  {

    private val mMutableIsPlaying = MutableLiveData<Boolean>(false)
    private val mMutablePlayingProgressValue = MutableLiveData<Long>(0)
    private val mMutableSourceOfQueueList = MutableLiveData<String>("")
    private val mMutableSourceOfQueueListValue = MutableLiveData<String>("")
    private val mMutableShuffle = MutableLiveData<Int>(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    private val mMutableRepeat = MutableLiveData<Int>(PlaybackStateCompat.REPEAT_MODE_NONE)
    private val mMutableCurrentSong = MutableLiveData<CurrentPlayingSongSP?>(null)

    private val mIsPlaying: LiveData<Boolean> get() = mMutableIsPlaying
    private val mPlayingProgressValue: LiveData<Long> get() = mMutablePlayingProgressValue
    private val mSourceOfQueueList: LiveData<String> get() = mMutableSourceOfQueueList
    private val mSourceOfQueueListValue: LiveData<String> get() = mMutableSourceOfQueueListValue
    private val mCurrentSong: LiveData<CurrentPlayingSongSP?> get() = mMutableCurrentSong
    private val mShuffle: LiveData<Int> get() = mMutableShuffle
    private val mRepeat: LiveData<Int> get() = mMutableRepeat

    fun setIsPlaying(value : Boolean){
        mMutableIsPlaying.value = value
    }
    fun getIsPlaying(): LiveData<Boolean> {
        return mIsPlaying
    }
    fun setPlayingProgressValue(value : Long){
        mMutablePlayingProgressValue.value = value
    }
    fun getPlayingProgressValue(): LiveData<Long> {
        return mPlayingProgressValue
    }
    fun setSourceOfQueueList(source : String){
        mMutableSourceOfQueueList.value = source
    }
    fun getSourceOfQueueList(): LiveData<String> {
        return mSourceOfQueueList
    }
    fun setSourceOfQueueListValue(source : String){
        mMutableSourceOfQueueListValue.value = source
    }
    fun getSourceOfQueueListValue(): LiveData<String> {
        return mSourceOfQueueListValue
    }
    fun setCurrentSong(newCurrentSong : CurrentPlayingSongSP?){
        mMutableCurrentSong.value = newCurrentSong
    }
    fun getCurrentSong(): LiveData<CurrentPlayingSongSP?> {
        return mCurrentSong
    }
    fun setShuffle(newShuffleValue : Int){
        mMutableShuffle.value = newShuffleValue
    }
    fun getShuffle(): LiveData<Int> {
        return mShuffle
    }
    fun setRepeat(newRepeatValue : Int){
        mMutableRepeat.value = newRepeatValue
    }
    fun getRepeat(): LiveData<Int> {
        return mRepeat
    }
}