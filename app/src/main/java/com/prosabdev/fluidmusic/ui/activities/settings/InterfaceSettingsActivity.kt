package com.prosabdev.fluidmusic.ui.activities.settings

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityInterfaceSettingsBinding
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class InterfaceSettingsActivity : AppCompatActivity() {

    private lateinit var mDataBidingView : ActivityInterfaceSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_interface_settings)

        initViews()
        checkInteractions()
        registerOnBackPressedCallback()
    }
    private fun buildEnterTransition(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(R.id.coordinator_settings_activity)
            duration = 250
            pathMotion = MaterialArcMotion()
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
    }
    private fun buildExitTransition(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(R.id.coordinator_settings_activity)
            duration = 250
            pathMotion = MaterialArcMotion()
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
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
    }

    private fun initViews() {
        InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorSettingsActivity)
        InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.emptyView)
    }

    companion object {
        const val TAG = "InterfaceSettingsActivity"
    }
}