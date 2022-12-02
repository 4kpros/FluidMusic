package com.prosabdev.fluidmusic.ui.fragments.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.GenreItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentGenresBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.GenreItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GenresFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentGenresBinding: FragmentGenresBinding

    private lateinit var mGenreItemViewModel: GenreItemViewModel

    private var mGenreItemListAdapter: GenreItemListAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_GENRES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentGenresBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_genres, container,false)
        val view = mFragmentGenresBinding.root

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
        const val TAG = "GenresFragment"

        @JvmStatic
        fun newInstance(pageIndex: Int) =
            GenresFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_GENRES, pageIndex)
                }
            }
    }
}