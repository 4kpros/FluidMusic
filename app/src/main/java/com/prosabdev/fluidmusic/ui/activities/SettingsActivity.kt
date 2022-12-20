package com.prosabdev.fluidmusic.ui.activities

import android.content.Intent
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivitySettingsBinding
import com.prosabdev.fluidmusic.ui.activities.settings.InterfaceSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.LanguageSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class SettingsActivity : AppCompatActivity() {

    private lateinit var mDataBidingView : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        initViews()
        checkInteractions()
        registerOnBackPressedCallback()
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

    private fun checkInteractions() {
        mDataBidingView.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mDataBidingView.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    //
                    true
                }
                else -> false
            }
        }
        mDataBidingView.cardViewLanguage.setOnClickListener{
            startActivity(Intent(this, LanguageSettingsActivity::class.java).apply {})
        }
        mDataBidingView.cardViewInterface.setOnClickListener{
            startActivity(Intent(this, InterfaceSettingsActivity::class.java).apply {})
        }
        mDataBidingView.cardViewCoverArt.setOnClickListener{
            //
        }
        mDataBidingView.cardViewNowPlaying.setOnClickListener{
            //
        }
        mDataBidingView.cardViewAudio.setOnClickListener{
            //
        }
        mDataBidingView.cardViewNotifications.setOnClickListener{
            //
        }
        mDataBidingView.cardViewLibraryScanner.setOnClickListener{
            startActivity(Intent(this, MediaScannerSettingsActivity::class.java).apply {})
        }
        mDataBidingView.cardViewHeadsetBluetooth.setOnClickListener{
            //
        }
        mDataBidingView.cardViewAbout.setOnClickListener{
            //
        }
    }

    private fun initViews() {
        InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.container)
    }

    companion object {
        const val TAG = "SettingsActivity"
    }
}