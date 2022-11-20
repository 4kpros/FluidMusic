package com.prosabdev.fluidmusic.viewmodels.views.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MediaScannerActivityViewModel : ViewModel() {

    private val mMutableIsLoadingInBackground = MutableLiveData<Boolean>(false)
    private val mMutableFoldersCounter = MutableLiveData<Int>(0)
    private val mMutableSongsCounter = MutableLiveData<Int>(0)
    private val mMutablePlaylistsCounter = MutableLiveData<Int>(0)
    private val mMutableEmptyFolderUriCounter = MutableLiveData<Int>(0)

    private val mIsLoadingInBackground: LiveData<Boolean> get() = mMutableIsLoadingInBackground
    private val mFoldersCounter: LiveData<Int> get() = mMutableFoldersCounter
    private val mSongsCounter: LiveData<Int> get() = mMutableSongsCounter
    private val mPlaylistsCounter: LiveData<Int> get() = mMutablePlaylistsCounter
    private val mEmptyFolderUriCounter: LiveData<Int> get() = mMutableEmptyFolderUriCounter


    fun setIsLoadingInBackground(isLoading : Boolean) {
        MainScope().launch {
            mMutableIsLoadingInBackground.value = isLoading
        }
    }
    fun getIsLoadingInBackground(): LiveData<Boolean> {
        return mIsLoadingInBackground
    }
    //
    fun getFoldersCounter(): LiveData<Int> {
        return mFoldersCounter
    }
    fun setFoldersCounter(value : Int) {
        MainScope().launch {
            mMutableFoldersCounter.value = value
        }
    }
    //
    fun getSongsCounter(): LiveData<Int> {
        return mSongsCounter
    }
    fun setSongsCounter(value : Int) {
        MainScope().launch {
            mMutableSongsCounter.value = value
        }
    }
    //
    fun getPlaylistsCounter(): LiveData<Int> {
        return mPlaylistsCounter
    }
    fun setPlaylistsCounter(value : Int) {
        MainScope().launch {
            mMutablePlaylistsCounter.value = value
        }
    }
    //
    fun getEmptyFolderUriCounter(): LiveData<Int> {
        return mEmptyFolderUriCounter
    }
    fun setIncrementEmptyFolderUriCounter() {
        MainScope().launch {
            var oldValue : Int = mEmptyFolderUriCounter.value ?: 0
            oldValue++
            mMutableEmptyFolderUriCounter.value = oldValue
        }
    }
}