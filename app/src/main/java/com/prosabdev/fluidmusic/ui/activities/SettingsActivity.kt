package com.prosabdev.fluidmusic.ui.activities

import android.content.Intent
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivitySettingsBinding
import com.prosabdev.fluidmusic.ui.activities.settings.InterfaceSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.LanguageSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class SettingsActivity : AppCompatActivity() {

    private lateinit var mActivitySettingsBinding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

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
            onBackPressedDispatcher.addCallback(this as LifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }

    private fun checkInteractions() {
        mActivitySettingsBinding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mActivitySettingsBinding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    //
                    true
                }
                else -> false
            }
        }
        mActivitySettingsBinding.cardViewLanguage.setOnClickListener{
            startActivity(Intent(this, LanguageSettingsActivity::class.java).apply {})
        }
        mActivitySettingsBinding.cardViewInterface.setOnClickListener{
            startActivity(Intent(this, InterfaceSettingsActivity::class.java).apply {})
        }
        mActivitySettingsBinding.cardViewCoverArt.setOnClickListener{
            //
        }
        mActivitySettingsBinding.cardViewNowPlaying.setOnClickListener{
            //
        }
        mActivitySettingsBinding.cardViewAudio.setOnClickListener{
            //
        }
        mActivitySettingsBinding.cardViewNotifications.setOnClickListener{
            //
        }
        mActivitySettingsBinding.cardViewLibraryScanner.setOnClickListener{
            startActivity(Intent(this, MediaScannerSettingsActivity::class.java).apply {})
        }
        mActivitySettingsBinding.cardViewHeadsetBluetooth.setOnClickListener{
            //
        }
        mActivitySettingsBinding.cardViewAbout.setOnClickListener{
            //
        }
    }

    private fun initViews() {
        ViewInsetModifiersUtils.updateTopViewInsets(mActivitySettingsBinding.coordinatorSettingsActivity)
        ViewInsetModifiersUtils.updateBottomViewInsets(mActivitySettingsBinding.emptyView)
    }
}