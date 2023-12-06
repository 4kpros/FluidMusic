package com.prosabdev.fluidmusic.ui.activities

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
import com.prosabdev.fluidmusic.databinding.ActivityEditTagsBinding

class EditTagsActivity : AppCompatActivity() {

    private lateinit var mDataBiding : ActivityEditTagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set content with data biding util
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_edit_tags)

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
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorLayout)
        InsetModifiers.updateBottomViewInsets(mDataBiding.constraintNestedScrollView)
    }

    companion object {
        const val TAG = "EditTagsActivity"
    }
}