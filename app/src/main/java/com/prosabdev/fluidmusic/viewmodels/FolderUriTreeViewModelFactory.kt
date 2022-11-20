package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao

class FolderUriTreeViewModelFactory(private val mFolderUriTreeDao : FolderUriTreeDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderUriTreeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderUriTreeViewModel(mFolderUriTreeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}