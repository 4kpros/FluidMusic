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
        // Inflate the layout for this fragment
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
        //Request load songs from database or media file scanner
        if(mAllSongsFragmentViewModel.getIsLoadingInBackground().value == false && (mAllSongsFragmentViewModel.getDataRequestCounter().value ?: 0) <= 0){
            mAllSongsFragmentViewModel.requestLoadDataAsync(mActivity as Activity, 0, 1000)
        }
        mAllSongsFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner
        ) {
            MainScope().launch {
                addSongsToAdapter(it)
            }
        }
        mAllSongsFragmentViewModel.getIsLoading().observe(mActivity as LifecycleOwner
        ) {
            if (it == false) {
                CustomAnimators.crossFadeDown(mLoadingContentProgress as View, true, 50)
            } else {
                CustomAnimators.crossFadeUp(mLoadingContentProgress as View, true, 100)
            }
        }
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
        ) {
            if (mPlayerFragmentViewModel.getSourceOfQueueList().value == ConstantValues.EXPLORE_ALL_SONGS)
                mSongItemAdapter?.setCurrentPlayingSong(it ?: -1)
        }
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { selectMode -> toggleSelectModeChangeWithoutNotifying(selectMode) }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner){
            updateTotalSelectedItems(it)
        }
        mMainFragmentViewModel.getIsAllSelected().observe(mActivity as LifecycleOwner){
            updateAllSelectionState(it)
        }
        mMainFragmentViewModel.getIsRangeSelected().observe(mActivity as LifecycleOwner){
            updateRangeSelected(it)
        }
    }

    private fun updateAllSelectionState(it: Boolean?) {
        if(it == true)
            mSongItemAdapter?.selectableSelectAll()
        else
            mSongItemAdapter?.selectableClearSelection()
    }

    private fun updateTotalSelectedItems(i: Int) {
        if(i > 0 && i >= (mMainFragmentViewModel.getTotalCount().value ?: 0))
            mSongItemAdapter?.selectableSelectAll()
    }

    private fun updateRangeSelected(it: Boolean?) {
        val tempStartRange : Int = mMainFragmentViewModel.getMinimumSelectedIndex().value ?: -1
        val tempEndRange : Int = mMainFragmentViewModel.getMaximumSelectedIndex().value ?: -1
        if(it == true){
            mSongItemAdapter?.selectableSelectRange(tempStartRange, tempEndRange)
        }else{
            mSongItemAdapter?.selectableClearRange(tempStartRange, tempEndRange)
        }
    }

    private fun toggleSelectModeChangeWithoutNotifying(selectMode: Boolean?) {
        if(selectMode == true){
            mSongItemAdapter?.selectableSetSelectionMode(true, notifyListener = false)
        }else{
            mSongItemAdapter?.selectableSetSelectionMode(false, notifyListener = false)
            mSongItemAdapter?.selectableClearSelection(false)
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
                    if(mSongItemAdapter?.selectableGetSelectionMode() == false){
                        mSongItemAdapter?.selectableSetSelectionMode(true)
                        mSongItemAdapter?.selectableToggleSelection(position)
                    }
                }

                override fun onItemMovedTo(position: Int) {
                    scrollDrag(position)
                }

            },
            object : SelectableRecycleViewAdapter.OnSelectSelectableItemListener {
                override fun onSelectionModeChange(
                    selectMode: Boolean,
                    totalSelected: Int
                ) {
                    mMainFragmentViewModel.setSelectMode(selectMode)
                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }

                override fun onTotalSelectedItemChange(totalSelected: Int) {
//                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }

                override fun onTotalSelectedItemChangeFromRange(totalSelected: Int) {
                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }

                override fun onTotalSelectedItemChangeFromToggle(totalSelected: Int) {
                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }

                override fun onMinSelectedItemChange(minSelected: Int) {
                    Log.i(ConstantValues.TAG, "MINIMUM CHANGED TO : $minSelected")
                    mMainFragmentViewModel.setMinimumSelectedIndex(minSelected)
                }

                override fun onMaxSelectedItemChange(maxSelected: Int) {
                    Log.i(ConstantValues.TAG, "MAXIMUM CHANGED TO : $maxSelected")
                    mMainFragmentViewModel.setMaximumSelectedIndex(maxSelected)
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
                    Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
                }
                override fun onShuffleButtonClicked() {
                    Toast.makeText(mContext, "onShuffleButtonClicked", Toast.LENGTH_SHORT).show()
                }

                override fun onFilterButtonClicked() {
                    Toast.makeText(mContext, "onFilterButtonClicked", Toast.LENGTH_SHORT).show()
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

        //Setup Item move callback
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