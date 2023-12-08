package com.prosabdev.fluidmusic.ui.activities.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.common.constants.MainConst
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityMediaScannerSettingsBinding
import com.prosabdev.fluidmusic.viewmodels.activities.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.FolderUriTreeViewModel
import kotlinx.coroutines.runBlocking

class MediaScannerSettingsActivity : AppCompatActivity() {

    //Data binding
    private lateinit var mDataBiding : ActivityMediaScannerSettingsBinding

    //View models
    private val mFolderUriTreeViewModel: FolderUriTreeViewModel by viewModels()
    private val mMediaScannerActivityViewModel: MediaScannerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set binding layout and return binding object
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_media_scanner_settings)

        //Load your UI content
        if(savedInstanceState == null){
            runBlocking {
                initViews()
                observeLiveData()
                checkInteractions()
                registerOnBackPressedCallback()
            }
        }
    }

    private fun registerOnBackPressedCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                supportFinishAfterTransition()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    supportFinishAfterTransition()
                }
            })
        }
    }

    private fun checkInteractions() {
        mDataBiding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mDataBiding.cardViewScanDevice.setOnClickListener{
            tryToScanDevice()
        }
        mDataBiding.cardViewSelectFolders.setOnClickListener{
            startActivity(Intent(this, StorageAccessSettingsActivity::class.java).apply {})
        }
        mDataBiding.cardViewRescanAll.setOnClickListener{
            onRescanAllButtonClicked()
        }
        mDataBiding.cardViewRestoreDefault.setOnClickListener{
            onRestoreAllButtonClicked()
        }
        mDataBiding.switchM3uPlaylists.setOnClickListener{
            //
        }
        mDataBiding.switchIgnoreVideos.setOnClickListener{
            //
        }
        mDataBiding.switchAutomaticScanner.setOnClickListener{
            //
        }
        mDataBiding.seekbarIgnoreShortFiles.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                //
            }

        })
        mDataBiding.cardViewScanM3uPlaylists.setOnClickListener{
            mDataBiding.switchM3uPlaylists.isChecked = !mDataBiding.switchM3uPlaylists.isChecked
        }
        mDataBiding.cardViewSkipVideos.setOnClickListener{
            mDataBiding.switchIgnoreVideos.isChecked = !mDataBiding.switchIgnoreVideos.isChecked
        }
        mDataBiding.cardViewAutomaticScan.setOnClickListener{
            mDataBiding.switchAutomaticScanner.isChecked = !mDataBiding.switchAutomaticScanner.isChecked
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
        if(mMediaScannerActivityViewModel.isLoadingInBackground.value == true)
            return
        mMediaScannerActivityViewModel.isLoadingInBackground.value = true
        mMediaScannerActivityViewModel.scanDevice()
    }

    private suspend fun observeLiveData() {
        mFolderUriTreeViewModel.getAll()?.observe(this){
            updateFolderUriTrees(it)
        }
        mMediaScannerActivityViewModel.isLoadingInBackground.observe(this){
            updateLoadingUI(it)
        }
        mMediaScannerActivityViewModel.foldersCounter.observe(this){
            updateFoldersCounterUI(it)
        }
        mMediaScannerActivityViewModel.songsCounter.observe(this){
            updateSongsCounterUI(it)
        }
        mMediaScannerActivityViewModel.playlistsCounter.observe(this){
            updatePlaylistCounterUI(it)
        }
        mMediaScannerActivityViewModel.emptyFolderUriCounter.observe(this){
            updateEmptyFolderUriTreeUI(it)
        }
        mMediaScannerActivityViewModel.outputWorkInfoItems.observe(this){
            checkStatusOfScanningDevice(it)
        }
    }

    private fun checkStatusOfScanningDevice(it: List<WorkInfo>?) {
        if (it.isNullOrEmpty()){
            return
        }
        val workInfo = it[0]

        mMediaScannerActivityViewModel.updateWorkInfoData(workInfo)
        if(workInfo.state.isFinished){
            mMediaScannerActivityViewModel.isLoadingInBackground.value = false
        }
    }
    private fun mediaScannerWorkInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            if (it.isEmpty()){
                return@Observer
            }

            val workInfo = it[0]

            if(workInfo.state.isFinished){
                Log.i(MainConst.TAG, "Device scan finished.")
                mMediaScannerActivityViewModel.updateWorkInfoData(workInfo)
                mMediaScannerActivityViewModel.isLoadingInBackground.value = false
            }else{
                Log.i(MainConst.TAG, "Working on background ...")
            }
        }
    }

    private fun updateEmptyFolderUriTreeUI(it: Int?) {
        if((it ?: 0) > 0){
            fadeInOut(mDataBiding.textSelectFoldersSubTitle)
            fadeInOut(mDataBiding.textSelectFoldersTitle)
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

    private fun updateFolderUriTrees(it: List<com.prosabdev.common.models.FolderUriTree>) {

    }

    private fun updatePlaylistCounterUI(it: Int?) {
        mDataBiding.playlistCounter = it
        Log.i(MainConst.TAG, "Device scan playlists : $it")
    }

    private fun updateSongsCounterUI(it: Int?) {
        mDataBiding.songsCounter = it
        Log.i(MainConst.TAG, "Device scan songs : $it")
    }

    private fun updateFoldersCounterUI(it: Int) {
        mDataBiding.folderCounter = it
        Log.i(MainConst.TAG, "Device scan folders : $it")
    }

    private fun updateLoadingUI(it: Boolean?) {
        mDataBiding.isLoading = it
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorLayout)
        InsetModifiers.updateBottomViewInsets(mDataBiding.container)
    }

    companion object {
        const val TAG = "MediaScannerSettingsActivity"
    }
}