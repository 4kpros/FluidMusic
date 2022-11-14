package com.prosabdev.fluidmusic.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MediaScannerActivityViewModel : GenericSongItemDataListViewModel() {
    private val mMutableFoldersCounter = MutableLiveData<Int>(0)
    private val mMutableSongsCounter = MutableLiveData<Int>(0)
    private val mMutablePlaylistsCounter = MutableLiveData<Int>(0)

    private val mFoldersCounter : LiveData<Int> get() = mMutableFoldersCounter
    private val mSongsCounter: LiveData<Int> get() = mMutableSongsCounter
    private val mPlaylistsCounter: LiveData<Int> get() = mMutablePlaylistsCounter

    override fun requestLoadDataAsync(context: Context, startCursor: Int, maxDataCount: Int) {
        super.requestLoadDataAsync(context, startCursor, maxDataCount)

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD DATA FROM EXPLORE SONGS")
        //First set is loading and is loading in background to true
        setIsLoading(true)
        setIsLoadingInBackground(true)

        //Else load songs from MediaFileScanner
        tempJob = MainScope().launch{
            MediaFileScanner.scanAudioFilesOnDevice(
                context,
                this@MediaScannerActivityViewModel,
                startCursor,
                maxDataCount
            )
        }
    }
    fun getFoldersCounter(): LiveData<Int> {
        return mFoldersCounter
    }
    fun setFoldersCounter(count : Int) {
        mMutableFoldersCounter.value = count
    }
    fun getSongsCounter(): LiveData<Int> {
        return mSongsCounter
    }
    fun setSongsCounter(count : Int) {
        mMutableSongsCounter.value = count
    }
    fun getPlaylistsCounter(): LiveData<Int> {
        return mPlaylistsCounter
    }
    fun setPlaylistsCounter(count : Int) {
        mMutablePlaylistsCounter.value = count
    }

}