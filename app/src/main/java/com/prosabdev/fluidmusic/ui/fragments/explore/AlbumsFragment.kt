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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAlbumsBinding
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortContentExplorerIBottomSheetDialogFragment
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumsFragment : Fragment() {
    private var mFragmentAlbumsBinding: FragmentAlbumsBinding? = null

    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mAlbumItemViewModel: AlbumItemViewModel by activityViewModels()

    private var mOrganizeDialog: OrganizeItemBottomSheetDialogFragment = OrganizeItemBottomSheetDialogFragment.newInstance()
    private val mSortContentDialog: SortContentExplorerIBottomSheetDialogFragment = SortContentExplorerIBottomSheetDialogFragment.newInstance()

    private var mConcatAdapter: ConcatAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mGenericListGridItemAdapter: GenericListGridItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var mItemDecoration: GridSpacingItemDecoration? = null

    private var mIsDraggingToScroll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
        arguments?.let {
        }

        loadPrefsAndInitViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentAlbumsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_albums,container,false)
        val view = mFragmentAlbumsBinding?.root

        initViews()
        setupRecyclerViewAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    override fun onDestroyView() {
        saveAllDataToPref()
        super.onDestroyView()
    }
    private fun saveAllDataToPref(){
        context?.let { ctx ->
            val tempSortOrganize = SortOrganizeItemSP()
            tempSortOrganize.sortOrderBy = mAlbumsFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.organizeListGrid = mAlbumsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.isInvertSort = mAlbumsFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            SharedPreferenceManagerUtils
                .SortAnOrganizeForExploreContents
                .saveSortOrganizeItemsFor(
                    ctx,
                    SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_ALBUMS,
                    tempSortOrganize
                )
        }
    }
    private fun loadPrefsAndInitViewModel() {
        context?.let { ctx ->
            val tempSortOrganize: SortOrganizeItemSP? =
                SharedPreferenceManagerUtils
                    .SortAnOrganizeForExploreContents
                    .loadSortOrganizeItemsFor(
                        ctx,
                        SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_ALBUMS
                    )
            tempSortOrganize?.let { sortOrganize ->
                mAlbumsFragmentViewModel.setSortBy(sortOrganize.sortOrderBy)
                mAlbumsFragmentViewModel.setOrganizeListGrid(sortOrganize.organizeListGrid)
                mAlbumsFragmentViewModel.setIsInverted(sortOrganize.isInvertSort)
            }
            if(tempSortOrganize == null){
                mAlbumsFragmentViewModel.setSortBy(SORT_LIST_GRID_DEFAULT_VALUE)
                mAlbumsFragmentViewModel.setOrganizeListGrid(ORGANIZE_LIST_GRID_DEFAULT_VALUE)
                mAlbumsFragmentViewModel.setIsInverted(IS_INVERTED_LIST_GRID_DEFAULT_VALUE)
            }
        }
    }

    private fun observeLiveData() {
        mAlbumsFragmentViewModel.getAll().observe(viewLifecycleOwner){
            addDataToGenericAdapter(it)
        }
        mAlbumsFragmentViewModel.getSortBy().observe(viewLifecycleOwner){
            requestNewDataFromDatabase()
        }
        mAlbumsFragmentViewModel.getIsInverted().observe(viewLifecycleOwner){
            invertAlbumListAndUpdateAdapter(it)
        }
        mAlbumsFragmentViewModel.getOrganizeListGrid().observe(viewLifecycleOwner){
            updateOrganizeListGrid(it)
        }

        //Listen for main fragment changes
        mMainFragmentViewModel.getSelectMode().observe(viewLifecycleOwner) {
            onSelectionModeChanged(it)
        }
        mMainFragmentViewModel.getTotalSelected().observe(viewLifecycleOwner){
            onTotalSelectedItemsChanged(it)
        }
        mMainFragmentViewModel.getToggleRange().observe(viewLifecycleOwner){
            onToggleRangeChanged()
        }
        mMainFragmentViewModel.getScrollingState().observe(viewLifecycleOwner){
            updateOnScrollingStateUI(it)
        }
    }
    private fun updateOrganizeListGrid(organizeValue: Int?) {
        context?.let { ctx ->
            val tempSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, organizeValue)
            mGenericListGridItemAdapter?.setOrganizeListGrid(mAlbumsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE)
            mLayoutManager?.spanCount = tempSpanCount
        }
    }
    private fun invertAlbumListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewReverseLayout : Boolean = isInverted ?: false
        if(tempNewReverseLayout){
            mGenericListGridItemAdapter?.submitList(mAlbumsFragmentViewModel.getAll().value?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(mAlbumsFragmentViewModel.getAll().value)
        }
        if(
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mAlbumsFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mAlbumsFragmentViewModel.getIsInverted().value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        }else{
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }
    private fun requestNewDataFromDatabase() {
        MainScope().launch {
            mAlbumsFragmentViewModel.requestDataDirectlyFromDatabase(
                mAlbumItemViewModel
            )
        }
    }
    private fun addDataToGenericAdapter(dataList: List<Any>?) {
        if(mAlbumsFragmentViewModel.getIsInverted().value == true){
            mGenericListGridItemAdapter?.submitList(dataList?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(dataList)
        }
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG){
            mMainFragmentViewModel.setTotalCount(dataList?.size ?: 0)
        }
        if(
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mAlbumsFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mAlbumsFragmentViewModel.getIsInverted().value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        }else{
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun updateOnScrollingStateUI(i: Int) {
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG){
            if(i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }
    private fun onToggleRangeChanged() {
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }
    private fun onTotalSelectedItemsChanged(it: Int?) {
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG){
            if((it ?: 0) > 0 && (it ?: 0) >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
                mGenericListGridItemAdapter?.selectableSelectAll(mLayoutManager)
            }else if((it ?: 0) <= 0 && (mGenericListGridItemAdapter?.selectableGetSelectedItemCount() ?: 0) > 0){
                mGenericListGridItemAdapter?.selectableClearSelection(mLayoutManager)
            }
        }
    }
    private fun onSelectionModeChanged(it: Boolean?) {
        Log.i(ConstantValues.TAG, "mMainFragmentViewModel ${mMainFragmentViewModel.getCurrentSelectablePage().value}")
        Log.i(ConstantValues.TAG, "mMainFragmentViewModel List size ${mMainFragmentViewModel.getTotalCount().value}")
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mMainFragmentViewModel.setTotalCount(mGenericListGridItemAdapter?.itemCount ?: 0)
            mLayoutManager?.let { it1 ->
                mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
            }
            mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
        }
    }

    private fun checkInteractions() {
        mFragmentAlbumsBinding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(mIsDraggingToScroll){
                    if(dy < 0){
                        Log.i(TAG, "Scrolling --> TOP")
                        mMainFragmentViewModel.setScrollingState(-1)
                    }else if(dy > 0){
                        Log.i(TAG, "Scrolling --> BOTTOM")
                        mMainFragmentViewModel.setScrollingState(1)
                    }
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(TAG, "Scrolled to BOTTOM")
                        mMainFragmentViewModel.setScrollingState(2)
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(TAG, "Scrolled to TOP")
                        mMainFragmentViewModel.setScrollingState(-2)
                    }
                }else{
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(TAG, "Scrolled to BOTTOM")
                        if(mMainFragmentViewModel.getScrollingState().value != 2)
                            mMainFragmentViewModel.setScrollingState(2)
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(TAG, "Scrolled to TOP")
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

    private fun setupRecyclerViewAdapter() {
        val ctx : Context = context ?: return
        //Setup headline adapter
        val listHeadlines : ArrayList<Int> = ArrayList()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                playAllAlbumsFromFirstPosition()
            }
            override fun onShuffleButtonClicked() {
                playAllAlbumsWithShuffle()
            }
            override fun onSortButtonClicked() {
                showSortDialog()
            }
            override fun onOrganizeButtonClicked() {
                showOrganizeDialog()
            }
        })

        //Setup generic item adapter
        mGenericListGridItemAdapter = GenericListGridItemAdapter(
            ctx,
            object : GenericListGridItemAdapter.OnItemRequestDataInfo{
                override fun onRequestDataInfo(dataItem: Any, position: Int): GenericItemListGrid? {
                    return AlbumItem.castDataItemToGeneric(ctx, dataItem)
                }
                override fun onRequestTextIndexForFastScroller(
                    dataItem: Any,
                    position: Int
                ): String {
                    return AlbumItem.getStringIndexRequestFastScroller(ctx, dataItem)
                }
            },
            object : GenericListGridItemAdapter.OnItemClickListener{
                override fun onItemClicked(position: Int) {
                    openExploreContentFragment(position)
                }
                override fun onItemLongPressed(position: Int) {
                    checkMultipleSelection(position)
                }
            },
            object : SelectableItemListAdapter.OnSelectSelectableItemListener{
                override fun onSelectModeChange(selectMode: Boolean) {
                    mMainFragmentViewModel.setSelectMode(selectMode)
                }
                override fun onTotalSelectedItemChange(totalSelected: Int) {
                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }
            },
            AlbumItem.diffCallback as DiffUtil.ItemCallback<Any>,
            mAlbumsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            mIsSelectable = true,
            mHavePlaybackState = false
        )

        //Setup empty bottom space adapter
        val listEmptyBottomSpace : ArrayList<String> = ArrayList()
        listEmptyBottomSpace.add("")
        mEmptyBottomAdapter = EmptyBottomAdapter(listEmptyBottomSpace)

        //Setup concat adapter
        mConcatAdapter = ConcatAdapter()
        mHeadlineTopPlayShuffleAdapter?.let {
            mConcatAdapter?.addAdapter(it)
        }
        mGenericListGridItemAdapter?.let {
            mConcatAdapter?.addAdapter(it)
        }
        mEmptyBottomAdapter?.let {
            mConcatAdapter?.addAdapter(it)
        }

        //Add Layout manager
        val initialSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAlbumsFragmentViewModel.getOrganizeListGrid().value)
        val initialReverseLayout : Boolean = mAlbumsFragmentViewModel.getIsInverted().value ?: false
        mLayoutManager = GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, initialReverseLayout)
        mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAlbumsFragmentViewModel.getOrganizeListGrid().value)
                val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
                return when (position) {
                    0 -> updatedSpan
                    ((mLayoutManager?.itemCount ?: 0) - 1) -> updatedSpan
                    else -> 1
                }
            }
        }
        mFragmentAlbumsBinding?.let { fragmentAlbumsBinding ->
            fragmentAlbumsBinding.recyclerView.adapter = mConcatAdapter
            fragmentAlbumsBinding.recyclerView.layoutManager = mLayoutManager
            val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAlbumsFragmentViewModel.getOrganizeListGrid().value)
            val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
            mItemDecoration = GridSpacingItemDecoration(updatedSpan)
            mItemDecoration?.let {
                fragmentAlbumsBinding.recyclerView.addItemDecoration(it)
            }

            fragmentAlbumsBinding.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
            fragmentAlbumsBinding.fastScroller.attachRecyclerView(fragmentAlbumsBinding.recyclerView)
            fragmentAlbumsBinding.fastScroller.setFastScrollListener(object :
                FastScroller.FastScrollListener {
                override fun onFastScrollStart(fastScroller: FastScroller) {
                    mMainFragmentViewModel.setIsFastScrolling(true)
                    println("FAST SCROLLING STARTED")
                }

                override fun onFastScrollStop(fastScroller: FastScroller) {
                    mMainFragmentViewModel.setIsFastScrolling(false)
                    println("FAST SCROLLING STOPPED")
                    if (mFragmentAlbumsBinding?.recyclerView?.canScrollVertically(-1) == false) {
                        //On scrolled to top
                        mMainFragmentViewModel.setScrollingState(-2)
                    }else if(mFragmentAlbumsBinding?.recyclerView?.canScrollVertically(1) == false){
                        //On scrolled to bottom
                        mMainFragmentViewModel.setScrollingState(2)
                    }
                }

            })
        }
    }
    private fun openExploreContentFragment(position: Int) {
        //
    }
    private fun playAllAlbumsFromFirstPosition() {
        //
    }
    private fun playAllAlbumsWithShuffle() {
        //
    }

    private fun checkMultipleSelection(position: Int) {
        mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
    }
    private fun showSortDialog() {
        if(mSortContentDialog.isVisible) return

        mSortContentDialog.updateBottomSheetData(
            mAlbumsFragmentViewModel,
            TAG,
            null
        )
        mSortContentDialog.show(childFragmentManager, SortContentExplorerIBottomSheetDialogFragment.TAG)
    }

    private fun showOrganizeDialog() {
        if(mOrganizeDialog.isVisible) return

        mOrganizeDialog.updateBottomSheetData(
            mAlbumsFragmentViewModel,
            TAG,
            null
        )
        mOrganizeDialog.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }

    private fun initViews() {
        mFragmentAlbumsBinding?.recyclerView?.setHasFixedSize(true)
    }

    companion object {
        const val TAG = "AlbumsFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_GRID_MEDIUM
        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = "name"
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance() =
            AlbumsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}