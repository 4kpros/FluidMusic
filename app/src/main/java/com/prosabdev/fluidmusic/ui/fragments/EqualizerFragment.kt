package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEqualizerBinding
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel

@BuildCompat.PrereleaseSdkCheck class EqualizerFragment : Fragment() {

    private var mFragmentEqualizerBinding: FragmentEqualizerBinding? = null

    private val  mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val  mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

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
    ): View? {
        mFragmentEqualizerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_equalizer, container,false)
        val view = mFragmentEqualizerBinding?.root

        initViews()
        setupRecyclerViewAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
        checkInteractions()
    }

    private fun checkInteractions() {
        mFragmentEqualizerBinding?.let { fragmentEqualizerBinding ->
            fragmentEqualizerBinding.topAppBar.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun observeLiveData() {
        mFragmentEqualizerBinding?.let { fragmentEqualizerBinding ->
            InsetModifiersUtils.updateTopViewInsets(fragmentEqualizerBinding.container)
            InsetModifiersUtils.updateBottomViewInsets(fragmentEqualizerBinding.container)
        }
    }

    private fun setupRecyclerViewAdapter() {
        mFragmentEqualizerBinding?.let { fragmentEqualizerBinding ->
            InsetModifiersUtils.updateTopViewInsets(fragmentEqualizerBinding.container)
            InsetModifiersUtils.updateBottomViewInsets(fragmentEqualizerBinding.container)
        }
    }

    private fun initViews() {
        mFragmentEqualizerBinding?.let { fragmentEqualizerBinding ->
            InsetModifiersUtils.updateTopViewInsets(fragmentEqualizerBinding.container)
            InsetModifiersUtils.updateBottomViewInsets(fragmentEqualizerBinding.container)
        }
    }

    companion object {
        const val TAG: String = "EqualizerFragment"

        @JvmStatic
        fun newInstance() =
            EqualizerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}