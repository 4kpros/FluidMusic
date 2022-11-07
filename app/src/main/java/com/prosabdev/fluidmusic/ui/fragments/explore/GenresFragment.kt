package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.GenreItemAdapter
import com.prosabdev.fluidmusic.models.GenreItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.GenresFragmentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GenresFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mGenresFragmentViewModel: GenresFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

    private var mEmptyBottomAdapter: HeadlinePlayShuffleAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mGenreItemAdapter: GenreItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: LinearLayoutCompat? = null

    private var mGenreList : ArrayList<GenreItem> = ArrayList<GenreItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALL_GENRES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_genres, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            launch {
                initViews(view)
                setupRecyclerViewAdapter()
                observeLiveData()
                checkInteractions()
            }
        }
    }

    private fun checkInteractions() {

    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {

    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
        mLoadingContentProgress = view.findViewById<LinearLayoutCompat>(R.id.loading_content_progress)
    }

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int) =
            GenresFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALL_GENRES, pageIndex)
                }
            }
    }
}