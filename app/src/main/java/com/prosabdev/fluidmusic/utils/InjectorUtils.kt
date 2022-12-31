package com.prosabdev.fluidmusic.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.prosabdev.common.media.MusicService
import com.prosabdev.common.media.MusicServiceConnection
import com.prosabdev.fluidmusic.viewmodels.activities.MainActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    private fun provideMusicServiceConnection(context: Context): MusicServiceConnection {
        return MusicServiceConnection.getInstance(
            context,
            ComponentName(context, MusicService::class.java)
        )
    }

    fun provideMainActivityViewModel(app: Application): MainActivityViewModel.Factory {
        val applicationContext = app.applicationContext
        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
        return MainActivityViewModel.Factory(app, musicServiceConnection)
    }

    fun provideNowPlayingFragmentViewModel(context: Context): NowPlayingFragmentViewModel.Factory {
        val applicationContext = context.applicationContext
        val musicServiceConnection = provideMusicServiceConnection(applicationContext)
        return NowPlayingFragmentViewModel.Factory(
            applicationContext as Application, musicServiceConnection
        )
    }
}