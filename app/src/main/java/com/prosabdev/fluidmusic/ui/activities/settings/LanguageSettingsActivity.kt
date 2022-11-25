package com.prosabdev.fluidmusic.ui.activities.settings

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityLanguageSettingsBinding
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class LanguageSettingsActivity : AppCompatActivity() {

    private lateinit var mActivityLanguageSettingsBinding : ActivityLanguageSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivityLanguageSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_language_settings)

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
        mActivityLanguageSettingsBinding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initViews() {
        ViewInsetModifiersUtils.updateTopViewInsets(mActivityLanguageSettingsBinding.coordinatorSettingsActivity)
        ViewInsetModifiersUtils.updateBottomViewInsets(mActivityLanguageSettingsBinding.emptyView)
    }
}