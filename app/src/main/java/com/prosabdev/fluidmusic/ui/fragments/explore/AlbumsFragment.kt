package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.AlbumItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAlbumsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.views.explore.AlbumItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentAlbumsBinding: FragmentAlbumsBinding

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private lateinit var mAlbumItemViewModel: AlbumItemViewModel

    private var mEmptyBottomSpaceAdapter: HeadlinePlayShuffleAdapter? = null
    private var mAlbumItemAdapter: AlbumItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALBUMS)
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentAlbumsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_albums, container,false)
        val view = mFragmentAlbumsBinding.root

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
        //
    }

    private fun observeLiveData() {
        //
    }

    private fun setupRecyclerViewAdapter() {
        //
    }

    private fun initViews() {
        //
    }

    companion object {
        @JvmStatic
        fun newInstance(pageIndex : Int) =
            AlbumsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALBUMS, pageIndex)
                }
            }
    }
}