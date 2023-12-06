package com.prosabdev.fluidmusic.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.DynamicColors
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivitySettingsBinding
import com.prosabdev.fluidmusic.ui.activities.settings.InterfaceSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.LanguageSettingsActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var mDataBiding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set content with data biding util
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        //Load your UI content
        if(savedInstanceState == null){
            initViews()
            checkInteractions()
            registerOnBackPressedCallback()
        }
    }

    private fun registerOnBackPressedCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
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
        mDataBiding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mDataBiding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    //
                    true
                }
                else -> false
            }
        }
        mDataBiding.cardViewLanguage.setOnClickListener{
            startActivity(Intent(this, LanguageSettingsActivity::class.java).apply {})
        }
        mDataBiding.cardViewInterface.setOnClickListener{
            startActivity(Intent(this, InterfaceSettingsActivity::class.java).apply {})
        }
        mDataBiding.cardViewCoverArt.setOnClickListener{
            //
        }
        mDataBiding.cardViewNowPlaying.setOnClickListener{
            //
        }
        mDataBiding.cardViewAudio.setOnClickListener{
            //
        }
        mDataBiding.cardViewNotifications.setOnClickListener{
            //
        }
        mDataBiding.cardViewLibraryScanner.setOnClickListener{
            startActivity(Intent(this, MediaScannerSettingsActivity::class.java).apply {})
        }
        mDataBiding.cardViewHeadsetBluetooth.setOnClickListener{
            //
        }
        mDataBiding.cardViewAbout.setOnClickListener{
            //
        }
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorLayout)
        InsetModifiers.updateBottomViewInsets(mDataBiding.container)
    }

    companion object {
        const val TAG = "SettingsActivity"
    }
}