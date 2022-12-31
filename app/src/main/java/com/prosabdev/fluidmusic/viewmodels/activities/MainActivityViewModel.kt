package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.common.media.MusicServiceConnection

class MainActivityViewModel(
    application: Application,
    private val musicServiceConnection: MusicServiceConnection
) : AndroidViewModel(application) {

    class Factory(
        private val app: Application,
        private val musicServiceConnection: MusicServiceConnection
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(app, musicServiceConnection) as T
        }
    }

    companion object {
        private const val TAG = "MainActivityVM"
    }
}

