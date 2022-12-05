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
            mLoadSongFromSource = it.getString(ConstantValues.ARGS_EXPLORE_MUSIC_CONTENT)
            mLoadSongFromSourceValue = it.getString(ConstantValues.ARGS_EXPLORE_MUSIC_CONTENT_VALUE)
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
            ConstantValues.EXPLORE_ARTISTS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Artist : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_ALBUMS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Album : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_FOLDERS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Folder : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_ALBUM_ARTISTS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Album artist : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_GENRES -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Genre : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_COMPOSERS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Composer : ${mLoadSongFromSourceValue}"
            }
            ConstantValues.EXPLORE_YEARS -> {
                mFragmentExploreContentsForBinding.topAppBar.title = "Year : ${mLoadSongFromSourceValue}"
            }
        }
    }


    companion object {
        const val TAG = "ExploreContentsForFragment"

        @JvmStatic
        fun newInstance(loadSongFromSource: String?, value : String?) =
            ExploreContentsForFragment().apply {
                arguments = Bundle().apply {
                    putString(ConstantValues.ARGS_EXPLORE_MUSIC_CONTENT, loadSongFromSource)
                    putString(ConstantValues.ARGS_EXPLORE_MUSIC_CONTENT_VALUE, value)
                }
            }
    }
}