package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEqualizerBinding
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class EqualizerFragment : Fragment() {

    private lateinit var mFragmentEqualizerBinding: FragmentEqualizerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentEqualizerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_equalizer, container,false)
        val view = mFragmentEqualizerBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            setupRecyclerViewAdapter()
            observeLiveData()
            checkInteractions()
        }
    }

    private fun checkInteractions() {
        mFragmentEqualizerBinding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {
    }

    private fun initViews() {
        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentEqualizerBinding.coordinatorLayout)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentEqualizerBinding.constraintContainer)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EqualizerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}