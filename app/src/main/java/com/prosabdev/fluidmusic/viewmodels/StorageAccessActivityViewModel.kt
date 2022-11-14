package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderSAF

class StorageAccessActivityViewModel : ViewModel() {

    private val mMutableRequestRemoveAllFolders = MutableLiveData<Int>(0)
    private val mMutableFoldersCounter = MutableLiveData<Int>(0)

    private val mRequestRemoveAllFolders: LiveData<Int> get() = mMutableRequestRemoveAllFolders
    private val mFoldersCounter: LiveData<Int> get() = mMutableFoldersCounter

    fun getFoldersCounter(): LiveData<Int> {
        return mFoldersCounter
    }
    fun setFoldersCounter(count : Int){
        mMutableFoldersCounter.value = count
    }
    fun getRemoveAllFoldersCounter(): LiveData<Int> {
        return mRequestRemoveAllFolders
    }
    fun setRemoveAllFoldersCounter(){
        var tempValue : Int = mMutableRequestRemoveAllFolders.value ?: 0
        tempValue++
        mMutableRequestRemoveAllFolders.value = tempValue
    }
}