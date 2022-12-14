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
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortContentExplorerBottomSheetDialogFragment
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumsFragment : Fragment() {
    private var mDataBidingView: FragmentAlbumsBinding? = null

    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mAlbumItemViewModel: AlbumItemViewModel by activityViewModels()

    private var mOrganizeDialog: OrganizeItemBottomSheetDialogFragment = OrganizeItemBottomSheetDialogFragment.newInstance()
    private val mSortContentDialog: SortContentExplorerBottomSheetDialogFragment = SortContentExplorerBottomSheetDialogFragment.newInstance()

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
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_albums,container,false)
        val view = mDataBidingView?.root

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
        mMainFragmentViewModel.getReQuestToggleSelectAll().observe(viewLifecycleOwner){
            onReQuestToggleSelectAll(it)
        }
        mMainFragmentViewModel.getReQuestToggleSelectRange().observe(viewLifecycleOwner){
            onReQuestToggleSelectRange(it)
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
        val tempNewIsInverted : Boolean = isInverted ?: false
        if(tempNewIsInverted){
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
    private fun onReQuestToggleSelectRange(requestCount : Int?) {
        if(requestCount == null || requestCount <= 0) return
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == AlbumsFragment.TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }
    private fun onReQuestToggleSelectAll(requestCount: Int?) {
        if(requestCount == null || requestCount <= 0) return
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == AlbumsFragment.TAG){
            val totalItemCount = mMainFragmentViewModel.getTotalCount().value ?: 0
            val selectedItemCount = mGenericListGridItemAdapter?.selectableGetSelectedItemCount() ?: 0
            if(totalItemCount < selectedItemCount){
                mGenericListGridItemAdapter?.selectableSelectAll(mLayoutManager)
            }else{
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
        mDataBidingView?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    return AlbumItem.getStringIndexForFastScroller(dataItem)
                }
            },
            object : GenericListGridItemAdapter.OnItemClickListener{
                override fun onItemClicked(position: Int) {
                    if(mMainFragmentViewModel.getSelectMode().value == true){
                        mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                    }else{
                        openExploreContentFragment(position)
                    }
                }
                override fun onItemLongPressed(position: Int) {
                    mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                }
            },
            object : SelectableItemListAdapter.OnSelectSelectableItemListener{
                override fun onSelectModeChange(selectMode: Boolean) {
                    mMainFragmentViewModel.setSelectMode(selectMode)
                }
                override fun onRequestGetStringIndex(position: Int): String {
                    return AlbumItem.getStringIndexForSelection(
                        mGenericListGridItemAdapter?.currentList?.get(position)
                    )
                }
                override fun onSelectedListChange(selectedList: HashMap<Int, String>) {
                    mMainFragmentViewModel.setSelectedDataList(selectedList)
                }
            },
            AlbumItem.diffCallback as DiffUtil.ItemCallback<Any>,
            mAlbumsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            mIsSelectable = true,
            mHavePlaybackState = false,
            mIsImageFullCircle = false
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
        mLayoutManager = GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, false)
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
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.recyclerView.adapter = mConcatAdapter
            dataBidingView.recyclerView.layoutManager = mLayoutManager
            val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAlbumsFragmentViewModel.getOrganizeListGrid().value)
            val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
            mItemDecoration = GridSpacingItemDecoration(updatedSpan)
            mItemDecoration?.let {
                dataBidingView.recyclerView.addItemDecoration(it)
            }

            dataBidingView.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
            dataBidingView.fastScroller.attachRecyclerView(dataBidingView.recyclerView)
            dataBidingView.fastScroller.setFastScrollListener(object :
                FastScroller.FastScrollListener {
                override fun onFastScrollStart(fastScroller: FastScroller) {
                    mMainFragmentViewModel.setIsFastScrolling(true)
                    println("FAST SCROLLING STARTED")
                }

                override fun onFastScrollStop(fastScroller: FastScroller) {
                    mMainFragmentViewModel.setIsFastScrolling(false)
                    println("FAST SCROLLING STOPPED")
                    if (mDataBidingView?.recyclerView?.canScrollVertically(-1) == false) {
                        //On scrolled to top
                        mMainFragmentViewModel.setScrollingState(-2)
                    }else if(mDataBidingView?.recyclerView?.canScrollVertically(1) == false){
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

    private fun showSortDialog() {
        if(mSortContentDialog.isVisible) return

        mSortContentDialog.updateBottomSheetData(
            mAlbumsFragmentViewModel,
            TAG,
            null
        )
        mSortContentDialog.show(childFragmentManager, SortContentExplorerBottomSheetDialogFragment.TAG)
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
        mDataBidingView?.recyclerView?.setHasFixedSize(true)
        mDataBidingView?.constraintFastScrollerContainer?.let {
            InsetModifiersUtils.updateBottomViewInsets(
                it
            )
        }
    }

    companion object {
        const val TAG = "AlbumsFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_GRID_LARGE
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