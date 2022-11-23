package com.prosabdev.fluidmusic.ui.fragments.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.AlbumArtistItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAlbumArtistsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumArtistItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumArtistsFragment : Fragment() {
    private var mPageIndex: Int = -1

    private lateinit var mFragmentAlbumArtistsBinding: FragmentAlbumArtistsBinding

    private lateinit var mAlbumArtistItemViewModel: AlbumArtistItemViewModel

    private var mEmptyBottomSpaceAdapter: HeadlinePlayShuffleAdapter? = null
    private var mAlbumArtistItemAdapter: AlbumArtistItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALBUM_ARTISTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentAlbumArtistsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_album_artists, container,false)
        val view = mFragmentAlbumArtistsBinding.root

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
        fun newInstance(pageIndex: Int) =
            AlbumArtistsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALBUM_ARTISTS, pageIndex)
                }
            }
    }
}