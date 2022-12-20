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
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityMediaScannerSettingsBinding
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.activities.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.FolderUriTreeViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MediaScannerSettingsActivity : AppCompatActivity() {

    private lateinit var mDataBidingView : ActivityMediaScannerSettingsBinding

    private val mFolderUriTreeViewModel: FolderUriTreeViewModel by viewModels()
    private val mMediaScannerActivityViewModel: MediaScannerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_media_scanner_settings)

        initViews()
        MainScope().launch {
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
        mDataBidingView.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mDataBidingView.cardViewScanDevice.setOnClickListener{
            tryToScanDevice()
        }
        mDataBidingView.cardViewSelectFolders.setOnClickListener{
            startActivity(Intent(this, StorageAccessSettingsActivity::class.java).apply {})
        }
        mDataBidingView.cardViewRescanAll.setOnClickListener{
            onRescanAllButtonClicked()
        }
        mDataBidingView.cardViewRestoreDefault.setOnClickListener{
            onRestoreAllButtonClicked()
        }
        mDataBidingView.switchM3uPlaylists.setOnClickListener{
            //
        }
        mDataBidingView.switchIgnoreVideos.setOnClickListener{
            //
        }
        mDataBidingView.switchAutomaticScanner.setOnClickListener{
            //
        }
        mDataBidingView.seekbarIgnoreShortFiles.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                //
            }

        })
        mDataBidingView.cardViewScanM3uPlaylists.setOnClickListener{
            mDataBidingView.switchM3uPlaylists.isChecked = !mDataBidingView.switchM3uPlaylists.isChecked
        }
        mDataBidingView.cardViewSkipVideos.setOnClickListener{
            mDataBidingView.switchIgnoreVideos.isChecked = !mDataBidingView.switchIgnoreVideos.isChecked
        }
        mDataBidingView.cardViewAutomaticScan.setOnClickListener{
            mDataBidingView.switchAutomaticScanner.isChecked = !mDataBidingView.switchAutomaticScanner.isChecked
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
        mMediaScannerActivityViewModel.scanDevice()
    }

    private suspend fun observeLiveData() {
        mFolderUriTreeViewModel.getAll()?.observe(this){
            updateFolderUriTrees(it)
        }
        mMediaScannerActivityViewModel.getIsLoadingInBackground().observe(this){
            updateLoadingUI(it)
        }
        mMediaScannerActivityViewModel.getFoldersCounter().observe(this){
            updateFoldersCounterUI(it)
        }
        mMediaScannerActivityViewModel.getSongsCounter().observe(this){
            updateSongsCounterUI(it)
        }
        mMediaScannerActivityViewModel.getPlaylistsCounter().observe(this){
            updatePlaylistCounterUI(it)
        }
        mMediaScannerActivityViewModel.getEmptyFolderUriCounter().observe(this){
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

        mMediaScannerActivityViewModel.updateWorkInfoData(workInfo)
        if(workInfo.state.isFinished){
            Log.i(ConstantValues.TAG, "Device scan finished.")
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
            fadeInOut(mDataBidingView.textSelectFoldersSubTitle)
            fadeInOut(mDataBidingView.textSelectFoldersTitle)
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
        mDataBidingView.playlistCounter = it
        Log.i(ConstantValues.TAG, "Device scan playlists : $it")
    }

    private fun updateSongsCounterUI(it: Int?) {
        mDataBidingView.songsCounter = it
        Log.i(ConstantValues.TAG, "Device scan songs : $it")
    }

    private fun updateFoldersCounterUI(it: Int) {
        mDataBidingView.folderCounter = it
        Log.i(ConstantValues.TAG, "Device scan folders : $it")
    }

    private fun updateLoadingUI(it: Boolean?) {
        mDataBidingView.isLoading = it
    }

    private fun initViews() {
        InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.container)
    }

    companion object {
        const val TAG = "MediaScannerSettingsActivity"
    }
}