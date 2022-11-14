package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderSAF

class StorageAccessActivityViewModel : ViewModel() {

    private val mMutableHaveBeenUpdated = MutableLiveData<Boolean>(false)
    private val mMutableRequestAddFolder = MutableLiveData<Int>(0)
    private val mMutableFolderList = MutableLiveData<ArrayList<FolderSAF>>(ArrayList())
    private val mMutableAddFolderSAF = MutableLiveData<FolderSAF?>(null)
    private val mMutableRemoveFolderSAF = MutableLiveData<Int>(-1)

    private val mHaveBeenUpdated: LiveData<Boolean> get() = mMutableHaveBeenUpdated
    private val mRequestAddFolder: LiveData<Int> get() = mMutableRequestAddFolder
    private val mFolderList: LiveData<ArrayList<FolderSAF>> get() = mMutableFolderList
    private val mAddFolderSAF: MutableLiveData<FolderSAF?> get() = mMutableAddFolderSAF
    private val mRemoveFolderSAF: MutableLiveData<Int> get() = mMutableRemoveFolderSAF

    fun getHaveBeenUpdated(): LiveData<Boolean> {
        return mHaveBeenUpdated
    }
    fun setRequestAddFolder(value : Int){
        mMutableRequestAddFolder.value = value
    }
    fun getRequestAddFolder(): LiveData<Int> {
        return mRequestAddFolder
    }
    fun setAddFolderSAF(value : FolderSAF?){
        if(value == null)
            return
        val tempOldList = mFolderList.value
        tempOldList?.add(value)
        mMutableFolderList.value = tempOldList ?: ArrayList()
        mMutableAddFolderSAF.value = value
        mMutableHaveBeenUpdated.value = true
    }
    fun getAddFolderSAF(): LiveData<FolderSAF?> {
        return mAddFolderSAF
    }
    fun setRemoveFolderSAF(position : Int){
        mMutableRemoveFolderSAF.value = position
    }
    fun getRemoveFolderSAF(): LiveData<Int> {
        return mRemoveFolderSAF
    }
    fun getFoldersList(): LiveData<ArrayList<FolderSAF>> {
        return mFolderList
    }
    fun removeFromFoldersList(position : Int) {
        val tempOldList = mFolderList.value
        tempOldList?.removeAt(position)
        mMutableFolderList.value = tempOldList ?: ArrayList()
        mMutableHaveBeenUpdated.value = true
    }
}