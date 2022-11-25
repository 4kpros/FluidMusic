package com.prosabdev.fluidmusic.ui.activities.settings

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityInterfaceSettingsBinding
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class InterfaceSettingsActivity : AppCompatActivity() {

    private lateinit var mActivityInterfaceSettingsBinding : ActivityInterfaceSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivityInterfaceSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_interface_settings)

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
        mActivityInterfaceSettingsBinding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initViews() {
        ViewInsetModifiersUtils.updateTopViewInsets(mActivityInterfaceSettingsBinding.coordinatorSettingsActivity)
        ViewInsetModifiersUtils.updateBottomViewInsets(mActivityInterfaceSettingsBinding.emptyView)
    }
}