package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEqualizerBinding
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel

@BuildCompat.PrereleaseSdkCheck class EqualizerFragment : Fragment() {

    private var mDataBiding: FragmentEqualizerBinding? = null

    private val  mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val  mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater,R.layout.fragment_equalizer, container,false)
        val view = mDataBiding?.root

        //Load your UI content
        initViews()
        setupRecyclerViewAdapter()
        observeLiveData()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
        mDataBiding?.let { dataBidingView ->
            dataBidingView.topAppBar.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun observeLiveData() {
        mDataBiding?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiers.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiers.updateBottomViewInsets(dataBidingView.container)
        }
    }

    private fun setupRecyclerViewAdapter() {
        mDataBiding?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiers.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiers.updateBottomViewInsets(dataBidingView.container)
        }
    }

    private fun initViews() {
        mDataBiding?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiers.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiers.updateBottomViewInsets(dataBidingView.container)
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