package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem

class PlayerPageFragmentViewModel : ViewModel()  {
    private val mMutableShuffle = MutableLiveData<Boolean>()
    private val mMutableRepeat = MutableLiveData<Boolean>()
    private val mMutableCurrentSong = MutableLiveData<Int>()
    private val mMutableQueueList = MutableLiveData<List<SongItem>>()

    private val mQueueList: LiveData<List<SongItem>> get() = mMutableQueueList
    private val mCurrentSong: LiveData<Int> get() = mMutableCurrentSong
    private val mShuffle: LiveData<Boolean> get() = mMutableShuffle
    private val mRepeat: LiveData<Boolean> get() = mMutableRepeat

    fun setQueueList(newQueueList : List<SongItem>){
        mMutableQueueList.value = newQueueList
    }
    fun setCurrentSong(newCurrentSong : Int){
        mMutableCurrentSong.value = newCurrentSong
    }
    fun setShuffle(newShuffleValue : Boolean){
        mMutableShuffle.value = newShuffleValue
    }
    fun setRepeat(newRepeatValue : Boolean){
        mMutableRepeat.value = newRepeatValue
    }
}