package com.prosabdev.fluidmusic.viewmodels

import android.app.Activity
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel

class PlayerFragmentViewModel : GenericSongItemDataListViewModel()  {

    private val mMutableIsPlaying = MutableLiveData<Boolean>(false)
    private val mMutablePlayingProgressValue = MutableLiveData<Long>(0)
    private val mMutableSourceOfQueueList = MutableLiveData<String>("")
    private val mMutableSourceOfQueueListValue = MutableLiveData<String>("")
    private val mMutableShuffle = MutableLiveData<Int>(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    private val mMutableRepeat = MutableLiveData<Int>(PlaybackStateCompat.REPEAT_MODE_NONE)
    private val mMutableCurrentSong = MutableLiveData<Int>(0)

    private val mIsPlaying: LiveData<Boolean> get() = mMutableIsPlaying
    private val mPlayingProgressValue: LiveData<Long> get() = mMutablePlayingProgressValue
    private val mSourceOfQueueList: LiveData<String> get() = mMutableSourceOfQueueList
    private val mSourceOfQueueListValue: LiveData<String> get() = mMutableSourceOfQueueListValue
    private val mCurrentSong: LiveData<Int> get() = mMutableCurrentSong
    private val mShuffle: LiveData<Int> get() = mMutableShuffle
    private val mRepeat: LiveData<Int> get() = mMutableRepeat

    override fun requestLoadDataAsync(activity: Activity, minToUpdateDataList: Int) {
        super.requestLoadDataAsync(activity, minToUpdateDataList)

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD DATA FROM PLAYER FRAGMENT")

        //First set is loading and is loading in background to true
        setIsLoading(true)
        setIsLoadingInBackground(true)

        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesWithMediaStore(
            activity,
            this as GenericSongItemDataListViewModel,
            10
        )
    }

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
    fun setCurrentSong(newCurrentSong : Int){
        mMutableCurrentSong.value = newCurrentSong
    }
    fun getCurrentSong(): LiveData<Int> {
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