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

    private val mMutableFirstLoaded = MutableLiveData<Boolean>(false)
    private val mMutableHaveData = MutableLiveData<Boolean>(false)
    private val mMutableStartPosition = MutableLiveData<Int>(0)
    private val mMutableSongList = MutableLiveData<ArrayList<SongItem>>()

    private val mFirstLoaded : LiveData<Boolean> get() = mMutableFirstLoaded
    private val mHaveData : LiveData<Boolean> get() = mMutableHaveData
    private val mStartPosition: LiveData<Int> get() = mMutableStartPosition
    private val mSongList: LiveData<ArrayList<SongItem>> get() = mMutableSongList

    fun requestLoadAsyncSongs(activity : Activity){
        //Load songs from database id exist

        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesWithMediaStore(activity, mMutableSongList, mMutableHaveData, 30)
    }
    fun setFirstLoaded(value : Boolean) {
        mMutableFirstLoaded.value = true
    }
    fun getFirstLoaded(): LiveData<Boolean> {
        return mFirstLoaded
    }
    fun onDataReady(): LiveData<Boolean> {
        return mHaveData
    }
    fun loadMoreSongs(pagination : Int = 30): LiveData<ArrayList<SongItem>>? {
        var tempLiveDataSongList: MutableLiveData<ArrayList<SongItem>>? = null
        val tempSongList: ArrayList<SongItem> = ArrayList()
        var tempStartPosition = mMutableStartPosition.value ?: 0
        if(!(mSongList.value == null || mSongList.value?.size!! <= 0)){
            for (i in 0 until pagination){
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