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

    private var mDataBidingView: FragmentEqualizerBinding? = null

    private val  mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val  mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_equalizer, container,false)
        val view = mDataBidingView?.root

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
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.topAppBar.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun observeLiveData() {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.container)
        }
    }

    private fun setupRecyclerViewAdapter() {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.container)
        }
    }

    private fun initViews() {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(dataBidingView.container)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.container)
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