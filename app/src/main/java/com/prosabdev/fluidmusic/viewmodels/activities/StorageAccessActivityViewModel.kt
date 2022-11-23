package com.prosabdev.fluidmusic.viewmodels.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StorageAccessActivityViewModel : ViewModel() {

    private val mMutableRequestRemoveAllFolderUriTrees = MutableLiveData<Int>(0)

    private val mRequestRemoveAllFolderUriTrees: LiveData<Int> get() = mMutableRequestRemoveAllFolderUriTrees

    fun getRemoveAllFoldersCounter(): LiveData<Int> {
        return mRequestRemoveAllFolderUriTrees
    }
    fun setRemoveAllFoldersCounter(){
        var tempValue : Int = mRequestRemoveAllFolderUriTrees.value ?: 0
        tempValue++
        MainScope().launch {
            mMutableRequestRemoveAllFolderUriTrees.value = tempValue
        }
    }
}