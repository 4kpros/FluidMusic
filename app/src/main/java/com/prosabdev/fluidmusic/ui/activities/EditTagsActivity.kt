package com.prosabdev.fluidmusic.ui.activities

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityEditTagsBinding

@BuildCompat.PrereleaseSdkCheck class EditTagsActivity : AppCompatActivity() {
    private lateinit var mDataBidingView : ActivityEditTagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_edit_tags)

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
    }

    private fun initViews() {
        com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.constraintNestedScrollView)
    }

    companion object {
        const val TAG = "EditTagsActivity"
    }
}