package com.prosabdev.fluidmusic.ui.activities.settings

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
import com.prosabdev.fluidmusic.databinding.ActivityInterfaceSettingsBinding

class InterfaceSettingsActivity : AppCompatActivity() {

    private lateinit var mDataBiding : ActivityInterfaceSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set content with data biding util
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_interface_settings)

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
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorSettingsActivity)
        InsetModifiers.updateBottomViewInsets(mDataBiding.emptyView)
    }

    companion object {
        const val TAG = "InterfaceSettingsActivity"
    }
}