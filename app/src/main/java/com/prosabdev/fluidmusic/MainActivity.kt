package com.prosabdev.fluidmusic

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
import com.prosabdev.fluidmusic.ui.fragments.MainFragment


@BuildCompat.PrereleaseSdkCheck class MainActivity : AppCompatActivity(){

    private lateinit var mDataBidingView: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(savedInstanceState == null){
            initViews()
            setupFragments()
            setupAudioSettings()
            createMediaBrowserService()
        }
    }

    private fun createMediaBrowserService() {
    }
    private fun setupAudioSettings() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun setupFragments() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.main_activity_fragment_container,
                MainFragment.newInstance(),
                MainFragment.TAG
            )
        }
    }

    private fun initViews(){
    }

    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mConnectionCallbacks: ConnectionCallback = object : ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "ConnectionCallback onConnected")
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
            Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "ConnectionCallback onConnectionSuspended")
        }
        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "ConnectionCallback onConnectionFailed")
        }
    }
    private var mControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "MediaControllerCompat onMetadataChanged : $metadata")
        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "MediaControllerCompat onPlaybackStateChanged : $state")
        }
    }

    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        mediaController.transportControls.prepare()

        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(mControllerCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        com.prosabdev.common.utils.ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        com.prosabdev.common.utils.ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
    }

    public override fun onStart() {
        super.onStart()
        mMediaBrowser?.connect()
        com.prosabdev.common.utils.ImageLoadersUtils.initializeJobWorker(applicationContext)
    }
    public override fun onResume() {
        super.onResume()
        setupAudioSettings()
    }

    override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser?.disconnect()
    }

    override fun onDestroy() {
        com.prosabdev.common.utils.ImageLoadersUtils.stopAllJobsWorkers(applicationContext)
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
