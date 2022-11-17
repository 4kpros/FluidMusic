package com.prosabdev.fluidmusic.viewmodels.views.fragments.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FoldersFragmentViewModelFactory(): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoldersFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoldersFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}