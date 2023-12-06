package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentFoldersHierarchyBinding

class FoldersHierarchyFragment : Fragment() {

    private lateinit var mDataBinding: FragmentFoldersHierarchyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_folders_hierarchy, container, false)
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        setupRecyclerViewAdapter()
        observeLiveData()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {

    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {
    }

    private fun initViews() {
        //There are no views to be initialized
    }

    companion object {
        const val TAG = "FoldersHierarchyFragment"

        @JvmStatic
        fun newInstance() =
            FoldersHierarchyFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}