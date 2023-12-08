package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.*

class PlayingNowFragmentViewModel(
    app: Application
) : AndroidViewModel(app) {

    val canSmoothScrollViewpager = MutableLiveData<Boolean>(false)
    val viewpagerChangedFromUser = MutableLiveData<Boolean>(false)

    class Factory(
        private val app: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayingNowFragmentViewModel(app) as T
        }
    }

    companion object {
        const val TAG = "NowPlayingFVM"

        const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }
}
