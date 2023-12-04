package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
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
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.common.constants.MainConst
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentExploreContentsForBinding
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortSongsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.custom.CenterSmoothScroller
import com.prosabdev.fluidmusic.ui.custom.CustomShapeableImageViewImageViewRatio11
import com.prosabdev.fluidmusic.ui.fragments.actions.PlaybackActions
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ExploreContentsForFragment : Fragment() {

    private var mDataBiding: FragmentExploreContentsForBinding? = null

    private val mExploreContentsForFragmentViewModel: ExploreContentsForFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel by activityViewModels()

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

    private var mLoadSongFromSource: String? = null
    private var mWhereColumnIndex: String? = null
    private var mWhereColumnValue: String? = null

    private var mSharedPrefsKey: String? = null
    private var mImageUri: String? = null
    private var mHashedCoverArtSignature: Int = -1
    private var mTextTitle: String? = null
    private var mTextSubTitle: String? = null
    private var mTextDetails: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply transition animation
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater,R.layout.fragment_explore_contents_for, container,false)
        val view = mDataBiding?.root

        //Load your UI content
        loadPrefsAndInitViewModel()
        initViews()
        setupRecyclerViewAdapter()
        checkInteractions()
        observeLiveData()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        saveAllDataToPref()
    }

    private fun saveAllDataToPref(){
        context?.let { ctx ->
            val tempSortOrganize = com.prosabdev.common.persistence.models.SortOrganizeItemSP()
            tempSortOrganize.sortOrderBy = mExploreContentsForFragmentViewModel.sortBy.value ?: SORT_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.organizeListGrid = mExploreContentsForFragmentViewModel.organizeListGrid.value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.isInvertSort = mExploreContentsForFragmentViewModel.isInverted.value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            PersistentStorage
                .SortAnOrganizeForExploreContents
                .saveSortOrganizeItemsFor(
                    mSharedPrefsKey ?: return,
                    tempSortOrganize
                )
        }
    }
    private fun loadPrefsAndInitViewModel() {
        context?.let { ctx ->
            val tempSortOrganize: com.prosabdev.common.persistence.models.SortOrganizeItemSP? =
                PersistentStorage
                    .SortAnOrganizeForExploreContents
                    .loadSortOrganizeItemsFor(
                        mSharedPrefsKey ?: return
                    )
            if(tempSortOrganize != null){
                mExploreContentsForFragmentViewModel.sortBy.value = tempSortOrganize.sortOrderBy
                mExploreContentsForFragmentViewModel.organizeListGrid.value = tempSortOrganize.organizeListGrid
                mExploreContentsForFragmentViewModel.isInverted.value = tempSortOrganize.isInvertSort
            }else{
                mExploreContentsForFragmentViewModel.sortBy.value = SORT_LIST_GRID_DEFAULT_VALUE
                mExploreContentsForFragmentViewModel.organizeListGrid.value = ORGANIZE_LIST_GRID_DEFAULT_VALUE
                mExploreContentsForFragmentViewModel.isInverted.value = IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            }
        }
    }
    private fun observeLiveData() {
        mExploreContentsForFragmentViewModel.dataList.observe(viewLifecycleOwner){
            addDataToGenericAdapter(it)
        }
        mExploreContentsForFragmentViewModel.sortBy.observe(viewLifecycleOwner){
            requestNewDataFromDatabase()
        }
        mExploreContentsForFragmentViewModel.isInverted.observe(viewLifecycleOwner){
            invertSongListAndUpdateAdapter(it)
        }
        mExploreContentsForFragmentViewModel.organizeListGrid.observe(viewLifecycleOwner){
            updateOrganizeListGrid(it)
        }

        //Listen to player changes
        mNowPlayingFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner) {
            updatePlayingSongUI(it)
        }
        mNowPlayingFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            updatePlaybackStateUI(it)
        }

        //Listen for main fragment changes
        mMainFragmentViewModel.selectMode.observe(viewLifecycleOwner) {
            onSelectionModeChanged(it)
        }
        mMainFragmentViewModel.requestToggleSelectAll.observe(viewLifecycleOwner){
            onReQuestToggleSelectAll(it)
        }
        mMainFragmentViewModel.requestToggleSelectRange.observe(viewLifecycleOwner){
            onReQuestToggleSelectRange(it)
        }
        mMainFragmentViewModel.scrollingState.observe(viewLifecycleOwner){
            updateOnScrollingStateUI(it)
        }
        mMainFragmentViewModel.isFastScrolling.observe(viewLifecycleOwner){
            tryToUpdateFastScrollStateUI(it)
        }
    }
    private fun tryToUpdateFastScrollStateUI(isFastScrolling: Boolean = true) {
        if(isFastScrolling){
            mDataBiding?.appBarLayout?.setExpanded(false)
        }
    }
    private fun updateOrganizeListGrid(organizeValue: Int?) {
        context?.let { ctx ->
            val tempSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, organizeValue)
            mGenericListGridItemAdapter?.setOrganizeListGrid(mExploreContentsForFragmentViewModel.organizeListGrid.value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE)
            mLayoutManager?.spanCount = tempSpanCount
        }
    }
    private fun invertSongListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewIsInverted : Boolean = isInverted ?: false
        if(tempNewIsInverted){
            mGenericListGridItemAdapter?.submitList(mExploreContentsForFragmentViewModel.dataList.value?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(mExploreContentsForFragmentViewModel.dataList.value)
        }
        if(
            mNowPlayingFragmentViewModel.queueListSource.value == TAG &&
            mNowPlayingFragmentViewModel.queueListSourceColumnIndex.value == mWhereColumnIndex &&
            mNowPlayingFragmentViewModel.queueListSourceColumnValue.value == mWhereColumnValue &&
            mNowPlayingFragmentViewModel.sortBy.value == mExploreContentsForFragmentViewModel.sortBy.value &&
            mNowPlayingFragmentViewModel.isInverted.value == mExploreContentsForFragmentViewModel.isInverted.value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(mNowPlayingFragmentViewModel.getIsPlaying().value ?: false)
        }else{
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }
    private fun requestNewDataFromDatabase() {
        if(mExploreContentsForFragmentViewModel.sortBy.value?.isEmpty() == true) return

        mExploreContentsForFragmentViewModel.requestDataDirectlyWhereColumnEqualFromDatabase(
            mSongItemViewModel,
            mWhereColumnIndex ?: return,
            mWhereColumnValue,
        )
    }
    private fun addDataToGenericAdapter(dataList: List<Any>?) {
        if(mExploreContentsForFragmentViewModel.isInverted.value == true){
            mGenericListGridItemAdapter?.submitList(dataList?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(dataList)
        }
        if(mMainFragmentViewModel.currentSelectablePage.value == TAG){
            mMainFragmentViewModel.totalCount.value = dataList?.size ?: 0)
        }
        if(
            mNowPlayingFragmentViewModel.queueListSource.value == TAG &&
            mNowPlayingFragmentViewModel.queueListSourceColumnIndex.value == mWhereColumnIndex &&
            mNowPlayingFragmentViewModel.queueListSourceColumnValue.value == mWhereColumnValue &&
            mNowPlayingFragmentViewModel.sortBy.value == mExploreContentsForFragmentViewModel.sortBy.value &&
            mNowPlayingFragmentViewModel.isInverted.value == mExploreContentsForFragmentViewModel.isInverted.value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(mNowPlayingFragmentViewModel.getIsPlaying().value ?: false)
        }else{
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun updateOnScrollingStateUI(i: Int) {
        if(mMainFragmentViewModel.currentSelectablePage.value == TAG){
            if(i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }
    private fun onReQuestToggleSelectRange(requestCount : Int?) {
        if(requestCount == null || requestCount <= 0) return
        if(mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }
    private fun onReQuestToggleSelectAll(requestCount: Int?) {
        if(requestCount == null || requestCount <= 0) return
        if(mMainFragmentViewModel.currentSelectablePage.value == TAG){
            val totalItemCount = mGenericListGridItemAdapter?.itemCount ?: 0
            val selectedItemCount = mGenericListGridItemAdapter?.selectableGetSelectedItemCount() ?: 0
            if(totalItemCount > selectedItemCount){
                mGenericListGridItemAdapter?.selectableSelectAll(mLayoutManager)
            }else{
                mGenericListGridItemAdapter?.selectableClearSelection(mLayoutManager)
            }
        }
    }
    private fun onSelectionModeChanged(it: Boolean?) {
        if(mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            mMainFragmentViewModel.totalCount.value = mGenericListGridItemAdapter?.itemCount ?: 0
            mLayoutManager?.let { it1 ->
                mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
            }
            mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
        }
    }
    private fun updatePlayingSongUI(songItem: com.prosabdev.common.models.songitem.SongItem?) {
        val songPosition: Int = songItem?.position ?: -1

        if (
            mNowPlayingFragmentViewModel.queueListSource.value == TAG &&
            mNowPlayingFragmentViewModel.queueListSourceColumnIndex.value == mWhereColumnIndex &&
            mNowPlayingFragmentViewModel.queueListSourceColumnValue.value == mWhereColumnValue &&
            mNowPlayingFragmentViewModel.sortBy.value == mExploreContentsForFragmentViewModel.sortBy.value &&
            mNowPlayingFragmentViewModel.isInverted.value == mExploreContentsForFragmentViewModel.isInverted.value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(songPosition)
            mGenericListGridItemAdapter?.setIsPlaying(
                mNowPlayingFragmentViewModel.getIsPlaying().value ?: false
            )
            tryToScrollOnCurrentItem(songPosition)
        } else {
            if ((mGenericListGridItemAdapter?.getPlayingPosition() ?: -1) >= 0)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
        }
    }
    private fun tryToScrollOnCurrentItem(position: Int) {
        if (position >= 0) {
            val tempCanScrollToPlayingSong: Boolean =
                mNowPlayingFragmentViewModel.getCanScrollCurrentPlayingSong().value ?: false
            if (!tempCanScrollToPlayingSong) return
            mNowPlayingFragmentViewModel.setCanScrollCurrentPlayingSong(false)
            val tempFV: Int = (mLayoutManager?.findFirstVisibleItemPosition() ?: 0) - 1
            val tempLV: Int = mLayoutManager?.findLastVisibleItemPosition() ?: +1
            val tempVisibility: Boolean = position in tempFV..tempLV
            if (!tempVisibility) return
            context?.let { ctx ->
                val tempListSize: Int = mGenericListGridItemAdapter?.currentList?.size ?: 0
                val tempTargetPosition =
                    if (position + 2 <= tempListSize) position + 2 else tempListSize
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
        if (
            mNowPlayingFragmentViewModel.queueListSource.value == TAG &&
            mNowPlayingFragmentViewModel.queueListSourceColumnIndex.value == mWhereColumnIndex &&
            mNowPlayingFragmentViewModel.queueListSourceColumnValue.value == mWhereColumnValue &&
            mNowPlayingFragmentViewModel.sortBy.value == mExploreContentsForFragmentViewModel.sortBy.value &&
            mNowPlayingFragmentViewModel.isInverted.value == mExploreContentsForFragmentViewModel.isInverted.value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
            mGenericListGridItemAdapter?.setIsPlaying(isPlaying)
            tryToScrollOnCurrentItem(
                mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
        } else {
            if (mGenericListGridItemAdapter?.getIsPlaying() == true) {
                mGenericListGridItemAdapter?.setIsPlaying(false)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
            }
        }
    }

    private fun checkInteractions() {
        mDataBiding?.let { dataBidingView ->
            dataBidingView.topAppBar.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            dataBidingView.topAppBar.setOnMenuItemClickListener {
                if(it?.itemId == R.id.search){
                    Log.i(TAG, "ON CLICK TO SEARCH PAGE")
                }
                true
            }
            dataBidingView.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if(mIsDraggingToScroll){
                        if(dy < 0){
                            Log.i(TAG, "Scrolling --> TOP")
                            mMainFragmentViewModel.scrollingState.value = -1
                        }else if(dy > 0){
                            Log.i(TAG, "Scrolling --> BOTTOM")
                            mMainFragmentViewModel.scrollingState.value = 1 }
                        if (!recyclerView.canScrollVertically(1) && dy > 0) {
                            Log.i(TAG, "Scrolled to BOTTOM")
                            mMainFragmentViewModel.scrollingState.value = 2 } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                            Log.i(TAG, "Scrolled to TOP")
                            mMainFragmentViewModel.scrollingState.value = -2
                        }
                    }else{
                        if (!recyclerView.canScrollVertically(1) && dy > 0) {
                            Log.i(TAG, "Scrolled to BOTTOM")
                            if(mMainFragmentViewModel.scrollingState.value != 2)
                                mMainFragmentViewModel.scrollingState.value = 2 } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                            Log.i(TAG, "Scrolled to TOP")
                            if(mMainFragmentViewModel.scrollingState.value != -2)
                                mMainFragmentViewModel.scrollingState.value = -2
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
    }

    private fun setupRecyclerViewAdapter() {
        if(mGenericListGridItemAdapter != null) return
        val ctx : Context = context ?: return

        //Setup headline adapter
        val listHeadlines : ArrayList<Int> = ArrayList()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                PlaybackActions.playSongAtPositionFromGenericAdapterView(
                    mNowPlayingFragmentViewModel,
                    mExploreContentsForFragmentViewModel,
                    mGenericListGridItemAdapter,
                    TAG,
                    mWhereColumnIndex,
                    mWhereColumnValue,
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
                override fun onRequestDataInfo(dataItem: Any, position: Int): com.prosabdev.common.models.generic.GenericItemListGrid? {
                    return SongItem.castDataItemToGeneric(ctx, dataItem)
                }
                override fun onRequestTextIndexForFastScroller(
                    dataItem: Any,
                    position: Int
                ): String {
                    return SongItem.getStringIndexForFastScroller(dataItem)
                }
            },
            object : GenericListGridItemAdapter.OnItemClickListener{
                override fun onItemClicked(
                    position: Int,
                    imageviewCoverArt: CustomShapeableImageViewImageViewRatio11,
                    textTitle: MaterialTextView,
                    textSubtitle: MaterialTextView,
                    textDetails: MaterialTextView
                ) {
                    if(mMainFragmentViewModel.selectMode.value == true){
                        mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                    }else{
                        PlaybackActions.playSongAtPositionFromGenericAdapterView(
                            mNowPlayingFragmentViewModel,
                            mExploreContentsForFragmentViewModel,
                            mGenericListGridItemAdapter,
                            TAG,
                            mWhereColumnIndex,
                            mWhereColumnValue,
                            position
                        )
                    }
                }
                override fun onItemLongPressed(position: Int) {
                    mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                }
            },
            object : SelectableItemListAdapter.OnSelectSelectableItemListener{
                override fun onSelectModeChange(selectMode: Boolean) {
                    if(selectMode){
                        mMainFragmentViewModel.currentSelectablePage.value = TAG
                    }
                    mMainFragmentViewModel.selectMode.value = selectMode
                }
                override fun onRequestGetStringIndex(position: Int): String {
                    return SongItem.getStringIndexForSelection(
                        mGenericListGridItemAdapter?.currentList?.get(position)
                    )
                }
                override fun onSelectedListChange(selectedList: HashMap<Int, String>) {
                    mMainFragmentViewModel.selectedDataList.value = selectedList
                }
            },
            SongItem.diffCallback as DiffUtil.ItemCallback<Any>,
            mExploreContentsForFragmentViewModel.organizeListGrid.value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            mIsSelectable = true,
            mHavePlaybackState = true,
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
        val initialSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.organizeListGrid.value)
        mLayoutManager = GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, false)
        mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.organizeListGrid.value)
                val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
                return when (position) {
                    0 -> updatedSpan
                    ((mLayoutManager?.itemCount ?: 0) - 1) -> updatedSpan
                    else -> 1
                }
            }
        }
        mDataBiding?.let { dataBidingView ->
            MainScope().launch {
                dataBidingView.recyclerView.adapter = mConcatAdapter
                dataBidingView.recyclerView.layoutManager = mLayoutManager
            }
            val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.organizeListGrid.value)
            val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
            mItemDecoration = GridSpacingItemDecoration(updatedSpan)
            mItemDecoration?.let {
                MainScope().launch {
                    dataBidingView.recyclerView.addItemDecoration(it)
                }
            }

            dataBidingView.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
            dataBidingView.fastScroller.attachRecyclerView(dataBidingView.recyclerView)
            dataBidingView.fastScroller.setFastScrollListener(object :
                FastScroller.FastScrollListener {
                override fun onFastScrollStart(fastScroller: FastScroller) {
                    mMainFragmentViewModel.isFastScrolling.value = true
                }

                override fun onFastScrollStop(fastScroller: FastScroller) {
                    mMainFragmentViewModel.isFastScrolling.value = false
                    if (mDataBiding?.recyclerView?.canScrollVertically(-1) == false) {
                        //On scrolled to top
                        mMainFragmentViewModel.scrollingState.value = -2
                    }else if(mDataBiding?.recyclerView?.canScrollVertically(1) == false){
                        //On scrolled to bottom
                        mMainFragmentViewModel.scrollingState.value = 2
                    }
                }
            })
        }
    }
    private fun showSortDialog() {
        if(mSortSongDialog.isVisible) return

        mSortSongDialog.updateBottomSheetData(
            mExploreContentsForFragmentViewModel,
            TAG,
            mLoadSongFromSource
        )
        mSortSongDialog.show(childFragmentManager, SortSongsBottomSheetDialogFragment.TAG)
    }

    private fun showOrganizeDialog() {
        if(mOrganizeDialog.isVisible) return

        mOrganizeDialog.updateBottomSheetData(
            mExploreContentsForFragmentViewModel,
            TAG,
            mLoadSongFromSource
        )
        mOrganizeDialog.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }

    private fun playSongOnShuffle() {
        if((mGenericListGridItemAdapter?.currentList?.size ?: 0) <= 0) return

        val randomExcludedNumber: Int =
            com.prosabdev.common.utils.MathComputations.randomExcluded(
                mGenericListGridItemAdapter?.getPlayingPosition() ?: -1,
                (mGenericListGridItemAdapter?.currentList?.size ?: 0) -1
            )
        mGenericListGridItemAdapter?.let { genericListGridItemAdapter ->
            PlaybackActions.playSongAtPositionFromGenericAdapterView(
                mNowPlayingFragmentViewModel,
                mExploreContentsForFragmentViewModel,
                genericListGridItemAdapter,
                TAG,
                mWhereColumnIndex,
                mWhereColumnValue,
                randomExcludedNumber,
                PlaybackStateCompat.REPEAT_MODE_NONE,
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            )
        }
        updateRecyclerViewScrollingSate()
    }
    private fun updateRecyclerViewScrollingSate(){
        if(mDataBiding?.recyclerView?.scrollState == RecyclerView.SCROLL_STATE_SETTLING){
            mIsDraggingToScroll = false
        }
        mMainFragmentViewModel.scrollingState.value = -1
    }

    private fun initViews() {
        mMainFragmentViewModel.scrollingState.value = -2

        mDataBiding?.recyclerView?.setHasFixedSize(true)
        mDataBiding?.constraintFastScrollerContainer?.let {
            InsetModifiers.updateBottomViewInsets(
                it
            )
        }
        updateCoverArts()
        updateBackButtonTitle()
        updateTextUI()
    }

    private fun updateTextUI() {
        mDataBiding?.let { dataBidingView ->
            dataBidingView.textTitle.text = mTextTitle
            dataBidingView.textSubtitle.text = mTextSubTitle
            dataBidingView.textDetails.text = mTextDetails
        }
    }

    private fun updateCoverArts() {
        context?.let { ctx ->
            val imageRequestLargeImage = com.prosabdev.common.utils.ImageLoaders.ImageRequestItem.newOriginalMediumCardInstance()
            imageRequestLargeImage.uri = Uri.parse(mImageUri ?: return)
            imageRequestLargeImage.hashedCovertArtSignature = mHashedCoverArtSignature
            imageRequestLargeImage.imageView = mDataBiding?.imageViewCoverArt
            com.prosabdev.common.utils.ImageLoaders.startExploreContentImageLoaderJob(
                ctx,
                imageRequestLargeImage
            )
        }
    }

    private fun updateBackButtonTitle() {
        mDataBiding?.let { dataBidingView ->
            when (mLoadSongFromSource) {
                AlbumsFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title = context?.resources?.getString(R.string.album)
                }
                AlbumArtistsFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title =
                        context?.resources?.getString(R.string.album_artist)
                }
                ArtistsFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title =
                        context?.resources?.getString(R.string.artist)
                }
                ComposersFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title =
                        context?.resources?.getString(R.string.composer)
                }
                FoldersFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title =
                        context?.resources?.getString(R.string.folder)
                }
                GenresFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title = context?.resources?.getString(R.string.genre)
                }
                YearsFragment.TAG -> {
                    dataBidingView.collapsingToolBarLayout.title = context?.resources?.getString(R.string.year)
                }
            }
        }
    }

    fun updateInstanceData(
        sharedPrefsKey: String,
        loadSongFromSource: String?,
        whereColumnIndex : String?,
        columnValue : String?,
        imageUri : String?,
        hashedCoverArtSignature : Int,
        textTitle : String?,
        textSubTitle : String?,
        textDetails : String?,
    ){
        mLoadSongFromSource = loadSongFromSource
        mWhereColumnIndex = whereColumnIndex
        mWhereColumnValue = columnValue

        mSharedPrefsKey = sharedPrefsKey
        mImageUri = imageUri
        mHashedCoverArtSignature = hashedCoverArtSignature
        mTextTitle = textTitle
        mTextSubTitle = textSubTitle
        mTextDetails = textDetails

        mMainFragmentViewModel.scrollingState.value = -2
        loadPrefsAndInitViewModel()
        updateCoverArts()
        updateBackButtonTitle()
        updateTextUI()
        setupRecyclerViewAdapter()
    }

    companion object {
        const val TAG = "ExploreContentsFor"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = MainConst.ORGANIZE_LIST_SMALL
        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = com.prosabdev.common.models.songitem.SongItem.DEFAULT_INDEX
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance(
            sharedPrefsKey: String,
            loadSongFromSource: String?,
            whereColumnIndex : String?,
            columnValue : String?,
            imageUri : String?,
            hashedCoverArtSignature : Int,
            textTitle : String?,
            textSubTitle : String?,
            textDetails : String?,
        ) =
            ExploreContentsForFragment().apply {
                mLoadSongFromSource = loadSongFromSource
                mWhereColumnIndex = whereColumnIndex
                mWhereColumnValue = columnValue

                mSharedPrefsKey = sharedPrefsKey
                mImageUri = imageUri
                mHashedCoverArtSignature = hashedCoverArtSignature
                mTextTitle = textTitle
                mTextSubTitle = textSubTitle
                mTextDetails = textDetails

            }
    }
}