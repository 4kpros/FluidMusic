package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAllSongsBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.OrganizeItemBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter.SortItemsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.utils.CenterSmoothScroller
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MathComputationsUtils
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

    private lateinit var mFragmentAllSongsBinding: FragmentAllSongsBinding

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mSongItemViewModel: SongItemViewModel by activityViewModels()

    private var mSortItemsBottomSheetDialogFragment: SortItemsBottomSheetDialogFragment = SortItemsBottomSheetDialogFragment.newInstance(ConstantValues.EXPLORE_ALL_SONGS)
    private var mOrganizeItemBottomSheetDialogFragment: OrganizeItemBottomSheetDialogFragment = OrganizeItemBottomSheetDialogFragment.newInstance(ConstantValues.EXPLORE_ALL_SONGS)

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: SongItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mIsDragging: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALL_SONGS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentAllSongsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_songs,container,false)
        val view = mFragmentAllSongsBinding.root

        initViews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            setupRecyclerViewAdapter()
            observeLiveData()
            checkInteractions()
        }
    }

    private fun observeLiveData() {
        mAllSongsFragmentViewModel.getAllSongs().observe(viewLifecycleOwner){
            addSongsToAdapter(it)
        }
        mAllSongsFragmentViewModel.getIsInverted().observe(viewLifecycleOwner){
            updateInvertedValueChangeUI(it)
        }
        mAllSongsFragmentViewModel.getOrganizeListGrid().observe(viewLifecycleOwner){
            organizeListGridUI(it)
        }

        //Listen to player changes
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner) {
            updatePlayingSongUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            updatePlaybackStateUI(it)
        }
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

    private fun updateOnScrollingStateUI(i: Int) {
        if(mMainFragmentViewModel.getActivePage().value == mPageIndex){
            if(i == 2)
                mEmptyBottomAdapter?.onSetScrollState(2)
        }
    }
    private fun onToggleRangeChanged() {
        mSongItemAdapter?.selectableToggleSelectRange(mLayoutManager)
    }
    private fun onTotalSelectedItemsChanged(it: Int?) {
        if((it ?: 0) > 0 && (it ?: 0) >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            mSongItemAdapter?.selectableSelectAll(mLayoutManager)
        }else if((it ?: 0) <= 0 && (mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0) > 0){
            mSongItemAdapter?.selectableClearSelection(mLayoutManager)
        }
    }
    private fun onSelectionModeChanged(it: Boolean?) {
        mSongItemAdapter?.selectableSetSelectionMode(it?:false)
        mHeadlineTopPlayShuffleAdapter?.onSelectModeValue(it ?: false)
    }
    private fun updatePlayingSongUI(songItem: SongItem?) {
        val songPosition: Int = songItem?.position ?: -1
        if(mPlayerFragmentViewModel.getQueueListSource().value == ConstantValues.EXPLORE_ALL_SONGS){
            mSongItemAdapter?.setPlayingPosition(songPosition)
        }else{
            if((mSongItemAdapter?.getPlayingPosition() ?: -1) >= 0)
                mSongItemAdapter?.setPlayingPosition(-1)
        }
        tryToScrollOnCurrentItem(songPosition)
    }
    private fun tryToScrollOnCurrentItem(songPosition: Int) {
        if(songPosition >= 0) {
            context?.let { ctx ->
                val tempListSize: Int = mSongItemAdapter?.currentList?.size ?: 0
                val tempTargetPosition = if(songPosition + 2 <= tempListSize) songPosition + 2 else tempListSize
                MainScope().launch {
                    mLayoutManager?.let {
                        it?.startSmoothScroll(
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
        if(mPlayerFragmentViewModel.getQueueListSource().value == ConstantValues.EXPLORE_ALL_SONGS){
            mSongItemAdapter?.setIsPlaying(isPlaying)
        }else{
            if(mSongItemAdapter?.getIsPlaying() == true)
                mSongItemAdapter?.setIsPlaying(false)
        }
    }
    private fun updateInvertedValueChangeUI(isInverted: Boolean?) {
        mLayoutManager?.reverseLayout = isInverted == true
    }
    private fun organizeListGridUI(organizeValue: Int?) {
        when (organizeValue ?: ConstantValues.ORGANIZE_LIST) {
            ConstantValues.ORGANIZE_SMALL_LIST -> {
                mLayoutManager?.spanCount = 1
            }
            ConstantValues.ORGANIZE_LIST -> {
                mLayoutManager?.spanCount = 1
            }
            ConstantValues.ORGANIZE_LARGE_LIST -> {
                mLayoutManager?.spanCount = 1
            }
            ConstantValues.ORGANIZE_SMALL_GRID -> {
                mLayoutManager?.spanCount = 4
            }
            ConstantValues.ORGANIZE_GRID -> {
                mLayoutManager?.spanCount = 3
            }
            ConstantValues.ORGANIZE_LARGE_GRID -> {
                mLayoutManager?.spanCount = 2
            }
        }
    }
    private fun addSongsToAdapter(songList: ArrayList<SongItem>?) {
        mSongItemAdapter?.submitList(songList as ArrayList<Any>?)
        mMainFragmentViewModel.setTotalCount(songList?.size ?: 0)
    }

    private fun checkInteractions() {
        mFragmentAllSongsBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(mIsDragging){
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
                        mIsDragging = false
                        println("The RecyclerView is SCROLL_STATE_IDLE")
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        mIsDragging = true
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
            val spanCount = 1

            //Setup headline adapter
            val listHeadlines : ArrayList<Int> = ArrayList<Int>()
            listHeadlines.add(0)
            mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                    playSongAtPosition(0)
                }
                override fun onShuffleButtonClicked() {
                    playSongsShuffle()
                }
                override fun onSortButtonClicked() {
                    showSortSongsDialog()
                }
                override fun onOrganizeButtonClicked() {
                    showOrganizeSongsDialog()
                }
            })

            //Setup song adapter
            mSongItemAdapter = SongItemAdapter(
                ctx,
                object : SongItemAdapter.OnItemClickListener{
                    override fun onSongItemClicked(position: Int) {
                        if(mSongItemAdapter?.selectableGetSelectionMode() == true){
                            mSongItemAdapter?.selectableToggleSelection(position, mLayoutManager)
                            mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                        }else{
                            playSongAtPosition(position)
                        }
                    }
                    override fun onSongItemLongClicked(position: Int) {
                        checkMultipleSelection(position)
                    }
                },
                object : SelectableItemListAdapter.OnSelectSelectableItemListener {
                    override fun onTotalSelectedItemChange(totalSelected: Int) {
                        mMainFragmentViewModel.setTotalSelected(totalSelected)
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
            mSongItemAdapter?.let {
                concatAdapter.addAdapter(it)
            }
            mEmptyBottomAdapter?.let {
                concatAdapter.addAdapter(it)
            }

            //Add Layout manager
            mLayoutManager = GridLayoutManager(ctx, spanCount, GridLayoutManager.VERTICAL, false)
            MainScope().launch {
                mFragmentAllSongsBinding.recyclerView.adapter = concatAdapter
                mFragmentAllSongsBinding.recyclerView.layoutManager = mLayoutManager
            }
        }
    }
    private fun checkMultipleSelection(position: Int) {
        if(mSongItemAdapter?.selectableGetSelectionMode() == true){
            mSongItemAdapter?.selectableToggleSelection(position, mLayoutManager)
            mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
        }else{
            mSongItemAdapter?.selectableSetSelectionMode(true, mLayoutManager)
            mMainFragmentViewModel.setSelectMode(mSongItemAdapter?.selectableGetSelectionMode() ?: false)
            mSongItemAdapter?.selectableToggleSelection(position, mLayoutManager)
            mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
        }
    }
    private fun showSortSongsDialog() {
        if(!mSortItemsBottomSheetDialogFragment.isVisible)
            mSortItemsBottomSheetDialogFragment.show(childFragmentManager, SortItemsBottomSheetDialogFragment.TAG)
    }
    private fun showOrganizeSongsDialog() {
        if(!mOrganizeItemBottomSheetDialogFragment.isVisible)
            mOrganizeItemBottomSheetDialogFragment.show(childFragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
    }
    private fun playSongsShuffle() {
        if((mSongItemAdapter?.currentList?.size ?: 0) <= 0) return
        MainScope().launch {
            withContext(Dispatchers.Default){
                val randomExcludedNumber: Int =
                    MathComputationsUtils.randomExcluded(
                        mSongItemAdapter?.getPlayingPosition() ?: -1,
                        (mSongItemAdapter?.currentList?.size ?: 0) -1
                    )
                playSongAtPosition(randomExcludedNumber, PlaybackStateCompat.REPEAT_MODE_NONE, PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
        }
    }
    private fun playSongAtPosition(position: Int, repeat: Int? = null, shuffle: Int? = null) {
        if((mSongItemAdapter?.currentList?.size ?: 0) <= 0) return
        mPlayerFragmentViewModel.setCurrentPlayingSong(getCurrentPlayingSongFromPosition(position))
        mPlayerFragmentViewModel.setIsPlaying(true)
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setRepeat(repeat ?: mPlayerFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
        mPlayerFragmentViewModel.setShuffle(shuffle ?: mPlayerFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
        if(mFragmentAllSongsBinding.recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING)
            mIsDragging = false
        mMainFragmentViewModel.setScrollingState(-1)
        mPlayerFragmentViewModel.setQueueListSource(ConstantValues.EXPLORE_ALL_SONGS)
    }
    private fun getCurrentPlayingSongFromPosition(position: Int): SongItem? {
        if (position < 0 || position >= (mSongItemAdapter?.currentList?.size ?: 0)) return null
        val tempSongItem: SongItem = mSongItemAdapter?.currentList?.get(position) as SongItem? ?: return null
        tempSongItem.position = position
        return tempSongItem
    }


    private fun initViews() {
    }

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int) =
            AllSongsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALL_SONGS, pageIndex)
                }
            }
    }
}