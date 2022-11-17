package com.prosabdev.fluidmusic.viewmodels.views.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MediaScannerActivityViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaScannerActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaScannerActivityViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}