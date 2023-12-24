package com.prosabdev.fluidmusic.utils

import android.app.Application
import android.content.Context
import com.prosabdev.fluidmusic.media.MediaEventsListener
import com.prosabdev.fluidmusic.viewmodels.activities.EqualizerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.activities.MainActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun provideMediaControllerViewModel(listener: MediaEventsListener): MediaControllerViewModel.Factory {
        return MediaControllerViewModel.Factory(listener)
    }

    fun provideMainActivityViewModel(app: Application): MainActivityViewModel.Factory {
        return MainActivityViewModel.Factory(app)
    }

    fun provideEqualizerActivityViewModel(app: Application): EqualizerActivityViewModel.Factory {
        return EqualizerActivityViewModel.Factory(app)
    }

    fun provideNowPlayingFragmentViewModel(ctx: Context): PlayingNowFragmentViewModel.Factory {
        val applicationContext = ctx.applicationContext
        return PlayingNowFragmentViewModel.Factory(
            applicationContext as Application
        )
    }
}