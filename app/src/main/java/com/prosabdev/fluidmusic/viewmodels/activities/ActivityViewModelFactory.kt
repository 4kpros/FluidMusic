package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActivityViewModelFactory(val application: Application? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageAccessActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StorageAccessActivityViewModel() as T
        }else if(modelClass.isAssignableFrom(MediaScannerActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaScannerActivityViewModel(application!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}