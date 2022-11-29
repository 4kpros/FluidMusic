package com.prosabdev.fluidmusic.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentSettingsBinding
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils

@BuildCompat.PrereleaseSdkCheck class SettingsFragment : Fragment() {

    private lateinit var mFragmentSettingsBinding : FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentSettingsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)
        val view = mFragmentSettingsBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
    }

    private fun buildEnterTransition(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(R.id.coordinator_settings_activity)
//            duration = 250
            pathMotion = MaterialArcMotion()
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
    }
    private fun buildExitTransition(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            addTarget(R.id.coordinator_settings_activity)
//            duration = 250
            pathMotion = MaterialArcMotion()
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
        }
    }

    private fun checkInteractions() {
        mFragmentSettingsBinding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        mFragmentSettingsBinding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    //
                    true
                }
                else -> false
            }
        }
        mFragmentSettingsBinding.cardViewLanguage.setOnClickListener{
        }
        mFragmentSettingsBinding.cardViewInterface.setOnClickListener{
        }
        mFragmentSettingsBinding.cardViewCoverArt.setOnClickListener{
            //
        }
        mFragmentSettingsBinding.cardViewNowPlaying.setOnClickListener{
            //
        }
        mFragmentSettingsBinding.cardViewAudio.setOnClickListener{
            //
        }
        mFragmentSettingsBinding.cardViewNotifications.setOnClickListener{
            //
        }
        mFragmentSettingsBinding.cardViewLibraryScanner.setOnClickListener{
        }
        mFragmentSettingsBinding.cardViewHeadsetBluetooth.setOnClickListener{
            //
        }
        mFragmentSettingsBinding.cardViewAbout.setOnClickListener{
            //
        }
    }

    private fun initViews() {
        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentSettingsBinding.coordinatorLayout)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentSettingsBinding.coordinatorLayout)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}