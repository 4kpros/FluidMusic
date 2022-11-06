package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem

class PlayerFragmentViewModel : ViewModel()  {

    private val mMutableSourceOfQueueList = MutableLiveData<String>("")
    private val mMutableSourceOfQueueListValue = MutableLiveData<String>("")
    private val mMutableQueueList = MutableLiveData<ArrayList<SongItem>>()

    private val mMutableShuffle = MutableLiveData<Int>(0)
    private val mMutableRepeat = MutableLiveData<Int>(0)
    private val mMutableCurrentSong = MutableLiveData<Int>(0)

    private val mSourceOfQueueList: LiveData<String> get() = mMutableSourceOfQueueList
    private val mSourceOfQueueListValue: LiveData<String> get() = mMutableSourceOfQueueListValue
    private val mQueueList: LiveData<ArrayList<SongItem>> get() = mMutableQueueList
    private val mCurrentSong: LiveData<Int> get() = mMutableCurrentSong
    private val mShuffle: LiveData<Int> get() = mMutableShuffle
    private val mRepeat: LiveData<Int> get() = mMutableRepeat

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
    fun setQueueList(newQueueList : ArrayList<SongItem>){
        mMutableQueueList.value = newQueueList
    }
    fun getQueueList(): LiveData<ArrayList<SongItem>> {
        return mQueueList
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