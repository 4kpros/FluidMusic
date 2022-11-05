package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.AlbumItemAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.models.AlbumItem
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlbumsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: AlbumItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: LinearProgressIndicator? = null

    private var mAlbumList : ArrayList<AlbumItem> = ArrayList<AlbumItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALBUMS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_albums, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        runBlocking {
            launch {
                initViews(view)
                setupRecyclerViewAdapter()
                observeLiveData()
                checkInteractions()
            }
        }

        return view
    }

    private fun checkInteractions() {

    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {

    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
        mLoadingContentProgress = view.findViewById<LinearProgressIndicator>(R.id.loading_content_progress)
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