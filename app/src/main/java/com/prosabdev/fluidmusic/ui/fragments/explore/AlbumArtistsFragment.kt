package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.AlbumArtistItemListAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAlbumArtistsBinding
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.SortOrganizePrefsLoaderAndSetupViewModels
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AlbumArtistsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumArtistsFragment : Fragment() {


    private lateinit var mFragmentAlbumArtistsBinding: FragmentAlbumArtistsBinding

    private val mAlbumArtistsFragmentViewModel: AlbumArtistsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mAlbumArtistItemListAdapter: AlbumArtistItemListAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mIsDraggingToScroll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
            loadPrefsAndInitViewModel()
            checkInteractions()
            observeLiveData()
        }
    }

    private suspend fun loadPrefsAndInitViewModel() {
        context?.let { ctx ->
            withContext(Dispatchers.Default) {
                SortOrganizePrefsLoaderAndSetupViewModels.loadSortOrganizeItemsSettings(
                    ctx,
                    mAlbumArtistsFragmentViewModel,
                    SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_ALBUM_ARTISTS
                )
            }
        }
    }

    private fun checkInteractions() {
        mFragmentAlbumArtistsBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(mIsDraggingToScroll){
                    if(dy < 0){
                        Log.i(ConstantValues.TAG, "Scrolling --> TOP")
                        mMainFragmentViewModel.setScrollingState(-1)
                    }else if(dy > 0){
                        Log.i(ConstantValues.TAG, "Scrolling --> BOTTOM")
                        mMainFragmentViewModel.setScrollingState(1)
                    }
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(ConstantValues.TAG, "Scrolled to BOTTOM")
                        mMainFragmentViewModel.setScrollingState(2)
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(ConstantValues.TAG, "Scrolled to TOP")
                        mMainFragmentViewModel.setScrollingState(-2)
                    }
                }else{
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(ConstantValues.TAG, "Scrolled to BOTTOM")
                        if(mMainFragmentViewModel.getScrollingState().value != 2)
                            mMainFragmentViewModel.setScrollingState(2)
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(ConstantValues.TAG, "Scrolled to TOP")
                        if(mMainFragmentViewModel.getScrollingState().value != -2)
                            mMainFragmentViewModel.setScrollingState(-2)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        mIsDraggingToScroll = false
                        println("The RecyclerView is SCROLL_STATE_IDLE")
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        mIsDraggingToScroll = true
                        println("The RecyclerView is SCROLL_STATE_DRAGGING")
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        println("The RecyclerView is SCROLL_STATE_SETTLING")
                    }
                }
            }
        })
    }

    private fun observeLiveData() {
        mAlbumArtistsFragmentViewModel.getAll().observe(viewLifecycleOwner){
            addDataToAdapter(it)
        }
    }
    private fun addDataToAdapter(albumList: List<Any>?) {
        mAlbumArtistItemListAdapter?.submitList(albumList)
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == ConstantValues.EXPLORE_ALBUMS){
            mMainFragmentViewModel.setTotalCount(albumList?.size ?: 0)
        }
    }

    private suspend fun setupRecyclerViewAdapter() {
        withContext(Dispatchers.Default){
            val ctx : Context = context ?: return@withContext
            val spanCount = 2
            //Setup headline adapter
            val listHeadlines : ArrayList<Int> = ArrayList<Int>()
            listHeadlines.add(0)
            mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                    //
                }
                override fun onShuffleButtonClicked() {
                    //
                }
                override fun onSortButtonClicked() {
                    //
                }
                override fun onOrganizeButtonClicked() {
                    //
                }
            })

            //Setup content adapter
            mAlbumArtistItemListAdapter = AlbumArtistItemListAdapter(
                ctx,
                object : AlbumArtistItemListAdapter.OnItemClickListener{
                    override fun onItemClicked(position: Int) {
                        //
                    }

                    override fun onItemLongClicked(position: Int) {
                        //
                    }
                },
                object : SelectableItemListAdapter.OnSelectSelectableItemListener{
                    override fun onSelectModeChange(selectMode: Boolean) {
                        //
                    }

                    override fun onTotalSelectedItemChange(totalSelected: Int) {
                        //
                    }
                }
            )

            //Setup empty bottom space adapter
            val listEmptyBottomSpace : ArrayList<String> = ArrayList()
            listEmptyBottomSpace.add("")
            mEmptyBottomAdapter = EmptyBottomAdapter(listEmptyBottomSpace)

            //Setup concat adapter
            val concatAdapter = ConcatAdapter()
            mHeadlineTopPlayShuffleAdapter?.let {
                concatAdapter.addAdapter(it)
            }
            mAlbumArtistItemListAdapter?.let {
                concatAdapter.addAdapter(it)
            }
            mEmptyBottomAdapter?.let {
                concatAdapter.addAdapter(it)
            }

            //Add Layout manager
            mLayoutManager = GridLayoutManager(ctx, spanCount, GridLayoutManager.VERTICAL, false)
            mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0 -> spanCount
                        ((mLayoutManager?.itemCount ?: 0) - 1) -> spanCount
                        else -> 1
                    }
                }
            }
            MainScope().launch {
                mFragmentAlbumArtistsBinding.recyclerView.adapter = concatAdapter
                mFragmentAlbumArtistsBinding.recyclerView.layoutManager = mLayoutManager
                mFragmentAlbumArtistsBinding.recyclerView.addItemDecoration(
                    GridSpacingItemDecoration(spanCount)
                )

                mFragmentAlbumArtistsBinding.fastScroller.setSectionIndexer(mAlbumArtistItemListAdapter)
                mFragmentAlbumArtistsBinding.fastScroller.attachRecyclerView(mFragmentAlbumArtistsBinding.recyclerView)
                mFragmentAlbumArtistsBinding.fastScroller.setFastScrollListener(object :
                    FastScroller.FastScrollListener {
                    override fun onFastScrollStart(fastScroller: FastScroller) {
                        mMainFragmentViewModel.setIsFastScrolling(true)
                        println("FAST SCROLLING STARTED")
                    }

                    override fun onFastScrollStop(fastScroller: FastScroller) {
                        mMainFragmentViewModel.setIsFastScrolling(false)
                        println("FAST SCROLLING STOPPED")
                    }

                })
            }
        }
    }

    private fun initViews() {
        //
    }

    companion object {
        const val TAG = "AlbumArtistsFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_GRID_MEDIUM

        @JvmStatic
        fun newInstance(pageIndex: Int) =
            AlbumArtistsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}