package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEqualizerBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class EqualizerFragment : Fragment() {

    private lateinit var mFragmentEqualizerBinding: FragmentEqualizerBinding

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mContext = requireContext()
        mActivity = requireActivity()
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

    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {
    }

    private fun initViews() {
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