package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.media.PlaybackService

class MainActivityViewModel(
    application: Application,
) : AndroidViewModel(application) {

    var mediaEventsListener: MediaEventsListener = object : MediaEventsListener() {
    }

    class Factory(
        private val app: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(app) as T
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}

