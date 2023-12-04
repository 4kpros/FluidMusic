package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.media.browse.MediaBrowser
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.android.material.color.DynamicColors
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.fluidmusic.databinding.ActivityMainBinding
import com.prosabdev.fluidmusic.media.PlaybackService
import com.prosabdev.fluidmusic.ui.fragments.MainFragment

@OptIn(BuildCompat.PrereleaseSdkCheck::class) @UnstableApi class MainActivity : AppCompatActivity(){

    private lateinit var mDataBiding: ActivityMainBinding

    private var mSessionToken: SessionToken? = null
    private var mMediaControllerFuture: ListenableFuture<MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this@MainActivity.application)

        //Set content with data biding util
        mDataBiding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        //Load your UI content
        if(savedInstanceState == null){
            initViews()
            setupFragments()
        }
    }

    private fun setupFragments() {
        this.supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.main_activity_fragment_container,
                MainFragment.newInstance(),
                MainFragment.TAG
            )
        }
    }

    private fun initViews(){
        //There are no views to be initialized
    }

    private fun setupMediaController() {
        mSessionToken = SessionToken(this@MainActivity, ComponentName(this@MainActivity, PlaybackService::class.java))
        mMediaControllerFuture = MediaController.Builder(this@MainActivity, mSessionToken!!).buildAsync()
        mMediaControllerFuture?.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                // mMediaControllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )
        ImageLoaders.initializeJobs(applicationContext)
    }

    public override fun onStart() {
        super.onStart()
        setupMediaController()
    }

    public override fun onResume() {
        super.onResume()
        ImageLoaders.resumeJobs()
    }
    override fun onPause() {
        super.onPause()
        ImageLoaders.pauseJobs()
    }
    public override fun onStop() {
        mMediaControllerFuture?.get()?.release()
        ImageLoaders.stopAllJobs(applicationContext)
        super.onStop()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
