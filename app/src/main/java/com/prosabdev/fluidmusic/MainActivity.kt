package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.databinding.ActivityMainBinding
import com.prosabdev.fluidmusic.service.MediaPlaybackService
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@BuildCompat.PrereleaseSdkCheck class MainActivity : AppCompatActivity(){

    private lateinit var mActivityMainBinding: ActivityMainBinding

    private val mMainFragment = MainFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(savedInstanceState == null){
            initViews()
            setupFragments()
            setupAudioSettings()
            createMediaBrowserService()
        }
    }

    override fun onDestroy() {
        MainScope().launch {
            ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
        }
        super.onDestroy()
    }

    private fun createMediaBrowserService() {
        mMediaBrowser = MediaBrowserCompat(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null
        )
    }
    private fun setupAudioSettings() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun setupFragments() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_activity_fragment_container, mMainFragment)
        }
    }

    private fun initViews(){
    }

    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mConnectionCallbacks: ConnectionCallback = object : ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnected")
            mMediaBrowser?.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    this@MainActivity.applicationContext, // Context
                    token!!
                )
                MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
            }
            buildTransportControls()
        }
        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnectionSuspended")
        }
        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnectionFailed")
        }
    }
    private var mControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i(ConstantValues.TAG, "MediaControllerCompat onMetadataChanged : $metadata")
        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.i(ConstantValues.TAG, "MediaControllerCompat onPlaybackStateChanged : $state")
        }
    }

    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        mediaController.transportControls.prepare()

        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(mControllerCallback)
    }

    override fun onPause() {
        MainScope().launch {
            ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
        }
        super.onPause()
    }

    public override fun onStart() {
        ImageLoadersUtils.initializeJobWorker(applicationContext)
        super.onStart()
        mMediaBrowser?.connect()
    }
    public override fun onResume() {
        ImageLoadersUtils.initializeJobWorker(applicationContext)
        super.onResume()
        setupAudioSettings()
    }

    public override fun onStop() {
        MainScope().launch {
            ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
        }
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser?.disconnect()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
