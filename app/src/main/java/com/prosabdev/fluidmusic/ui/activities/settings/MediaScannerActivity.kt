package com.prosabdev.fluidmusic.ui.activities.settings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityMediaScannerBinding
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.StorageAccessActivityViewModel

@BuildCompat.PrereleaseSdkCheck class MediaScannerActivity : AppCompatActivity() {

    private lateinit var mActivityMediaScannerBinding : ActivityMediaScannerBinding

    private val mMediaScannerActivityViewModel: MediaScannerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityMediaScannerBinding = DataBindingUtil.setContentView(this, R.layout.activity_media_scanner)

        initViews()
        observeLiveData()
        checkInteractions()
    }

    private fun checkInteractions() {
        mActivityMediaScannerBinding.cardViewScanDevice.setOnClickListener(){
            tryToScanDevice()
        }
        mActivityMediaScannerBinding.cardViewSelectFolders.setOnClickListener(){
            startActivity(Intent(this, StorageAccessActivity::class.java).apply {})
        }
    }

    private fun tryToScanDevice(){
        if(mMediaScannerActivityViewModel.getIsLoadingInBackground().value == false){
            mMediaScannerActivityViewModel.requestLoadDataAsync(this, 0, 1000)
        }
    }

    private fun observeLiveData() {
        mMediaScannerActivityViewModel.getIsLoadingInBackground().observe(this as LifecycleOwner){
            updateLoadingUI(it)
        }
        mMediaScannerActivityViewModel.getFoldersCounter().observe(this as LifecycleOwner){
            updateFoldersCounterUI(it)
        }
        mMediaScannerActivityViewModel.getSongsCounter().observe(this as LifecycleOwner){
            updateSongsCounterUI(it)
        }
        mMediaScannerActivityViewModel.getPlaylistsCounter().observe(this as LifecycleOwner){
            updatePlaylistCounterUI(it)
        }
    }

    private fun updatePlaylistCounterUI(it: Int?) {
        mActivityMediaScannerBinding.playlistCounter = it
    }

    private fun updateSongsCounterUI(it: Int?) {
        mActivityMediaScannerBinding.songsCounter = it
    }

    private fun updateFoldersCounterUI(it: Int) {
        mActivityMediaScannerBinding.folderCounter = it
    }

    private fun updateLoadingUI(it: Boolean?) {
        mActivityMediaScannerBinding.isLoading = it
    }

    private fun initViews() {
        CustomViewModifiers.updateTopViewInsets(mActivityMediaScannerBinding.coordinator)
        CustomViewModifiers.updateBottomViewInsets(mActivityMediaScannerBinding.constraintMainContainer)
    }
}