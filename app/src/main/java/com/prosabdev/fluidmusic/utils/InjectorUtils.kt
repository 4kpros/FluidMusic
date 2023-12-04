package com.prosabdev.fluidmusic.utils

import android.app.Application
import android.content.Context
import com.prosabdev.fluidmusic.viewmodels.activities.MainActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    fun provideMainActivityViewModel(app: Application): MainActivityViewModel.Factory {
        val applicationContext = app.applicationContext
        return MainActivityViewModel.Factory(app)
    }

    fun provideNowPlayingFragmentViewModel(context: Context): NowPlayingFragmentViewModel.Factory {
        val applicationContext = context.applicationContext
        return NowPlayingFragmentViewModel.Factory(
            applicationContext as Application
        )
    }
}