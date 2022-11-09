package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.AlbumItemAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.models.AlbumItem
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.utils.adapters.SelectableRecycleViewAdapter
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlbumsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

    private var mEmptyBottomAdapter: HeadlinePlayShuffleAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mAlbumItemAdapter: AlbumItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: LinearLayoutCompat? = null

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            initViews(view)
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
        val spanCount = 1
        var touchHelper : ItemTouchHelper? = null

        //Setup headline adapter
        val listHeadlines : ArrayList<Long> = ArrayList<Long>()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, R.layout.item_top_play_shuffle, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
            }
            override fun onShuffleButtonClicked() {
                Toast.makeText(mContext, "onShuffleButtonClicked", Toast.LENGTH_SHORT).show()
            }

            override fun onFilterButtonClicked() {
                Toast.makeText(mContext, "onFilterButtonClicked", Toast.LENGTH_SHORT).show()
            }
        })
        //Setup song adapter
        mAlbumItemAdapter = AlbumItemAdapter(mAlbumList, mContext!!, object : AlbumItemAdapter.OnItemClickListener{
            override fun onAlbumItemClicked(position: Int) {
            }

            override fun onAlbumItemLongClicked(position: Int) {
            }

        },
            object : SelectableRecycleViewAdapter.OnSelectSelectableItemListener{
                override fun onSelectionModeChange(
                    selectMode: Boolean,
                    totalSelected: Int,
                    totalCount: Int
                ) {
                }

                override fun onTotalSelectedItemChange(totalSelected: Int, totalCount: Int) {
                }

        })
        //Setup empty bottom adapter
        mEmptyBottomAdapter = HeadlinePlayShuffleAdapter(
            listHeadlines,
            R.layout.item_custom_empty_bottom_space,
            object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                    Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
                }
                override fun onShuffleButtonClicked() {
                    Toast.makeText(mContext, "onShuffleButtonClicked", Toast.LENGTH_SHORT).show()
                }

                override fun onFilterButtonClicked() {
                    Toast.makeText(mContext, "onFilterButtonClicked", Toast.LENGTH_SHORT).show()
                }
            }
        )
        //Setup concat adapter
        val concatAdapter = ConcatAdapter()
//        concatAdapter.addAdapter(mHeadlineTopPlayShuffleAdapter!!)
        concatAdapter.addAdapter(mAlbumItemAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)
        mRecyclerView?.adapter = concatAdapter

        //Add Layout manager
        val layoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
        mLoadingContentProgress = view.findViewById<LinearLayoutCompat>(R.id.loading_content_progress)

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