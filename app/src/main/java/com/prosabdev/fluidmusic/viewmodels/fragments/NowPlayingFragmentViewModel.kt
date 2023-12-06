package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.media.MediaDescription
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import com.prosabdev.fluidmusic.media.MediaEventsListener

class NowPlayingFragmentViewModel(
    app: Application
) : AndroidViewModel(app) {

    class Factory(
        private val app: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NowPlayingFragmentViewModel(app) as T
        }
    }

    companion object {
        const val TAG = "NowPlayingFVM"

        const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }
}
