package com.prosabdev.fluidmusic.viewmodels.explore

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner

class AllSongsFragmentViewModel : ViewModel() {
    private val mMutableIsLoading = MutableLiveData<Boolean>(false)
    private val mMutableIsLoadingInBackground = MutableLiveData<Boolean>(false)
    private val mMutableDataLoadedCounter = MutableLiveData<Int>(0)
    private val mMutableLastLoadedPosition = MutableLiveData<Int>(0)
    private val mMutableSongList = MutableLiveData<ArrayList<SongItem>>(null)

    private val mIsLoading : LiveData<Boolean> get() = mMutableIsLoading
    private val mIsLoadingInBackground : LiveData<Boolean> get() = mMutableIsLoadingInBackground
    private val mDataLoadedCounter : LiveData<Int> get() = mMutableDataLoadedCounter
    private val mLastLoadedPosition: LiveData<Int> get() = mMutableLastLoadedPosition
    private val mSongList: LiveData<ArrayList<SongItem>> get() = mMutableSongList

    fun requestLoadAsyncSongs(activity : Activity){
        //Load songs from database id exist

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD SONGS ASYNC")
        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesWithMediaStore(
            activity,
            mMutableSongList,
            mMutableIsLoading,
            mMutableIsLoadingInBackground,
            mMutableDataLoadedCounter,
            10
        )
    }
    fun getIsLoading(): LiveData<Boolean> {
        return mIsLoading
    }
    fun setIsLoading(value : Boolean) {
        mMutableIsLoadingInBackground.value = true
        mMutableIsLoading.value = true
    }
    fun getIsLoadingInBackground(): LiveData<Boolean> {
        return mIsLoadingInBackground
    }
    fun getDataLoadedCounter(): LiveData<Int> {
        return mDataLoadedCounter
    }
    fun getSongs(): LiveData<ArrayList<SongItem>>  {
        return mSongList
    }
    fun loadMoreSongs(fromPosition : Int = 50): LiveData<ArrayList<SongItem>>? {
        var tempLiveDataSongList: MutableLiveData<ArrayList<SongItem>>? = null
        val tempSongList: ArrayList<SongItem> = ArrayList()
        var tempStartPosition = mLastLoadedPosition.value ?: 0
        if(!(mSongList.value == null || mSongList.value?.size!! <= 0)){
            for (i in 0 until fromPosition){
                if(tempStartPosition.plus(i) < mSongList.value?.size!!){
                    tempSongList.add(mSongList.value?.get(i)!!)
                    tempStartPosition++
                }else{
                    break
                }
            }
            tempLiveDataSongList = MutableLiveData<ArrayList<SongItem>>()
            tempLiveDataSongList.value = tempSongList
        }
        Log.i(ConstantValues.TAG, "Load more method ended with $tempStartPosition new songs !")
        return tempLiveDataSongList
    }
}