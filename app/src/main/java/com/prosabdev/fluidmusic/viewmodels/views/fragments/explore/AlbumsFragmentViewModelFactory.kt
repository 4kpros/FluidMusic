package com.prosabdev.fluidmusic.viewmodels.views.fragments.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlbumsFragmentViewModelFactory(): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumsFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}