package com.prosabdev.fluidmusic.ui.activities.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.work.WorkInfo
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityMediaScannerSettingsBinding
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.activities.ActivityViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.activities.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MediaScannerSettingsActivity : AppCompatActivity() {

    private lateinit var mActivityMediaScannerSettingsBinding : ActivityMediaScannerSettingsBinding

    private lateinit var mSongItemViewModel: SongItemViewModel
    private lateinit var mFolderUriTreeViewModel: FolderUriTreeViewModel
    private lateinit var mMediaScannerActivityViewModel: MediaScannerActivityViewModel

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
        mSongItemViewModel = ModelsViewModelFactory(this.baseContext).create(SongItemViewModel::class.java)
        mFolderUriTreeViewModel = ModelsViewModelFactory(this.baseContext).create(FolderUriTreeViewModel::class.java)

        mMediaScannerActivityViewModel = ActivityViewModelFactory(this.application).create(
            MediaScannerActivityViewModel::class.java)
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
        mMediaScannerActivityViewModel.scanDevice(ConstantValues.MEDIA_SCANNER_WORKER_METHOD_ADD_NEW)
    }

    private suspend fun observeLiveData() {
        mFolderUriTreeViewModel.getAll()?.observe(this as LifecycleOwner){
            updateFolderUriTrees(it)
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
        mMediaScannerActivityViewModel.getOutputWorkInfoList().observe(this){
            checkStatusOfScanningDevice(it)
        }
    }

    private fun checkStatusOfScanningDevice(it: List<WorkInfo>?) {
        if (it.isNullOrEmpty()){
            return
        }

        val workInfo = it[0]

        if(workInfo.state.isFinished){
            Log.i(ConstantValues.TAG, "Device scan finished.")
            mMediaScannerActivityViewModel.updateWorkInfoData(workInfo)
            mMediaScannerActivityViewModel.setIsLoadingInBackground(false)
        }else{
            Log.i(ConstantValues.TAG, "Working on background ...")
        }
    }
    private fun mediaScannerWorkInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            if (it.isNullOrEmpty()){
                return@Observer
            }

            val workInfo = it[0]

            if(workInfo.state.isFinished){
                Log.i(ConstantValues.TAG, "Device scan finished.")
                mMediaScannerActivityViewModel.updateWorkInfoData(workInfo)
                mMediaScannerActivityViewModel.setIsLoadingInBackground(false)
            }else{
                Log.i(ConstantValues.TAG, "Working on background ...")
            }
        }
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

    }

    private fun updatePlaylistCounterUI(it: Int?) {
        mActivityMediaScannerSettingsBinding.playlistCounter = it
        Log.i(ConstantValues.TAG, "Device scan playlists : $it")
    }

    private fun updateSongsCounterUI(it: Int?) {
        mActivityMediaScannerSettingsBinding.songsCounter = it
        Log.i(ConstantValues.TAG, "Device scan songs : $it")
    }

    private fun updateFoldersCounterUI(it: Int) {
        mActivityMediaScannerSettingsBinding.folderCounter = it
        Log.i(ConstantValues.TAG, "Device scan folders : $it")
    }

    private fun updateLoadingUI(it: Boolean?) {
        mActivityMediaScannerSettingsBinding.isLoading = it
    }

    private fun initializeUIs() {
        CustomViewModifiers.updateTopViewInsets(mActivityMediaScannerSettingsBinding.coordinatorSettingsActivity)
        CustomViewModifiers.updateBottomViewInsets(mActivityMediaScannerSettingsBinding.emptyView)
    }
}