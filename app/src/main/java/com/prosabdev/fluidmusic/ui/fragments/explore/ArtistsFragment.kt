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
import com.prosabdev.fluidmusic.adapters.explore.ArtistItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentArtistsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.views.explore.ArtistItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ArtistsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentArtistsBinding: FragmentArtistsBinding

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private lateinit var mArtistItemViewModel: ArtistItemViewModel

    private var mEmptyBottomSpaceAdapter: HeadlinePlayShuffleAdapter? = null
    private var mArtistItemAdapter: ArtistItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ARTISTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentArtistsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_artists, container,false)
        val view = mFragmentArtistsBinding.root

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
            ArtistsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ARTISTS, pageIndex)
                }
            }
    }
}