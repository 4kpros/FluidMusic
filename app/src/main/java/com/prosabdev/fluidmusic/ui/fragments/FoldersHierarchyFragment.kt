package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentFoldersHierarchyBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FoldersHierarchyFragment : Fragment() {

    private lateinit var mFragmentFoldersHierarchyBinding: FragmentFoldersHierarchyBinding

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
    ): View? {
        mFragmentFoldersHierarchyBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_folders_hierarchy, container,false)
        val view = mFragmentFoldersHierarchyBinding.root

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
        const val TAG = "FoldersHierarchyFragment"

        @JvmStatic
        fun newInstance() =
            FoldersHierarchyFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}