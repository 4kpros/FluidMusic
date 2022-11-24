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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentAllSongsBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.SortOrganizeItemsBottomSheetDialog
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomMathComputations
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class AllSongsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentAllSongsBinding: FragmentAllSongsBinding

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private lateinit var mSongItemViewModel: SongItemViewModel

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: SongItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onDestroyView() {
        mSongItemViewModel
        super.onDestroyView()
    }

    private suspend fun observeLiveData() {
        mSongItemViewModel.getAll()?.observe(this as LifecycleOwner){
            MainScope().launch {
                addSongsToAdapter(it as ArrayList<SongItem>)
            }
        }
        mPlayerFragmentViewModel.getCurrentSong().observe(this as LifecycleOwner
        ) { updateSelectedPlayingSong(it?.position?.toInt() ?: 0) }
        mMainFragmentViewModel.getSelectMode().observe(this as LifecycleOwner
        ) { onSelectionModeChanged(it) }
        mMainFragmentViewModel.getTotalSelected().observe(this as LifecycleOwner
        ){ onTotalSelectedItemsChanged(it) }
        mMainFragmentViewModel.getToggleRange().observe(this as LifecycleOwner
        ){ onToggleRangeChanged() }
        mMainFragmentViewModel.getScrollingState().observe(this as LifecycleOwner
        ){ updateOnScrollingStateUI(it) }
    }

    private fun updateSelectedPlayingSong(i: Int) {
        //
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
    private fun onCurrentPlayingSongChanged(it: Int?) {
        if (mPlayerFragmentViewModel.getSourceOfQueueList().value == ConstantValues.EXPLORE_ALL_SONGS)
            mSongItemAdapter?.setCurrentPlayingSong(it ?: -1)
    }
    private suspend fun addSongsToAdapter(songList: ArrayList<SongItem>?) = coroutineScope{
        mSongItemAdapter?.submitList(songList as ArrayList<Any>)
        mMainFragmentViewModel.setTotalCount(songList?.size ?: 0)
    }

    private fun checkInteractions() {
        mFragmentAllSongsBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
            }
        })
    }

    private fun setupRecyclerViewAdapter() = lifecycleScope.launch(context = Dispatchers.Default){
        val ctx : Context = this@AllSongsFragment.context ?: return@launch
        val spanCount = 1

        //Setup headline adapter
        val listHeadlines : ArrayList<Int> = ArrayList<Int>()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                onPlayButton(0)
            }
            override fun onShuffleButtonClicked() {
                onShuffleButton()
            }
            override fun onFilterButtonClicked() {
                onShowFilterDialog()
            }
        })

        //Setup song adapter
        mSongItemAdapter?.submitList(null)
        mSongItemAdapter = SongItemAdapter(
            ctx,
            object : SongItemAdapter.OnItemClickListener{
                override fun onSongItemClicked(position: Int) {
                    if(mSongItemAdapter?.selectableGetSelectionMode() == true){
                        mSongItemAdapter?.selectableToggleSelection(position, mLayoutManager)
                        mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                    }else{
                        onPlayButton(position)
                    }
                }
                override fun onSongItemPlayClicked(position: Int) {
                    updateCurrentPlayingSong(position)
                }
                override fun onSongItemLongClicked(position: Int) {
                    onLongPressedToItemSong(position)
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
        concatAdapter.addAdapter(mHeadlineTopPlayShuffleAdapter!!)
        concatAdapter.addAdapter(mSongItemAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)

        //Add Layout manager
        mLayoutManager = GridLayoutManager(ctx, spanCount, GridLayoutManager.VERTICAL, false)
        MainScope().launch {
            mFragmentAllSongsBinding.recyclerView.adapter = concatAdapter
            mFragmentAllSongsBinding.recyclerView.layoutManager = mLayoutManager
        }
    }

    private fun onShowFilterDialog() {
        SortOrganizeItemsBottomSheetDialog().show(childFragmentManager, SortOrganizeItemsBottomSheetDialog.TAG)
    }

    private fun onLongPressedToItemSong(position: Int) {
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
    private fun onPlayButton(position: Int) {
        mPlayerFragmentViewModel.setShuffle( PlaybackStateCompat.SHUFFLE_MODE_NONE)
        mPlayerFragmentViewModel.setRepeat( PlaybackStateCompat.REPEAT_MODE_NONE)
        updateCurrentPlayingSong(position)
        mMainFragmentViewModel.setScrollingState(-1)
    }
    private fun onShuffleButton() {
        mPlayerFragmentViewModel.setRepeat( PlaybackStateCompat.REPEAT_MODE_NONE)
        when (mPlayerFragmentViewModel.getShuffle().value) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_ALL)
            }
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
            else -> {
                mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
        }
        updateCurrentPlayingSong(
            CustomMathComputations.randomExcluded(
                mSongItemAdapter?.getCurrentPlayingSong() ?: -1,
            (
                    mSongItemAdapter?.currentList?.size ?: 0) -1
            )
        )
        mMainFragmentViewModel.setScrollingState(-1)
    }
    private fun updateCurrentPlayingSong(position: Int) {
        if(position >= 0 && position < (mSongItemAdapter?.currentList?.size ?: 0)) {
            if (mPlayerFragmentViewModel.getSourceOfQueueList().value != ConstantValues.EXPLORE_ALL_SONGS) {
                mPlayerFragmentViewModel.setSourceOfQueueList(ConstantValues.EXPLORE_ALL_SONGS)
            }
            castAndSetCurrentIem(mSongItemAdapter?.currentList?.get(position) as SongItem?)
        }
    }

    private fun castAndSetCurrentIem(songItem: SongItem?) {
        val currentPlayingSong = CurrentPlayingSongSP()
        if(songItem == null){
            return
        }
        currentPlayingSong.id = songItem.id
        currentPlayingSong.position = mSongItemAdapter?.getCurrentPlayingSong()?.toLong() ?: 0
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.uri = songItem.uri
        currentPlayingSong.artist = songItem.artist
        currentPlayingSong.duration = songItem.duration
        currentPlayingSong.title = songItem.title
        currentPlayingSong.typeMime = songItem.typeMime
        currentPlayingSong.uriTreeId = songItem.uriTreeId
        mPlayerFragmentViewModel.setCurrentSong(currentPlayingSong)
        mPlayerFragmentViewModel.setIsPlaying(true)
    }

    private fun initViews() {
        mSongItemViewModel = ModelsViewModelFactory(this.requireContext()).create(SongItemViewModel::class.java)
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