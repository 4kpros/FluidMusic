package com.prosabdev.fluidmusic.ui.fragments.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.FolderItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentFoldersBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.FolderItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FoldersFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentFoldersBinding: FragmentFoldersBinding

    private lateinit var mFolderItemViewModel: FolderItemViewModel

    private var mFolderItemAdapter: FolderItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_FOLDERS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentFoldersBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_folders, container,false)
        val view = mFragmentFoldersBinding.root

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
        fun newInstance(pageIndex: Int) =
            FoldersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_FOLDERS, pageIndex)
                }
            }
    }
}