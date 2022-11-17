package com.prosabdev.fluidmusic.viewmodels.views.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StorageAccessActivityViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageAccessActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StorageAccessActivityViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}