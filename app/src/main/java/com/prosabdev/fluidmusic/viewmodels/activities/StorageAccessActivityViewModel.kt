package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StorageAccessActivityViewModel(app: Application) : AndroidViewModel(app) {

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