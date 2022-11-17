package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.ArtistItemAdapter
import com.prosabdev.fluidmusic.models.collections.ArtistItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.explore.ArtistsFragmentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ArtistsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mArtistItemAdapter: ArtistItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    private var mArtistList : ArrayList<ArtistItem> = ArrayList<ArtistItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALL_ARTISTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_artists, container, false)

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
        val spanCount = 1

//        mRecyclerView?.adapter = mArtistItemAdapter

        //Add Layout manager
        val layoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
    }

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int) =
            ArtistsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALL_ARTISTS, pageIndex)
                }
            }
    }
}