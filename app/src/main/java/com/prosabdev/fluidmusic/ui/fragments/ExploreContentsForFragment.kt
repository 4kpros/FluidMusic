package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentExploreContentsForBinding
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.ConstantValues


class ExploreContentsForFragment : Fragment() {
    private var mLoadSongFromSource: String? = null
    private var mLoadSongFromSourceValue: String? = null

    private lateinit var mFragmentExploreContentsForBinding: FragmentExploreContentsForBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        arguments?.let {
            mLoadSongFromSource = it.getString(CONTENT_SOURCE)
            mLoadSongFromSourceValue = it.getString(CONTENT_SOURCE_VALUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentExploreContentsForBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_explore_contents_for,container,false)
        val view = mFragmentExploreContentsForBinding.root

        initViews()

        return view
    }

    private fun initViews() {
        when (mLoadSongFromSource) {
            ArtistsFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Artist : ${mLoadSongFromSourceValue}"
            }
            AlbumsFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Album : ${mLoadSongFromSourceValue}"
            }
            FoldersFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Folder : ${mLoadSongFromSourceValue}"
            }
            AlbumArtistsFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Album artist : ${mLoadSongFromSourceValue}"
            }
            GenresFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Genre : ${mLoadSongFromSourceValue}"
            }
            ComposersFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Composer : ${mLoadSongFromSourceValue}"
            }
            YearsFragment.TAG -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Year : ${mLoadSongFromSourceValue}"
            }
        }
    }


    companion object {
        const val TAG = "ExploreContentsForFragment"
        const val CONTENT_SOURCE = "CONTENT_SOURCE"
        const val CONTENT_SOURCE_VALUE = "CONTENT_SOURCE_VALUE"

        @JvmStatic
        fun newInstance(loadSongFromSource: String?, value : String?) =
            ExploreContentsForFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTENT_SOURCE, loadSongFromSource)
                    putString(CONTENT_SOURCE_VALUE, value)
                }
            }
    }
}