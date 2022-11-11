package com.prosabdev.fluidmusic.ui.fragments.explore

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomMathComputations
import com.prosabdev.fluidmusic.utils.adapters.SelectableRecycleViewAdapter
import com.prosabdev.fluidmusic.viewmodels.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AllSongsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

    private var mEmptyBottomAdapter: HeadlinePlayShuffleAdapter? = null
    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: SongItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: ConstraintLayout? = null

    private var mSongList : ArrayList<SongItem> = ArrayList<SongItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALL_SONGS)
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_all_songs, container, false)

        initViews(view)
        setupRecyclerViewAdapter()
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    private fun observeLiveData() {
        if(mAllSongsFragmentViewModel.getIsLoadingInBackground().value == false && (mAllSongsFragmentViewModel.getDataRequestCounter().value ?: 0) <= 0){
            mAllSongsFragmentViewModel.requestLoadDataAsync(mActivity as Activity, 0, 1000)
        }
        mAllSongsFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner
        ) { onSongListRetrieved(it) }
        mAllSongsFragmentViewModel.getIsLoading().observe(mActivity as LifecycleOwner
        ) { onLoadingStateChanged(it) }
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
        ) { onCurrentPlayingSongChanged(it) }
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { onSelectionModeChanged(it) }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner
        ){ onTotalSelectedItemsChanged(it) }
        mMainFragmentViewModel.getToggleRange().observe(mActivity as LifecycleOwner
        ){ onToggleRangeChanged() }
    }

    private fun onToggleRangeChanged() {
        mSongItemAdapter?.selectableToggleSelectRange()
    }
    private fun onTotalSelectedItemsChanged(it: Int?) {
        if((it ?: 0) > 0 && (it ?: 0) >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            mSongItemAdapter?.selectableSelectAll()
        }else if((it ?: 0) <= 0 && (mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0) > 0){
            mSongItemAdapter?.selectableClearSelection()
        }
    }
    private fun onSelectionModeChanged(it: Boolean?) {
        mSongItemAdapter?.selectableSetSelectionMode(it?:false)
    }
    private fun onCurrentPlayingSongChanged(it: Int?) {
        if (mPlayerFragmentViewModel.getSourceOfQueueList().value == ConstantValues.EXPLORE_ALL_SONGS)
            mSongItemAdapter?.setCurrentPlayingSong(it ?: -1)
    }
    private fun onLoadingStateChanged(it: Boolean?) {
        if (it == false) {
            CustomAnimators.crossFadeDown(mLoadingContentProgress as View, true, 50)
        } else {
            CustomAnimators.crossFadeUp(mLoadingContentProgress as View, true, 100)
        }
    }
    private fun onSongListRetrieved(it: ArrayList<SongItem>?) {
        MainScope().launch {
            addSongsToAdapter(it)
        }
    }
    private suspend fun addSongsToAdapter(songList: ArrayList<SongItem>?) = coroutineScope{
        if (songList != null) {
            val startPosition: Int = mSongList.size
            val itemCount: Int = songList.size
            mSongList.addAll(startPosition, songList)
            mSongItemAdapter?.notifyItemRangeInserted(startPosition, itemCount)
        }
        mMainFragmentViewModel.setTotalCount(mSongList.size)
    }

    private fun checkInteractions() {
    }

    private fun setupRecyclerViewAdapter() {
        val spanCount = 1
        var touchHelper : ItemTouchHelper ? = null
        //Setup headline adapter
        val listHeadlines : ArrayList<Long> = ArrayList<Long>()
        listHeadlines.add(0)
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, R.layout.item_top_play_shuffle, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                updatePlayClickListener()
                updateCurrentPlayingSong(0)
            }
            override fun onShuffleButtonClicked() {
                updateShuffleClickListener()
                updateCurrentPlayingSong(CustomMathComputations.randomExcluded(mPlayerFragmentViewModel.getCurrentSong().value ?: 0, mSongList.size-1))
            }
            override fun onFilterButtonClicked() {
                Toast.makeText(mContext, "onFilterButtonClicked", Toast.LENGTH_SHORT).show()
            }
        })
        //Setup song adapter
        mSongItemAdapter = SongItemAdapter(
            mSongList,
            mContext!!,
            object : SongItemAdapter.OnItemClickListener{
                override fun onSongItemClicked(position: Int) {
                    if(mSongItemAdapter?.selectableGetSelectionMode() == true){
                        mSongItemAdapter?.selectableToggleSelection(position)
                        mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                    }else{
                        updateCurrentPlayingSong(position)
                    }
                }
                override fun onSongItemPlayClicked(position: Int) {
                    updateCurrentPlayingSong(position)
                }
                override fun onSongItemLongClicked(position: Int) {
                    if(mSongItemAdapter?.selectableGetSelectionMode() == true){
//                        mSongItemAdapter?.selectableToggleSelection(position)
//                        mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                    }else{
                        mSongItemAdapter?.selectableSetSelectionMode(true)
                        mMainFragmentViewModel.setSelectMode(mSongItemAdapter?.selectableGetSelectionMode() ?: false)
                        mSongItemAdapter?.selectableToggleSelection(position)
                        mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                    }
                }
                override fun onItemMovedTo(position: Int) {
                    scrollDrag(position)
                }

            },
            object : SelectableRecycleViewAdapter.OnSelectSelectableItemListener {
                override fun onTotalSelectedItemChange(totalSelected: Int) {
                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }
            },
            object : SongItemAdapter.OnTouchListener{
                override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
                    if (viewHolder != null) {
                        touchHelper?.startDrag(viewHolder)
                    }
                }

            }
        )
        //Setup empty bottom adapter
        mEmptyBottomAdapter = HeadlinePlayShuffleAdapter(
            listHeadlines,
            R.layout.item_custom_empty_bottom_space,
            object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                }
                override fun onShuffleButtonClicked() {
                }
                override fun onFilterButtonClicked() {
                }
            }
        )
        //Setup concat adapter
        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mHeadlineTopPlayShuffleAdapter!!)
        concatAdapter.addAdapter(mSongItemAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)
        mRecyclerView?.adapter = concatAdapter

        //Add Layout manager
        val layoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager

        //Setup Item touch helper callback for drag feature
        val callback : ItemTouchHelper.Callback = SongItemMoveCallback(mSongItemAdapter!!)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
    }

    private fun updatePlayClickListener() {
        mPlayerFragmentViewModel.setShuffle( PlaybackStateCompat.SHUFFLE_MODE_NONE)
        mPlayerFragmentViewModel.setRepeat( PlaybackStateCompat.REPEAT_MODE_NONE)
    }

    private fun updateShuffleClickListener() {
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
    }

    private fun updateCurrentPlayingSong(position: Int) {
        if(mPlayerFragmentViewModel.getSourceOfQueueList().value != ConstantValues.EXPLORE_ALL_SONGS){
            mPlayerFragmentViewModel.setSongList(mSongList)
            mPlayerFragmentViewModel.setSourceOfQueueList(ConstantValues.EXPLORE_ALL_SONGS)
        }
        mPlayerFragmentViewModel.setCurrentSong(position)
        mPlayerFragmentViewModel.setIsPlaying(true)
    }

    fun scrollDrag(position: Int) {
        Log.i(ConstantValues.TAG, "scrollDrag To $position")
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
        mLoadingContentProgress = view.findViewById<ConstraintLayout>(R.id.loading_content_progress)
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