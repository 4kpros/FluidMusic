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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAlbumsBinding
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortContentExplorerBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.custom.CenterSmoothScroller
import com.prosabdev.fluidmusic.ui.custom.CustomShapeableImageViewImageViewRatio11
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentForFragment
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AlbumsFragment : Fragment() {

    //Data binding
    private lateinit var mDataBinding: FragmentAlbumsBinding

    //View models
    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayingNowFragmentViewModel: PlayingNowFragmentViewModel by activityViewModels()

    private val mMediaPlayerDataViewModel: MediaPlayerDataViewModel by activityViewModels()
    private val mMediaControllerViewModel by activityViewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mMediaPlayerDataViewModel.mediaEventsListener)
    }

    private val mAlbumItemViewModel: AlbumItemViewModel by activityViewModels()

    //Dialogs
    private var mOrganizeItemsDialog: OrganizeItemBottomSheetDialogFragment? = null
    private var mSortItemsDialog: SortContentExplorerBottomSheetDialogFragment? = null

    //Adapters
    private var mConcatAdapter: ConcatAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mGenericListGridItemAdapter: GenericListGridItemAdapter? = null

    private var mLayoutManager: GridLayoutManager? = null
    private var mItemDecoration: GridSpacingItemDecoration? = null

    private var mIsDraggingToScroll: Boolean = false

    private val isMatchingQueueMusicContent: Boolean
        get() {
            return mMediaPlayerDataViewModel.queueListSource.value == TAG &&
                    mMediaPlayerDataViewModel.queueListSourceColumnIndex.value == null &&
                    mMediaPlayerDataViewModel.queueListSourceColumnValue.value == null &&
                    mMediaPlayerDataViewModel.queueListIsInverted.value == mAlbumsFragmentViewModel.isInverted.value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_albums, container, false)
        val view = mDataBinding.root

        //Load your UI content
        if (savedInstanceState == null) {
            runBlocking {
                loadPrefsAndInitViewModel()
                initViews()
                setupRecyclerViewAdapter()
                checkInteractions()
                observeLiveData()
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        saveAllDataToPref()
    }

    private fun saveAllDataToPref() {
        val tempSortOrganize = SortOrganizeItemSP()
        tempSortOrganize.sortOrderBy =
            mAlbumsFragmentViewModel.sortBy.value ?: AlbumsFragmentViewModel.SORT_DEFAULT_VALUE
        tempSortOrganize.organizeListGrid =
            mAlbumsFragmentViewModel.organizeListGrid.value ?: AlbumsFragmentViewModel.ORGANIZE_DEFAULT_VALUE
        tempSortOrganize.isInvertSort =
            mAlbumsFragmentViewModel.isInverted.value ?: AlbumsFragmentViewModel.IS_INVERTED_DEFAULT_VALUE
        PersistentStorage
            .SortAndOrganize
            .save(
                PersistentStorage.SortAndOrganize.SORT_ORGANIZE_ALBUMS,
                tempSortOrganize
            )
    }

    private fun loadPrefsAndInitViewModel() {
        val tempSortOrganize: SortOrganizeItemSP? =
            PersistentStorage
                .SortAndOrganize
                .load(
                    PersistentStorage.SortAndOrganize.SORT_ORGANIZE_ALBUMS
                )
        tempSortOrganize?.let {
            mAlbumsFragmentViewModel.sortBy.value = it.sortOrderBy
            mAlbumsFragmentViewModel.organizeListGrid.value = it.organizeListGrid
            mAlbumsFragmentViewModel.isInverted.value = it.isInvertSort
        }
        if (tempSortOrganize == null) {
            mAlbumsFragmentViewModel.sortBy.value = AlbumsFragmentViewModel.SORT_DEFAULT_VALUE
            mAlbumsFragmentViewModel.organizeListGrid.value = AlbumsFragmentViewModel.ORGANIZE_DEFAULT_VALUE
            mAlbumsFragmentViewModel.isInverted.value = AlbumsFragmentViewModel.IS_INVERTED_DEFAULT_VALUE
        }
    }

    private fun observeLiveData() {
        mAlbumsFragmentViewModel.itemsList.observe(viewLifecycleOwner) {
            addDataToGenericAdapter(it)
        }
        mAlbumsFragmentViewModel.sortBy.observe(viewLifecycleOwner) {
            requestNewDataFromDatabase()
        }
        mAlbumsFragmentViewModel.isInverted.observe(viewLifecycleOwner) {
            invertSongListAndUpdateAdapter(it)
        }
        mAlbumsFragmentViewModel.organizeListGrid.observe(viewLifecycleOwner) {
            updateOrganizeListGrid(it)
        }

        //Listen to player changes
        mMediaPlayerDataViewModel.currentMediaItemIndex.observe(viewLifecycleOwner) {
            updateUICurrentMediaItemIndex(it)
        }
        mMediaPlayerDataViewModel.isPlaying.observe(viewLifecycleOwner) {
            updateUIIsPlaying(it)
        }

        //Listen for main fragment changes
        mMainFragmentViewModel.selectMode.observe(viewLifecycleOwner) {
            onSelectionModeChanged(it)
        }
        mMainFragmentViewModel.requestToggleSelectAll.observe(viewLifecycleOwner) {
            onReQuestToggleSelectAll(it)
        }
        mMainFragmentViewModel.requestToggleSelectRange.observe(viewLifecycleOwner) {
            onReQuestToggleSelectRange(it)
        }
        mMainFragmentViewModel.scrollingState.observe(viewLifecycleOwner) {
            updateOnScrollingStateUI(it)
        }
    }

    private fun updateOrganizeListGrid(organizeValue: Int?) {
        context?.let { ctx ->
            val tempSpanCount: Int =
                OrganizeItemBottomSheetDialogFragment.getSpanCount(ctx, organizeValue)
            mGenericListGridItemAdapter?.setOrganizeListGrid(
                mAlbumsFragmentViewModel.organizeListGrid.value ?: AlbumsFragmentViewModel.ORGANIZE_DEFAULT_VALUE
            )
            mLayoutManager?.spanCount = tempSpanCount
        }
    }

    private fun invertSongListAndUpdateAdapter(isInverted: Boolean?) {
        val tempNewIsInverted: Boolean = isInverted ?: false
        if (tempNewIsInverted) {
            mGenericListGridItemAdapter?.submitList(mAlbumsFragmentViewModel.itemsList.value?.reversed())
        } else {
            mGenericListGridItemAdapter?.submitList(mAlbumsFragmentViewModel.itemsList.value)
        }
        if (isMatchingQueueMusicContent) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1
            )
            mGenericListGridItemAdapter?.setIsPlaying(
                mMediaPlayerDataViewModel.isPlaying.value ?: false
            )
        } else {
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun requestNewDataFromDatabase() {
        if (mAlbumsFragmentViewModel.sortBy.value?.isEmpty() == true) return
        lifecycleScope.launch {
            mAlbumsFragmentViewModel.requestDataDirectlyFromDatabase(
                mAlbumItemViewModel
            )
        }
    }

    private fun addDataToGenericAdapter(itemsList: List<Any>?) {
        if (mAlbumsFragmentViewModel.isInverted.value == true) {
            mGenericListGridItemAdapter?.submitList(itemsList?.reversed())
        } else {
            mGenericListGridItemAdapter?.submitList(itemsList)
        }
        if (mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            mMainFragmentViewModel.totalCount.value = itemsList?.size ?: 0
        }
        if (isMatchingQueueMusicContent) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1
            )
            mGenericListGridItemAdapter?.setIsPlaying(
                mMediaPlayerDataViewModel.isPlaying.value ?: false
            )
        } else {
            mGenericListGridItemAdapter?.setPlayingPosition(-1)
            mGenericListGridItemAdapter?.setIsPlaying(false)
        }
    }

    private fun updateOnScrollingStateUI(i: Int) {
        if (mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            if (i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }

    private fun onReQuestToggleSelectRange(requestCount: Int?) {
        if (requestCount == null || requestCount <= 0) return
        if (mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            mGenericListGridItemAdapter?.selectableSelectRange(mLayoutManager)
        }
    }

    private fun onReQuestToggleSelectAll(requestCount: Int?) {
        if (requestCount == null || requestCount <= 0) return
        if (mMainFragmentViewModel.currentSelectablePage.value == TAG) {
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
        if (mMainFragmentViewModel.currentSelectablePage.value == TAG) {
            mMainFragmentViewModel.totalCount.value = mGenericListGridItemAdapter?.itemCount ?: 0
            mLayoutManager?.let { it1 ->
                mGenericListGridItemAdapter?.selectableSetSelectionMode(it ?: false, it1)
            }
            mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
        }
    }

    private fun updateUIIsPlaying(isPlaying: Boolean) {
        if (isMatchingQueueMusicContent) {
            mGenericListGridItemAdapter?.setPlayingPosition(
                mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1
            )
            mGenericListGridItemAdapter?.setIsPlaying(isPlaying)
            tryToScrollToCurrentItem(
                mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1
            )
        } else {
            if (mGenericListGridItemAdapter?.getIsPlaying() == true) {
                mGenericListGridItemAdapter?.setIsPlaying(false)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
            }
        }
    }

    private fun updateUICurrentMediaItemIndex(index: Int) {
        if (isMatchingQueueMusicContent) {
            mGenericListGridItemAdapter?.setPlayingPosition(index)
            mGenericListGridItemAdapter?.setIsPlaying(
                mMediaPlayerDataViewModel.isPlaying.value ?: false
            )
            tryToScrollToCurrentItem(index)
        } else {
            if ((mGenericListGridItemAdapter?.getPlayingPosition() ?: -1) >= 0)
                mGenericListGridItemAdapter?.setPlayingPosition(-1)
        }
    }
    private fun tryToScrollToCurrentItem(position: Int) {
        if (position >= 0) {
            val tempCanScrollToPlayingSong: Boolean =
                mPlayingNowFragmentViewModel.canSmoothScrollViewpager.value ?: false
            if (!tempCanScrollToPlayingSong) return
            mPlayingNowFragmentViewModel.canSmoothScrollViewpager.value = false
            val tempFV: Int = (mLayoutManager?.findFirstVisibleItemPosition() ?: 0) - 1
            val tempLV: Int = mLayoutManager?.findLastVisibleItemPosition() ?: +1
            val tempVisibility: Boolean = position in tempFV..tempLV
            if (!tempVisibility) return
            context?.let { ctx ->
                val tempListSize: Int = mGenericListGridItemAdapter?.currentList?.size ?: 0
                val tempTargetPosition =
                    if (position + 2 <= tempListSize) position + 2 else tempListSize
                lifecycleScope.launch {
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

    private fun checkInteractions() {
        mDataBinding.recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mIsDraggingToScroll) {
                    if (dy < 0) {
                        Log.i(TAG, "Scrolling --> TOP")
                        mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLING_TOP
                    } else if (dy > 0) {
                        Log.i(TAG, "Scrolling --> BOTTOM")
                        mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLING_BOTTOM
                    }
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(TAG, "Scrolled to BOTTOM")
                        mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLED_BOTTOM
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(TAG, "Scrolled to TOP")
                        mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLED_TOP
                    }
                } else {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        Log.i(TAG, "Scrolled to BOTTOM")
                        if (mMainFragmentViewModel.scrollingState.value != ExploreContentForFragment.SCROLLED_BOTTOM)
                            mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLED_BOTTOM
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        Log.i(TAG, "Scrolled to TOP")
                        if (mMainFragmentViewModel.scrollingState.value != ExploreContentForFragment.SCROLLED_TOP)
                            mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLED_TOP
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
                        return AlbumItem.castDataItemToGeneric(ctx, dataItem)
                    }

                    override fun onRequestTextIndexForFastScroller(
                        dataItem: Any,
                        position: Int
                    ): String {
                        return AlbumItem.getStringIndexForFastScroller(dataItem)
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
                        if (mMainFragmentViewModel.selectMode.value == true) {
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
                            mMainFragmentViewModel.currentSelectablePage.value = TAG
                        }
                        mMainFragmentViewModel.selectMode.value = selectMode
                    }

                    override fun onRequestGetStringIndex(position: Int): String {
                        return AlbumItem.getStringIndexForSelection(
                            mGenericListGridItemAdapter?.currentList?.get(position)
                        )
                    }

                    override fun onSelectedListChange(selectedList: HashMap<Int, String>) {
                        mMainFragmentViewModel.selectedItems.value = selectedList
                    }
                },
                AlbumItem.diffCallback as DiffUtil.ItemCallback<Any>,
                mAlbumsFragmentViewModel.organizeListGrid.value ?: AlbumsFragmentViewModel.ORGANIZE_DEFAULT_VALUE,
                mIsSelectable = true,
                mHavePlaybackState = false,
                mIsImageFullCircle = false,
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
                mAlbumsFragmentViewModel.organizeListGrid.value
            )
            mLayoutManager =
                GridLayoutManager(ctx, initialSpanCount, GridLayoutManager.VERTICAL, false)
            mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val newSpanCount: Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(
                        ctx,
                        mAlbumsFragmentViewModel.organizeListGrid.value
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

            lifecycleScope.launch {
                mDataBinding.recyclerView.adapter = mConcatAdapter
                mDataBinding.recyclerView.layoutManager = mLayoutManager
            }
            val newSpanCount: Int = OrganizeItemBottomSheetDialogFragment.getSpanCount(
                ctx,
                mAlbumsFragmentViewModel.organizeListGrid.value
            )
            val updatedSpan: Int =
                if (mLayoutManager?.spanCount == newSpanCount) newSpanCount else mLayoutManager?.spanCount
                    ?: 1
            mItemDecoration = GridSpacingItemDecoration(updatedSpan)
            mItemDecoration?.let {
                lifecycleScope.launch {
                    mDataBinding.recyclerView.addItemDecoration(it)
                }
            }

            lifecycleScope.launch {
                mDataBinding.fastScroller.setSectionIndexer(mGenericListGridItemAdapter)
                mDataBinding.fastScroller.attachRecyclerView(mDataBinding.recyclerView)
                mDataBinding.fastScroller.setFastScrollListener(object :
                    FastScroller.FastScrollListener {
                    override fun onFastScrollStart(fastScroller: FastScroller) {
                        mMainFragmentViewModel.isFastScrolling.value = true
                    }

                    override fun onFastScrollStop(fastScroller: FastScroller) {
                        mMainFragmentViewModel.isFastScrolling.value = false
                        if (!mDataBinding.recyclerView.canScrollVertically(-1)) {
                            //On scrolled to top
                            mMainFragmentViewModel.scrollingState.value = -2
                        } else if (!mDataBinding.recyclerView.canScrollVertically(1)) {
                            //On scrolled to bottom
                            mMainFragmentViewModel.scrollingState.value = 2
                        }
                    }
                })
            }
        }
    }

    private fun openExploreContentFragment(position: Int) {
        val tempFragmentManager = activity?.supportFragmentManager ?: return
        val tempItem = mGenericListGridItemAdapter?.currentList?.get(position) ?: return

        context?.let { ctx ->
            val tempGeneric = AlbumItem.castDataItemToGeneric(ctx, tempItem, true) ?: return
            val tempStringUri = if (tempGeneric.mediaUri == Uri.EMPTY)
                "" else tempGeneric.mediaUri.toString()
            tempFragmentManager.commit {
                setReorderingAllowed(false)
                add(
                    R.id.main_fragment_container,
                    ExploreContentForFragment.newInstance(
                        PersistentStorage
                            .SortAndOrganize
                            .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM,
                        TAG,
                        AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
                        tempGeneric.name,
                        tempStringUri,
                        tempGeneric.hashedCovertArtSignature,
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
        mSortItemsDialog = SortContentExplorerBottomSheetDialogFragment.newInstance(
            mAlbumsFragmentViewModel,
            TAG,
            null
        )
        mSortItemsDialog?.show(childFragmentManager, SortContentExplorerBottomSheetDialogFragment.TAG)
    }

    private fun showOrganizeDialog() {
        mOrganizeItemsDialog = OrganizeItemBottomSheetDialogFragment.newInstance(
            mAlbumsFragmentViewModel,
            TAG,
            null
        )
        mOrganizeItemsDialog?.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }

    private fun playFirstSong() {
        //
    }

    private fun playSongOnShuffle() {
        if ((mGenericListGridItemAdapter?.currentList?.size ?: 0) <= 0) return

        //Load song and play
        mMediaControllerViewModel.mediaController?.shuffleModeEnabled = true
        mMediaControllerViewModel.mediaController?.play()
        updateRecyclerViewScrollingSate()
    }

    private fun updateRecyclerViewScrollingSate() {
        if (mDataBinding.recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
            mIsDraggingToScroll = false
        }
        mMainFragmentViewModel.scrollingState.value = ExploreContentForFragment.SCROLLING_TOP
    }


    private fun initViews() {
        mDataBinding.recyclerView.setHasFixedSize(true)
        mDataBinding.constraintFastScrollerContainer.let {
            InsetModifiers.updateBottomViewInsets(
                it
            )
        }
    }

    companion object {
        const val TAG = "AlbumsFragment"

        @JvmStatic
        fun newInstance() =
            AlbumsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}