package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.l4digital.fastscroll.FastScroller
import com.l4digital.fastscroll.FastScroller.FastScrollListener
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAllSongsBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortSongsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.custom.CenterSmoothScroller
import com.prosabdev.fluidmusic.ui.fragments.commonmethods.FragmentCommonMediaPlaybackAction
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AllSongsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mFragmentAllSongsBinding: FragmentAllSongsBinding? = null

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mSongItemViewModel: SongItemViewModel by activityViewModels()

    private var mOrganizeDialog: OrganizeItemBottomSheetDialogFragment = OrganizeItemBottomSheetDialogFragment.newInstance()
    private val mSortSongDialog: SortSongsBottomSheetDialogFragment = SortSongsBottomSheetDialogFragment.newInstance()

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
        mFragmentAllSongsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_songs,container,false)
        val view = mFragmentAllSongsBinding?.root

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
            val tempSortOrganize: SortOrganizeItemSP = SortOrganizeItemSP()
            tempSortOrganize.sortOrderBy = mAllSongsFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.organizeListGrid = mAllSongsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.isInvertSort = mAllSongsFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            SharedPreferenceManagerUtils
                .SortAnOrganizeForExploreContents
                .saveSortOrganizeItemsFor(
                    ctx,
                    SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_ALL_SONGS,
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
                        SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_ALL_SONGS
                    )
            tempSortOrganize?.let { sortOrganize ->
                mAllSongsFragmentViewModel.setSortBy(sortOrganize.sortOrderBy)
                mAllSongsFragmentViewModel.setOrganizeListGrid(sortOrganize.organizeListGrid)
                mAllSongsFragmentViewModel.setIsInverted(sortOrganize.isInvertSort)
            }
            if(tempSortOrganize == null){
                mAllSongsFragmentViewModel.setSortBy(SORT_LIST_GRID_DEFAULT_VALUE)
                mAllSongsFragmentViewModel.setOrganizeListGrid(ORGANIZE_LIST_GRID_DEFAULT_VALUE)
                mAllSongsFragmentViewModel.setIsInverted(IS_INVERTED_LIST_GRID_DEFAULT_VALUE)
            }
        }
    }

    private fun observeLiveData() {
        mAllSongsFragmentViewModel.getAll().observe(viewLifecycleOwner){
            addDataToGenericAdapter(it)
        }
        mAllSongsFragmentViewModel.getSortBy().observe(viewLifecycleOwner){
            requestNewDataFromDatabase()
        }
        mAllSongsFragmentViewModel.getIsInverted().observe(viewLifecycleOwner){
            invertSongListAndUpdateAdapter(it)
        }
        mAllSongsFragmentViewModel.getOrganizeListGrid().observe(viewLifecycleOwner){
            updateOrganizeListGrid(it)
        }

        //Listen to player changes
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner) {
            updatePlayingSongUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            updatePlaybackStateUI(it)
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
            mGenericListGridItemAdapter?.setOrganizeListGrid(mAllSongsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE)
            mLayoutManager?.spanCount = tempSpanCount
        }
    }
    private fun invertSongListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewReverseLayout : Boolean = isInverted ?: false
        if(tempNewReverseLayout){
            mGenericListGridItemAdapter?.submitList(mAllSongsFragmentViewModel.getAll().value?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(mAllSongsFragmentViewModel.getAll().value)
        }
        mGenericListGridItemAdapter?.setPlayingPosition(-1)
        mGenericListGridItemAdapter?.setIsPlaying(false)
    }
    private fun requestNewDataFromDatabase() {
        MainScope().launch {
            mAllSongsFragmentViewModel.requestDataDirectlyFromDatabase(
                mSongItemViewModel
            )
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }
    private fun addDataToGenericAdapter(dataList: List<Any>?) {
        if(mAllSongsFragmentViewModel.getIsInverted().value == true){
            mGenericListGridItemAdapter?.submitList(dataList?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(dataList)
        }
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == ConstantValues.EXPLORE_ALL_SONGS){
            mMainFragmentViewModel.setTotalCount(dataList?.size ?: 0)
        }
    }

    private fun updateOnScrollingStateUI(i: Int) {
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == ConstantValues.EXPLORE_ALL_SONGS){
            if(i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }
    private fun onToggleRangeChanged() {
        mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
    }
    private fun onTotalSelectedItemsChanged(it: Int?) {
        if((it ?: 0) > 0 && (it ?: 0) >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            mGenericListGridItemAdapter?.selectableSelectAll(mLayoutManager)
        }else if((it ?: 0) <= 0 && (mGenericListGridItemAdapter?.selectableGetSelectedItemCount() ?: 0) > 0){
            mGenericListGridItemAdapter?.selectableClearSelection(mLayoutManager)
        }
    }
    private fun onSelectionModeChanged(it: Boolean?) {
        mLayoutManager?.let { it1 ->
            mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
        }
        mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
    }
    private fun updatePlayingSongUI(songItem: SongItem?) {
        val songPosition: Int = songItem?.position ?: -1

        if(
            mPlayerFragmentViewModel.getQueueListSource().value == ConstantValues.EXPLORE_ALL_SONGS &&
            mPlayerFragmentViewModel.getSortBy().value == mAllSongsFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mAllSongsFragmentViewModel.getIsInverted().value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(songPosition)
            mGenericListGridItemAdapter?.setIsPlaying(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
            tryToScrollOnCurrentItem(songPosition)
        }else{
            if((mGenericListGridItemAdapter?.getPlayingPosition() ?: -1) >= 0)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
        }
    }
    private fun tryToScrollOnCurrentItem(position: Int) {
        if(position >= 0) {
            val tempCanScrollToPlayingSong: Boolean = mPlayerFragmentViewModel.getCanScrollCurrentPlayingSong().value ?: false
            if(!tempCanScrollToPlayingSong) return
            mPlayerFragmentViewModel.setCanScrollCurrentPlayingSong(false)
            val tempFV: Int = (mLayoutManager?.findFirstVisibleItemPosition() ?: 0) -1
            val tempLV: Int = mLayoutManager?.findLastVisibleItemPosition() ?: +1
            val tempVisibility: Boolean = position in tempFV .. tempLV
            if(!tempVisibility) return
            context?.let { ctx ->
                val tempListSize: Int = mGenericListGridItemAdapter?.currentList?.size ?: 0
                val tempTargetPosition = if(position + 2 <= tempListSize) position + 2 else tempListSize
                MainScope().launch {
                    mLayoutManager?.let {
                        it.startSmoothScroll(
                            CenterSmoothScroller(ctx).apply {
                                targetPosition = tempTargetPosition
                            }
                        )
                    }
                }
            }
        }
    }

    private fun updatePlaybackStateUI(isPlaying: Boolean) {
        if(
            mPlayerFragmentViewModel.getQueueListSource().value == ConstantValues.EXPLORE_ALL_SONGS &&
            mPlayerFragmentViewModel.getSortBy().value == mAllSongsFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mAllSongsFragmentViewModel.getIsInverted().value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(isPlaying)
            tryToScrollOnCurrentItem(mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
        }else{
            if(mGenericListGridItemAdapter?.getIsPlaying() == true)
                mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun checkInteractions() {
        mFragmentAllSongsBinding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun setupRecyclerViewAdapter() {
        val ctx : Context = context ?: return
        //Setup headline adapter
        val listHeadlines : ArrayList<Int> = ArrayList<Int>()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {

                FragmentCommonMediaPlaybackAction.playSongAtPosition(
                    mPlayerFragmentViewModel,
                    mAllSongsFragmentViewModel,
                    mGenericListGridItemAdapter,
                    0
                )
            }
            override fun onShuffleButtonClicked() {
                playSongOnShuffle()
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
                    return SongItem.castDataItemToGeneric(ctx, dataItem)
                }
                override fun onRequestTextIndexForFastScroller(
                    dataItem: Any,
                    position: Int
                ): String {
                    return SongItem.getStringIndexRequestFastScroller(ctx, dataItem)
                }
            },
            object : GenericListGridItemAdapter.OnItemClickListener{
                override fun onItemClicked(position: Int) {
                    if(mGenericListGridItemAdapter?.selectableGetSelectionMode() == true){
                        mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                    }else{
                        FragmentCommonMediaPlaybackAction.playSongAtPosition(
                            mPlayerFragmentViewModel,
                            mAllSongsFragmentViewModel,
                            mGenericListGridItemAdapter,
                            position
                        )
                    }
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
            SongItem.diffCallback as DiffUtil.ItemCallback<Any>,
            mAllSongsFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            mIsSelectable = true,
            mHavePlaybackState = true
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
        val initialSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAllSongsFragmentViewModel.getOrganizeListGrid().value)
        val initialReverseLayout : Boolean = mAllSongsFragmentViewModel.getIsInverted().value ?: false
        mLayoutManager = GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, initialReverseLayout)
        mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAllSongsFragmentViewModel.getOrganizeListGrid().value)
                val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
                return when (position) {
                    0 -> updatedSpan
                    ((mLayoutManager?.itemCount ?: 0) - 1) -> updatedSpan
                    else -> 1
                }
            }
        }
        mFragmentAllSongsBinding?.let { fragmentAllSongsBinding ->
            fragmentAllSongsBinding.recyclerView.adapter = mConcatAdapter
            fragmentAllSongsBinding.recyclerView.layoutManager = mLayoutManager
            val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mAllSongsFragmentViewModel.getOrganizeListGrid().value)
            val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
            mItemDecoration = GridSpacingItemDecoration(updatedSpan)
            mItemDecoration?.let {
                fragmentAllSongsBinding.recyclerView.addItemDecoration(it)
            }

            fragmentAllSongsBinding.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
            fragmentAllSongsBinding.fastScroller.attachRecyclerView(fragmentAllSongsBinding.recyclerView)
            fragmentAllSongsBinding.fastScroller.setFastScrollListener(object : FastScrollListener{
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

    private fun checkMultipleSelection(position: Int) {
        mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
    }
    private fun showSortDialog() {
        if(mSortSongDialog.isVisible) return

        mSortSongDialog.updateBottomSheetData(
            mAllSongsFragmentViewModel,
            ConstantValues.EXPLORE_ALBUMS,
            null
        )
        mSortSongDialog.show(childFragmentManager, SortSongsBottomSheetDialogFragment.TAG)
    }

    private fun showOrganizeDialog() {
        if(mOrganizeDialog.isVisible) return

        mOrganizeDialog.updateBottomSheetData(
            mAllSongsFragmentViewModel,
            ConstantValues.EXPLORE_ALBUMS,
            null
        )
        mOrganizeDialog.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }

    private fun playSongOnShuffle() {
        if((mGenericListGridItemAdapter?.currentList?.size ?: 0) <= 0) return
        MainScope().launch {
            withContext(Dispatchers.Default){
                val randomExcludedNumber: Int =
                    MathComputationsUtils.randomExcluded(
                        mGenericListGridItemAdapter?.getPlayingPosition() ?: -1,
                        (mGenericListGridItemAdapter?.currentList?.size ?: 0) -1
                    )
                mGenericListGridItemAdapter?.let { genericListGridItemAdapter ->
                    FragmentCommonMediaPlaybackAction.playSongAtPosition(
                        mPlayerFragmentViewModel,
                        mAllSongsFragmentViewModel,
                        genericListGridItemAdapter,
                        randomExcludedNumber,
                        false,
                        PlaybackStateCompat.REPEAT_MODE_NONE,
                        PlaybackStateCompat.SHUFFLE_MODE_NONE
                    )
                }
                updateRecyclerViewScrollingSate()
            }
        }
    }
    private fun updateRecyclerViewScrollingSate(){
        if(mFragmentAllSongsBinding?.recyclerView?.scrollState == RecyclerView.SCROLL_STATE_SETTLING){
            mIsDraggingToScroll = false
        }
        mMainFragmentViewModel.setScrollingState(-1)
    }


    private fun initViews() {
        mFragmentAllSongsBinding?.recyclerView?.setHasFixedSize(true)
//        mFragmentAllSongsBinding?.recyclerView?.setItemViewCacheSize(100)
    }

    companion object {
        const val TAG = "AllSongsFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_LIST_SMALL
        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = "title"
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance(pageIndex: Int) =
            AllSongsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALL_SONGS, pageIndex)
                }
            }
    }
}