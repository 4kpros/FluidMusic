package com.prosabdev.fluidmusic.ui.activities.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.work.*
import androidx.work.OneTimeWorkRequest
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityMediaScannerSettingsBinding
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.bus.DatabaseAccessApplication
import com.prosabdev.fluidmusic.services.worker.MediaScannerWorker
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.views.activities.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.views.activities.MediaScannerActivityViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MediaScannerSettingsActivity : AppCompatActivity() {

    private lateinit var mActivityMediaScannerSettingsBinding : ActivityMediaScannerSettingsBinding

    private lateinit var mSongItemViewModel: SongItemViewModel
    private lateinit var mMediaScannerActivityViewModel: MediaScannerActivityViewModel
    private lateinit var mFolderUriTreeViewModel: FolderUriTreeViewModel

    private var mScanDeviceWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<MediaScannerWorker>()
            .setInputData(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_METHOD to ConstantValues.MEDIA_SCANNER_WORKER_METHOD_ADD_NEW))
            .build()

    private var mFolderUriList: List<FolderUriTree> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityMediaScannerSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_media_scanner_settings)

        initializeUIs()
        MainScope().launch {
            initViewsModels()
            observeLiveData()
            checkInteractions()
            registerOnBackPressedCallback()
        }
    }

    private fun registerOnBackPressedCallback() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }

    private fun initViewsModels() {
        mSongItemViewModel = SongItemViewModelFactory(
            (this.application as DatabaseAccessApplication).database.songItemDao()
        ).create(SongItemViewModel::class.java)
        mMediaScannerActivityViewModel = MediaScannerActivityViewModelFactory().create(
            MediaScannerActivityViewModel::class.java)
        mFolderUriTreeViewModel = FolderUriTreeViewModelFactory(
            (this.application as DatabaseAccessApplication).database.folderUriTreeDao()
        ).create(FolderUriTreeViewModel::class.java)
    }

    private fun checkInteractions() {
        mActivityMediaScannerSettingsBinding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mActivityMediaScannerSettingsBinding.cardViewScanDevice.setOnClickListener{
            tryToScanDevice()
        }
        mActivityMediaScannerSettingsBinding.cardViewSelectFolders.setOnClickListener{
            startActivity(Intent(this, StorageAccessSettingsActivity::class.java).apply {})
        }
        mActivityMediaScannerSettingsBinding.cardViewRescanAll.setOnClickListener{
            onRescanAllButtonClicked()
        }
        mActivityMediaScannerSettingsBinding.cardViewRestoreDefault.setOnClickListener{
            onRestoreAllButtonClicked()
        }
        mActivityMediaScannerSettingsBinding.switchM3uPlaylists.setOnClickListener{
            //
        }
        mActivityMediaScannerSettingsBinding.switchIgnoreVideos.setOnClickListener{
            //
        }
        mActivityMediaScannerSettingsBinding.switchAutomaticScanner.setOnClickListener{
            //
        }
        mActivityMediaScannerSettingsBinding.seekbarIgnoreShortFiles.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                //
            }

        })
        mActivityMediaScannerSettingsBinding.cardViewScanM3uPlaylists.setOnClickListener{
            mActivityMediaScannerSettingsBinding.switchM3uPlaylists.isChecked = !mActivityMediaScannerSettingsBinding.switchM3uPlaylists.isChecked
        }
        mActivityMediaScannerSettingsBinding.cardViewSkipVideos.setOnClickListener{
            mActivityMediaScannerSettingsBinding.switchIgnoreVideos.isChecked = !mActivityMediaScannerSettingsBinding.switchIgnoreVideos.isChecked
        }
        mActivityMediaScannerSettingsBinding.cardViewAutomaticScan.setOnClickListener{
            mActivityMediaScannerSettingsBinding.switchAutomaticScanner.isChecked = !mActivityMediaScannerSettingsBinding.switchAutomaticScanner.isChecked
        }
    }

    private fun onRestoreAllButtonClicked() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Restore all settings for this page ?")
            .setMessage("All current settings will benn reset ton default values including your selected folders. Continue anyways ?")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Restore") { dialog, _ ->
                onRestoreAll(dialog)
            }
            .show()
    }

    private fun onRestoreAll(dialog: DialogInterface) {
        //
        dialog.dismiss()
    }

    private fun onRescanAllButtonClicked() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear all scanned data from your database an rescan ?")
            .setMessage("Please note that all current songs cached on your database will be removed(not including your existing playlists) and reloaded. It could take more time than default scan. Do you want to proceed ?")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Start full scan") { dialog, _ ->
                onRescanAll(dialog)
            }
            .show()
    }

    private fun onRescanAll(dialog: DialogInterface) {
        //
        dialog.dismiss()
    }

    private fun tryToScanDevice() {
        if(mMediaScannerActivityViewModel.getIsLoadingInBackground().value == true)
            return
        mMediaScannerActivityViewModel.setIsLoadingInBackground(true)

        WorkManager
            .getInstance(this@MediaScannerSettingsActivity)
            .enqueue(mScanDeviceWorkRequest)
    }

    private fun observeLiveData() {
        lifecycle.coroutineScope.launch {
            mFolderUriTreeViewModel.getAllFolderUriTrees().collect {
                updateFolderUriTrees(it)
            }
        }
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
        mMediaScannerActivityViewModel.getEmptyFolderUriCounter().observe(this as LifecycleOwner){
            updateEmptyFolderUriTreeUI(it)
        }
        WorkManager
            .getInstance(this@MediaScannerSettingsActivity)
            .getWorkInfoByIdLiveData(mScanDeviceWorkRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    mMediaScannerActivityViewModel.setIsLoadingInBackground(false)
                }
            })
    }

    private fun updateEmptyFolderUriTreeUI(it: Int?) {
        if((it ?: 0) > 0){
            fadeInOut(mActivityMediaScannerSettingsBinding.textSelectFoldersSubTitle)
            fadeInOut(mActivityMediaScannerSettingsBinding.textSelectFoldersTitle)
        }
    }

    private fun fadeInOut(view: View, duration: Long = 200, nextStep : Boolean = true){
        view.apply {
            animate()
                .alpha(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.apply {
                            alpha = 0.0f
                            animate()
                                .alpha(1f)
                                .setInterpolator(AccelerateDecelerateInterpolator())
                                .setDuration(duration/2)
                                .setListener(object : AnimatorListenerAdapter(){
                                    override fun onAnimationEnd(animation: Animator) {
                                        if(nextStep){
                                            fadeInOut(view, 150, false)
                                            fadeInOut(view, 150, false)
                                        }
                                    }
                                })
                        }
                    }
                })
        }
    }

    private fun updateFolderUriTrees(it: List<FolderUriTree>) {
        mFolderUriList = it
    }

    private fun updatePlaylistCounterUI(it: Int?) {
        mActivityMediaScannerSettingsBinding.playlistCounter = it
    }

    private fun updateSongsCounterUI(it: Int?) {
        mActivityMediaScannerSettingsBinding.songsCounter = it
    }

    private fun updateFoldersCounterUI(it: Int) {
        mActivityMediaScannerSettingsBinding.folderCounter = it
    }

    private fun updateLoadingUI(it: Boolean?) {
        mActivityMediaScannerSettingsBinding.isLoading = it
    }

    private fun initializeUIs() {
        CustomViewModifiers.updateTopViewInsets(mActivityMediaScannerSettingsBinding.coordinatorSettingsActivity)
        CustomViewModifiers.updateBottomViewInsets(mActivityMediaScannerSettingsBinding.emptyView)
    }
}