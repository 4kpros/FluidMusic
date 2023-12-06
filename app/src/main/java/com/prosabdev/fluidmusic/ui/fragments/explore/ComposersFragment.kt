package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.common.constants.MainConst
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentComposersBinding
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortContentExplorerBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.custom.CenterSmoothScroller
import com.prosabdev.fluidmusic.ui.custom.CustomShapeableImageViewImageViewRatio11
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.ComposersFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ComposerItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComposersFragment : Fragment() {
    private var mDataBinding: FragmentComposersBinding? = null

    private val mComposersFragmentViewModel: ComposersFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel by activityViewModels()

    private val mComposerItemViewModel: ComposerItemViewModel by activityViewModels()

    private var mOrganizeDialog: OrganizeItemBottomSheetDialogFragment =
        OrganizeItemBottomSheetDialogFragment.newInstance()
    private val mSortComposersDialog: SortContentExplorerBottomSheetDialogFragment =
        SortContentExplorerBottomSheetDialogFragment.newInstance()

    private var mConcatAdapter: ConcatAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mGenericListGridItemAdapter: GenericListGridItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var mItemDecoration: GridSpacingItemDecoration? = null

    private var mIsDraggingToScroll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        if (savedInstanceState == null) {
            loadPrefsAndInitViewModel()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_composers, container, false)
        val view = mDataBinding?.root

        if (savedInstanceState == null) {
            initViews()
            MainScope().launch {
                setupRecyclerViewAdapter()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            checkInteractions()
            observeLiveData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveAllDataToPref()
    }

    private fun saveAllDataToPref() {
        context?.let { ctx ->
            val tempSortOrganize = com.prosabdev.common.persistence.models.SortOrganizeItemSP()
            tempSortOrganize.sortOrderBy =
                mComposersFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.organizeListGrid =
                mComposersFragmentViewModel.getOrganizeListGrid().value
                    ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            tempSortOrganize.isInvertSort = mComposersFragmentViewModel.getIsInverted().value
                ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            com.prosabdev.common.persistence.SharedPreferenceManagerUtils
                .SortAnOrganizeForExploreContents
                .saveSortOrganizeItemsFor(
                    ctx,
                    com.prosabdev.common.persistence.SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_COMPOSERS,
                    tempSortOrganize
                )
        }
    }

    private fun loadPrefsAndInitViewModel() {
        context?.let { ctx ->
            val tempSortOrganize: com.prosabdev.common.persistence.models.SortOrganizeItemSP =
                com.prosabdev.common.persistence.SharedPreferenceManagerUtils
                    .SortAnOrganizeForExploreContents
                    .loadSortOrganizeItemsFor(
                        ctx,
                        com.prosabdev.common.persistence.SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_COMPOSERS
                    )
            tempSortOrganize?.let { sortOrganize ->
                mComposersFragmentViewModel.setSortBy(sortOrganize.sortOrderBy)
                mComposersFragmentViewModel.setOrganizeListGrid(sortOrganize.organizeListGrid)
                mComposersFragmentViewModel.setIsInverted(sortOrganize.isInvertSort)
            }
            if (tempSortOrganize == null) {
                mComposersFragmentViewModel.setSortBy(SORT_LIST_GRID_DEFAULT_VALUE)
                mComposersFragmentViewModel.setOrganizeListGrid(ORGANIZE_LIST_GRID_DEFAULT_VALUE)
                mComposersFragmentViewModel.setIsInverted(IS_INVERTED_LIST_GRID_DEFAULT_VALUE)
            }
        }
    }

    private fun observeLiveData() {
        mComposersFragmentViewModel.getAll().observe(viewLifecycleOwner) {
            addDataToGenericAdapter(it)
        }
        mComposersFragmentViewModel.getSortBy().observe(viewLifecycleOwner) {
            requestNewDataFromDatabase()
        }
        mComposersFragmentViewModel.getIsInverted().observe(viewLifecycleOwner) {
            invertSongListAndUpdateAdapter(it)
        }
        mComposersFragmentViewModel.getOrganizeListGrid().observe(viewLifecycleOwner) {
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
        mMainFragmentViewModel.getSelectMode().observe(viewLifecycleOwner) {
            onSelectionModeChanged(it)
        }
        mMainFragmentViewModel.getReQuestToggleSelectAll().observe(viewLifecycleOwner) {
            onReQuestToggleSelectAll(it)
        }
        mMainFragmentViewModel.getReQuestToggleSelectRange().observe(viewLifecycleOwner) {
            onReQuestToggleSelectRange(it)
        }
        mMainFragmentViewModel.getScrollingState().observe(viewLifecycleOwner) {
            updateOnScrollingStateUI(it)
        }
    }

    private fun updateOrganizeListGrid(organizeValue: Int?) {
        context?.let { ctx ->
            val tempSpanCount: Int =
                OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, organizeValue)
            mGenericListGridItemAdapter?.setOrganizeListGrid(
                mComposersFragmentViewModel.getOrganizeListGrid().value
                    ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE
            )
            mLayoutManager?.spanCount = tempSpanCount
        }
    }

    private fun invertSongListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewIsInverted: Boolean = isInverted ?: false
        if (tempNewIsInverted) {
            mGenericListGridItemAdapter?.submitList(mComposersFragmentViewModel.getAll().value?.reversed())
        } else {
            mGenericListGridItemAdapter?.submitList(mComposersFragmentViewModel.getAll().value)
        }
        if (
            mNowPlayingFragmentViewModel.getQueueListSource().value == TAG &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value == null &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value == null &&
            mNowPlayingFragmentViewModel.getSortBy().value == mComposersFragmentViewModel.getSortBy().value &&
            mNowPlayingFragmentViewModel.getIsInverted().value == mComposersFragmentViewModel.getIsInverted().value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
            mGenericListGridItemAdapter?.setIsPlaying(
                mNowPlayingFragmentViewModel.getIsPlaying().value ?: false
            )
        } else {
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun requestNewDataFromDatabase() {
        if (mComposersFragmentViewModel.getSortBy().value?.isEmpty() == true) return
        MainScope().launch {
            mComposersFragmentViewModel.requestDataDirectlyFromDatabase(
                mComposerItemViewModel
            )
        }
    }

    private fun addDataToGenericAdapter(dataList: List<Any>?) {
        if (mComposersFragmentViewModel.getIsInverted().value == true) {
            mGenericListGridItemAdapter?.submitList(dataList?.reversed())
        } else {
            mGenericListGridItemAdapter?.submitList(dataList)
        }
        if (mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mMainFragmentViewModel.setTotalCount(dataList?.size ?: 0)
        }
        if (
            mNowPlayingFragmentViewModel.getQueueListSource().value == TAG &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value == null &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value == null &&
            mNowPlayingFragmentViewModel.getSortBy().value == mComposersFragmentViewModel.getSortBy().value &&
            mNowPlayingFragmentViewModel.getIsInverted().value == mComposersFragmentViewModel.getIsInverted().value
        ) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
            )
            mGenericListGridItemAdapter?.setIsPlaying(
                mNowPlayingFragmentViewModel.getIsPlaying().value ?: false
            )
        } else {
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun updateOnScrollingStateUI(i: Int) {
        if (mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            if (i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }

    private fun onReQuestToggleSelectRange(requestCount: Int?) {
        if (requestCount == null || requestCount <= 0) return
        if (mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }

    private fun onReQuestToggleSelectAll(requestCount: Int?) {
        if (requestCount == null || requestCount <= 0) return
        if (mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            val totalItemCount = mGenericListGridItemAdapter?.itemCount ?: 0
            val selectedItemCount =
                mGenericListGridItemAdapter?.selectableGetSelectedItemCount() ?: 0
            if (totalItemCount > selectedItemCount) {
                mGenericListGridItemAdapter?.selectableSelectAll(mLayoutManager)
            } else {
                mGenericListGridItemAdapter?.selectableClearSelection(mLayoutManager)
            }
        }
    }

    private fun onSelectionModeChanged(it: Boolean?) {
        if (mMainFragmentViewModel.getCurrentSelectablePage().value == TAG) {
            mMainFragmentViewModel.setTotalCount(mGenericListGridItemAdapter?.itemCount ?: 0)
            mLayoutManager?.let { it1 ->
                mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
            }
            mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
        }
    }

    private fun updatePlayingSongUI(songItem: com.prosabdev.common.models.songitem.SongItem?) {
        val songPosition: Int = songItem?.position ?: -1

        if (
            mNowPlayingFragmentViewModel.getQueueListSource().value == TAG &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value == null &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value == null &&
            mNowPlayingFragmentViewModel.getSortBy().value == mComposersFragmentViewModel.getSortBy().value &&
            mNowPlayingFragmentViewModel.getIsInverted().value == mComposersFragmentViewModel.getIsInverted().value
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
            mNowPlayingFragmentViewModel.getQueueListSource().value == TAG &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value == null &&
            mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value == null &&
            mNowPlayingFragmentViewModel.getSortBy().value == mComposersFragmentViewModel.getSortBy().value &&
            mNowPlayingFragmentViewModel.getIsInverted().value == mComposersFragmentViewModel.getIsInverted().value
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
        mDataBinding?.recyclerView?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mIsDraggingToScroll) {
                    if (dy < 0) {
                        Log.i(TAG, "Scrolling --> TOP")
                        mMainFragmentViewModel.setScrollingState(-1)
                    } else if (dy > 0) {
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
                } else {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(TAG, "Scrolled to BOTTOM")
                        if (mMainFragmentViewModel.getScrollingState().value != 2)
                            mMainFragmentViewModel.setScrollingState(2)
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(TAG, "Scrolled to TOP")
                        if (mMainFragmentViewModel.getScrollingState().value != -2)
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
        withContext(Dispatchers.Default) {
            val ctx: Context = context ?: return@withContext
            //Setup headline adapter
            val listHeadlines: ArrayList<Int> = ArrayList()
            listHeadlines.add(0)
            mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(
                listHeadlines,
                object : HeadlinePlayShuffleAdapter.OnItemClickListener {
                    override fun onPlayButtonClicked() {
                        playFirstSong()
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
                object : GenericListGridItemAdapter.OnItemRequestDataInfo {
                    override fun onRequestDataInfo(
                        dataItem: Any,
                        position: Int
                    ): com.prosabdev.common.models.generic.GenericItemListGrid? {
                        return com.prosabdev.common.models.view.ComposerItem.castDataItemToGeneric(
                            ctx,
                            dataItem
                        )
                    }

                    override fun onRequestTextIndexForFastScroller(
                        dataItem: Any,
                        position: Int
                    ): String {
                        return com.prosabdev.common.models.view.ComposerItem.getStringIndexForFastScroller(
                            dataItem
                        )
                    }
                },
                object : GenericListGridItemAdapter.OnItemClickListener {
                    override fun onItemClicked(
                        position: Int,
                        imageviewCoverArt: CustomShapeableImageViewImageViewRatio11,
                        textTitle: MaterialTextView,
                        textSubtitle: MaterialTextView,
                        textDetails: MaterialTextView
                    ) {
                        if (mMainFragmentViewModel.getSelectMode().value == true) {
                            mGenericListGridItemAdapter?.selectableSelectFromPosition(
                                position,
                                mLayoutManager
                            )
                        } else {
                            openExploreContentFragment(position)
                        }
                    }

                    override fun onItemLongPressed(position: Int) {
                        mGenericListGridItemAdapter?.selectableSelectFromPosition(
                            position,
                            mLayoutManager
                        )
                    }
                },
                object : SelectableItemListAdapter.OnSelectSelectableItemListener {
                    override fun onSelectModeChange(selectMode: Boolean) {
                        if (selectMode) {
                            mMainFragmentViewModel.setCurrentSelectablePage(
                                TAG
                            )
                        }
                        mMainFragmentViewModel.setSelectMode(selectMode)
                    }

                    override fun onRequestGetStringIndex(position: Int): String {
                        return com.prosabdev.common.models.view.ComposerItem.getStringIndexForSelection(
                            mGenericListGridItemAdapter?.currentList?.get(position)
                        )
                    }

                    override fun onSelectedListChange(selectedList: HashMap<Int, String>) {
                        mMainFragmentViewModel.setSelectedDataList(selectedList)
                    }
                },
                com.prosabdev.common.models.view.ComposerItem.diffCallback as DiffUtil.ItemCallback<Any>,
                mComposersFragmentViewModel.getOrganizeListGrid().value
                    ?: ORGANIZE_LIST_GRID_DEFAULT_VALUE,
                mIsSelectable = true,
                mHavePlaybackState = false,
                mIsImageFullCircle = true,
            )

            //Setup empty bottom space adapter
            val listEmptyBottomSpace: ArrayList<String> = ArrayList()
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
            val initialSpanCount: Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(
                ctx,
                mComposersFragmentViewModel.getOrganizeListGrid().value
            )
            mLayoutManager =
                GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, false)
            mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val newSpanCount: Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(
                        ctx,
                        mComposersFragmentViewModel.getOrganizeListGrid().value
                    )
                    val updatedSpan: Int =
                        if (mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount
                            ?: 1
                    return when (position) {
                        0 -> updatedSpan
                        ((mLayoutManager?.itemCount ?: 0) - 1) -> updatedSpan
                        else -> 1
                    }
                }
            }
            mDataBinding?.let { dataBidingView ->
                MainScope().launch {
                    dataBidingView.recyclerView.adapter = mConcatAdapter
                    dataBidingView.recyclerView.layoutManager = mLayoutManager
                }
                val newSpanCount: Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(
                    ctx,
                    mComposersFragmentViewModel.getOrganizeListGrid().value
                )
                val updatedSpan: Int =
                    if (mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount
                        ?: 1
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
                            if (mDataBinding?.recyclerView?.canScrollVertically(-1) == false) {
                                //On scrolled to top
                                mMainFragmentViewModel.setScrollingState(-2)
                            } else if (mDataBinding?.recyclerView?.canScrollVertically(1) == false) {
                                //On scrolled to bottom
                                mMainFragmentViewModel.setScrollingState(2)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun openExploreContentFragment(position: Int) {
        val tempFragmentManager = activity?.supportFragmentManager ?: return
        val tempItem = mGenericListGridItemAdapter?.currentList?.get(position) ?: return

        context?.let { ctx ->
            val tempGeneric = com.prosabdev.common.models.view.ComposerItem.castDataItemToGeneric(
                ctx,
                tempItem,
                true
            ) ?: return
            val tempStringUri =
                if (tempGeneric.imageUri == Uri.EMPTY) "" else tempGeneric.imageUri.toString()
            tempFragmentManager.commit {
                setReorderingAllowed(false)
                add(
                    R.id.main_fragment_container,
                    ExploreContentsForFragment.newInstance(
                        com.prosabdev.common.persistence.SharedPreferenceManagerUtils
                            .SortAnOrganizeForExploreContents
                            .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER,
                        TAG,
                        com.prosabdev.common.models.view.ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
                        tempGeneric.name,
                        tempStringUri,
                        tempGeneric.imageHashedSignature,
                        tempGeneric.title,
                        tempGeneric.subtitle,
                        tempGeneric.details,
                    )
                )
                addToBackStack(TAG)
            }
        }
    }

    private fun showSortDialog() {
        if (mSortComposersDialog.isVisible) return

        mSortComposersDialog.updateBottomSheetData(
            mComposersFragmentViewModel,
            TAG,
            null
        )
        mSortComposersDialog.show(
            childFragmentManager,
            SortContentExplorerBottomSheetDialogFragment.TAG
        )
    }

    private fun showOrganizeDialog() {
        if (mOrganizeDialog.isVisible) return

        mOrganizeDialog.updateBottomSheetData(
            mComposersFragmentViewModel,
            TAG,
            null
        )
        mOrganizeDialog.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }

    private fun playSongOnShuffle() {
        if ((mGenericListGridItemAdapter?.currentList?.size ?: 0) <= 0) return
        MainScope().launch {
            withContext(Dispatchers.Default) {
                val randomExcludedNumber: Int =
                    com.prosabdev.common.utils.MathComputations.randomExcluded(
                        mGenericListGridItemAdapter?.getPlayingPosition() ?: -1,
                        (mGenericListGridItemAdapter?.currentList?.size ?: 0) - 1
                    )

                //Load song and play
                updateRecyclerViewScrollingSate()
            }
        }
    }

    private fun playFirstSong() {
        //
    }

    private fun updateRecyclerViewScrollingSate() {
        if (mDataBinding?.recyclerView?.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
            mIsDraggingToScroll = false
        }
        mMainFragmentViewModel.setScrollingState(-1)
    }


    private fun initViews() {
        mDataBinding?.recyclerView?.setHasFixedSize(true)
        mDataBinding?.constraintFastScrollerContainer?.let {
            com.prosabdev.common.utils.InsetModifiers.updateBottomViewInsets(
                it
            )
        }
    }

    companion object {
        const val TAG = "ComposersFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = MainConst.ORGANIZE_GRID_LARGE
        private const val SORT_LIST_GRID_DEFAULT_VALUE: String =
            com.prosabdev.common.models.view.ComposerItem.DEFAULT_INDEX
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance() =
            ComposersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}