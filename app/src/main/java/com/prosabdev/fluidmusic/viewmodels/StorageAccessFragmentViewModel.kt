package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderSAF

class StorageAccessFragmentViewModel : ViewModel() {

    private val mMutableRequestAddFolder = MutableLiveData<Int>(0)
    private val mMutableFolderList = MutableLiveData<ArrayList<FolderSAF>>(ArrayList())
    private val mMutableAddFolderSAF = MutableLiveData<FolderSAF?>(null)

    private val mRequestAddFolder: LiveData<Int> get() = mMutableRequestAddFolder
    private val mAddFolderSAF: MutableLiveData<FolderSAF?> get() = mMutableAddFolderSAF
    private val mFolderList: LiveData<ArrayList<FolderSAF>> get() = mMutableFolderList

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
    }
    fun getAddFolderSAF(): MutableLiveData<FolderSAF?> {
        return mAddFolderSAF
    }
    fun getFoldersList(): LiveData<ArrayList<FolderSAF>> {
        return mFolderList
    }
    fun removeFromFoldersList(position : Int) {
        val tempOldList = mFolderList.value
        tempOldList?.removeAt(position)
        mMutableFolderList.value = tempOldList ?: ArrayList()
    }
}