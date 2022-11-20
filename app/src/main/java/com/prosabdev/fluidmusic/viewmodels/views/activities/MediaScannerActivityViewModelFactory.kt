package com.prosabdev.fluidmusic.viewmodels.views.activities

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MediaScannerActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaScannerActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaScannerActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}