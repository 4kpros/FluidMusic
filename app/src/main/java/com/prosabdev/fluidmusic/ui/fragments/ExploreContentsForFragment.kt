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
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentExploreContentsForBinding
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortSongsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.custom.CenterSmoothScroller
import com.prosabdev.fluidmusic.ui.fragments.commonmethods.CommonPlaybackAction
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.utils.MathComputationsUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ExploreContentsForFragment : Fragment() {
    private var mLoadSongFromSource: String? = null
    private var mWhereColumnIndex: String? = null
    private var mWhereColumnValue: String? = null

    private var mSharedPrefsKey: String? = null
    private var mImageUri: String? = null
    private var mHashedCoverArtSignature: Int = -1
    private var mTextTitle: String? = null
    private var mTextSubTitle: String? = null
    private var mTextDetails: String? = null

    private var mDataBidingView: FragmentExploreContentsForBinding? = null

    private val mExploreContentsForFragmentViewModel: ExploreContentsForFragmentViewModel by activityViewModels()
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
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_explore_contents_for,container,false)
        val view = mDataBidingView?.root

        initViews()
        MainScope().launch {
            setupRecyclerViewAdapter()
        }
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
            tempSortOrganize.sortOrderBy = mExploreContentsForFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.organizeListGrid = mExploreContentsForFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.isInvertSort = mExploreContentsForFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            SharedPreferenceManagerUtils
                .SortAnOrganizeForExploreContents
                .saveSortOrganizeItemsFor(
                    ctx,
                    mSharedPrefsKey ?: return,
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
                        mSharedPrefsKey ?: return
                    )
            if(tempSortOrganize != null){
                mExploreContentsForFragmentViewModel.setSortBy(tempSortOrganize.sortOrderBy)
                mExploreContentsForFragmentViewModel.setOrganizeListGrid(tempSortOrganize.organizeListGrid)
                mExploreContentsForFragmentViewModel.setIsInverted(tempSortOrganize.isInvertSort)
            }else{
                mExploreContentsForFragmentViewModel.setSortBy(SORT_LIST_GRID_DEFAULT_VALUE)
                mExploreContentsForFragmentViewModel.setOrganizeListGrid(ORGANIZE_LIST_GRID_DEFAULT_VALUE)
                mExploreContentsForFragmentViewModel.setIsInverted(IS_INVERTED_LIST_GRID_DEFAULT_VALUE)
            }
        }
    }
    private fun observeLiveData() {
        mExploreContentsForFragmentViewModel.getAll().observe(viewLifecycleOwner){
            addDataToGenericAdapter(it)
        }
        mExploreContentsForFragmentViewModel.getSortBy().observe(viewLifecycleOwner){
            requestNewDataFromDatabase()
        }
        mExploreContentsForFragmentViewModel.getIsInverted().observe(viewLifecycleOwner){
            invertSongListAndUpdateAdapter(it)
        }
        mExploreContentsForFragmentViewModel.getOrganizeListGrid().observe(viewLifecycleOwner){
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
            mGenericListGridItemAdapter?.setOrganizeListGrid(mExploreContentsForFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE)
            mLayoutManager?.spanCount = tempSpanCount
        }
    }
    private fun invertSongListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewIsInverted : Boolean = isInverted ?: false
        if(tempNewIsInverted){
            mGenericListGridItemAdapter?.submitList(mExploreContentsForFragmentViewModel.getAll().value?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(mExploreContentsForFragmentViewModel.getAll().value)
        }
        if(
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mExploreContentsForFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mExploreContentsForFragmentViewModel.getIsInverted().value
        ){
            mGenericListGridItemAdapter?.setPlayingPosition(mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)
            mGenericListGridItemAdapter?.setIsPlaying(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        }else{
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }
    private fun requestNewDataFromDatabase() {
        if(mExploreContentsForFragmentViewModel.getSortBy().value?.isEmpty() == true) return
        Log.i(TAG, "REQUEST DATA FOR mLoadSongFromSource ${mLoadSongFromSource}")
        Log.i(TAG, "REQUEST DATA FOR mWhereColumnIndex ${mWhereColumnIndex}")
        Log.i(TAG, "REQUEST DATA FOR mWhereColumnValue ${mWhereColumnValue}")
        MainScope().launch {
            mExploreContentsForFragmentViewModel.requestDataDirectlyWhereColumnEqualFromDatabase(
                mSongItemViewModel,
                mWhereColumnIndex ?: return@launch,
                mWhereColumnValue,
            )
        }
    }
    private fun addDataToGenericAdapter(dataList: List<Any>?) {
        if(mExploreContentsForFragmentViewModel.getIsInverted().value == true){
            mGenericListGridItemAdapter?.submitList(dataList?.reversed())
        }else{
            mGenericListGridItemAdapter?.submitList(dataList)
        }
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG){
            mMainFragmentViewModel.setTotalCount(dataList?.size ?: 0)
        }
        if(
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mExploreContentsForFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mExploreContentsForFragmentViewModel.getIsInverted().value
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
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }
    private fun onReQuestToggleSelectAll(requestCount: Int?) {
        if(requestCount == null || requestCount <= 0) return
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG){
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
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mMainFragmentViewModel.setTotalCount(mGenericListGridItemAdapter?.itemCount ?: 0)
            mLayoutManager?.let { it1 ->
                mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
            }
            mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
        }
    }
    private fun updatePlayingSongUI(songItem: SongItem?) {
        val songPosition: Int = songItem?.position ?: -1

        if (
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mExploreContentsForFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mExploreContentsForFragmentViewModel.getIsInverted().value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(songPosition)
            mGenericListGridItemAdapter?.setIsPlaying(
                mPlayerFragmentViewModel.getIsPlaying().value ?: false
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
                mPlayerFragmentViewModel.getCanScrollCurrentPlayingSong().value ?: false
            if (!tempCanScrollToPlayingSong) return
            mPlayerFragmentViewModel.setCanScrollCurrentPlayingSong(false)
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
            mPlayerFragmentViewModel.getQueueListSource().value == TAG &&
            mPlayerFragmentViewModel.getSortBy().value == mExploreContentsForFragmentViewModel.getSortBy().value &&
            mPlayerFragmentViewModel.getIsInverted().value == mExploreContentsForFragmentViewModel.getIsInverted().value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
            mGenericListGridItemAdapter?.setIsPlaying(isPlaying)
            tryToScrollOnCurrentItem(
                mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
        } else {
            if (mGenericListGridItemAdapter?.getIsPlaying() == true) {
                mGenericListGridItemAdapter?.setIsPlaying(false)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
            }
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

    private suspend fun setupRecyclerViewAdapter() {
        withContext(Dispatchers.Default){
            val ctx : Context = context ?: return@withContext
            //Setup headline adapter
            val listHeadlines : ArrayList<Int> = ArrayList()
            listHeadlines.add(0)
            mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                    CommonPlaybackAction.playSongAtPositionFromGenericAdapterView(
                        mPlayerFragmentViewModel,
                        mExploreContentsForFragmentViewModel,
                        mGenericListGridItemAdapter,
                        TAG,
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
                        return SongItem.getStringIndexForFastScroller(dataItem)
                    }
                },
                object : GenericListGridItemAdapter.OnItemClickListener{
                    override fun onItemClicked(position: Int) {
                        if(mMainFragmentViewModel.getSelectMode().value == true){
                            mGenericListGridItemAdapter?.selectableSelectFromPosition(position, mLayoutManager)
                        }else{
                            CommonPlaybackAction.playSongAtPositionFromGenericAdapterView(
                                mPlayerFragmentViewModel,
                                mExploreContentsForFragmentViewModel,
                                mGenericListGridItemAdapter,
                                TAG,
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
                        mMainFragmentViewModel.setSelectMode(selectMode)
                    }
                    override fun onRequestGetStringIndex(position: Int): String {
                        return SongItem.getStringIndexForSelection(
                            mGenericListGridItemAdapter?.currentList?.get(position)
                        )
                    }
                    override fun onSelectedListChange(selectedList: HashMap<Int, String>) {
                        mMainFragmentViewModel.setSelectedDataList(selectedList)
                    }
                },
                SongItem.diffCallback as DiffUtil.ItemCallback<Any>,
                mExploreContentsForFragmentViewModel.getOrganizeListGrid().value ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
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
            val initialSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.getOrganizeListGrid().value)
            mLayoutManager = GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, false)
            mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.getOrganizeListGrid().value)
                    val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
                    return when (position) {
                        0 -> updatedSpan
                        ((mLayoutManager?.itemCount ?: 0) - 1) -> updatedSpan
                        else -> 1
                    }
                }
            }
            mDataBidingView?.let { dataBidingView ->
                MainScope().launch {
                    dataBidingView.recyclerView.adapter = mConcatAdapter
                    dataBidingView.recyclerView.layoutManager = mLayoutManager
                }
                val newSpanCount : Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, mExploreContentsForFragmentViewModel.getOrganizeListGrid().value)
                val updatedSpan : Int = if(mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount ?: 1
                mItemDecoration = GridSpacingItemDecoration(updatedSpan)
                mItemDecoration?.let {
                    MainScope().launch {
                        dataBidingView.recyclerView.addItemDecoration(it)
                    }
                }

                MainScope().launch {
                    dataBidingView.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
                    dataBidingView.fastScroller.attachRecyclerView(dataBidingView.recyclerView)
                    dataBidingView.fastScroller.setFastScrollListener(object :
                        FastScroller.FastScrollListener {
                        override fun onFastScrollStart(fastScroller: FastScroller) {
                            mMainFragmentViewModel.setIsFastScrolling(true)
                        }

                        override fun onFastScrollStop(fastScroller: FastScroller) {
                            mMainFragmentViewModel.setIsFastScrolling(false)
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
        MainScope().launch {
            withContext(Dispatchers.Default){
                val randomExcludedNumber: Int =
                    MathComputationsUtils.randomExcluded(
                        mGenericListGridItemAdapter?.getPlayingPosition() ?: -1,
                        (mGenericListGridItemAdapter?.currentList?.size ?: 0) -1
                    )
                mGenericListGridItemAdapter?.let { genericListGridItemAdapter ->
                    CommonPlaybackAction.playSongAtPositionFromGenericAdapterView(
                        mPlayerFragmentViewModel,
                        mExploreContentsForFragmentViewModel,
                        genericListGridItemAdapter,
                        TAG,
                        randomExcludedNumber,
                        PlaybackStateCompat.REPEAT_MODE_NONE,
                        PlaybackStateCompat.SHUFFLE_MODE_NONE
                    )
                }
                updateRecyclerViewScrollingSate()
            }
        }
    }
    private fun updateRecyclerViewScrollingSate(){
        if(mDataBidingView?.recyclerView?.scrollState == RecyclerView.SCROLL_STATE_SETTLING){
            mIsDraggingToScroll = false
        }
        mMainFragmentViewModel.setScrollingState(-1)
    }

    private fun initViews() {
        mDataBidingView?.recyclerView?.setHasFixedSize(true)
        mDataBidingView?.constraintFastScrollerContainer?.let {
            InsetModifiersUtils.updateBottomViewInsets(
                it
            )
        }
        mDataBidingView?.let { dataBidingView ->
//            InsetModifiersUtils.removeTopViewInsets(dataBidingView.imageViewCoverArtBlurred)
//            InsetModifiersUtils.updateTopViewInsets(dataBidingView.linearTopContent)
        }
        MainScope().launch {
            updateCoverArts()
            updateBackButtonTitle()
            updateTextUI()
        }
    }

    private fun updateTextUI() {
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.textTitle.text = mTextTitle
            dataBidingView.textSubtitle.text = mTextSubTitle
            dataBidingView.textDetails.text = mTextDetails
        }
    }

    private suspend fun updateCoverArts() {
        withContext(Dispatchers.Default){
            context?.let { ctx ->
                val imageRequestLargeImage = ImageLoadersUtils.ImageRequestItem.newOriginalMediumCardInstance()
                imageRequestLargeImage.uri = Uri.parse(mImageUri)
                Log.i(TAG, "mHashedCoverArtSignature ${mHashedCoverArtSignature}")
                imageRequestLargeImage.hashedCovertArtSignature = mHashedCoverArtSignature
                imageRequestLargeImage.imageView = mDataBidingView?.imageViewCoverArt
                ImageLoadersUtils.startExploreContentImageLoaderJob(
                    ctx,
                    imageRequestLargeImage
                )
            }
        }
    }

    private fun updateBackButtonTitle() {
        mDataBidingView?.let { dataBidingView ->
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


    companion object {
        const val TAG = "ExploreContentsFor"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_LIST_MEDIUM
        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = SongItem.DEFAULT_INDEX
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