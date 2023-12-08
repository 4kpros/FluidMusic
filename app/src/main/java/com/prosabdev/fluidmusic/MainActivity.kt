package com.prosabdev.fluidmusic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.media3.common.util.UnstableApi
import com.google.android.material.color.DynamicColors
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.fluidmusic.databinding.ActivityMainBinding
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.activities.MainActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerDataViewModel

@UnstableApi class MainActivity : AppCompatActivity(){

    //Data binding
    private lateinit var mDataBinding: ActivityMainBinding

    //View models
    private val mMainActivityViewModel by viewModels<MainActivityViewModel> {
        InjectorUtils.provideMainActivityViewModel(application)
    }
    private val mMediaPlayerDataViewModel by viewModels<MediaPlayerDataViewModel>()
    private val mMediaControllerViewModel by viewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mMediaPlayerDataViewModel.mediaEventsListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this@MainActivity.application)

        //Set binding layout and return binding object
        mDataBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

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

    //Activity life cycle
    public override fun onStart() {
        super.onStart()
        mMediaControllerViewModel.setupMediaController(this@MainActivity)
        ImageLoaders.initializeJobs(applicationContext)
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
        super.onStop()
        ImageLoaders.stopAllJobs(applicationContext)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
