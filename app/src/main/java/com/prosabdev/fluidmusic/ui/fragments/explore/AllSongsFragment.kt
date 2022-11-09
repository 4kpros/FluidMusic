package com.prosabdev.fluidmusic.ui.fragments.explore

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.adapters.SelectableRecycleViewAdapter
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
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
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_all_songs, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViewAdapter()
        observeLiveData()
        checkInteractions()
    }

    private fun observeLiveData() {
        //Request load songs from database or media file scanner
        if(mAllSongsFragmentViewModel.getIsLoadingInBackground().value == false && (mAllSongsFragmentViewModel.getDataRequestCounter().value ?: 0) <= 0){
            mAllSongsFragmentViewModel.requestLoadDataAsync(mActivity as Activity)
        }
        //Listen when current page = this page, then try to load view data
        mMainExploreFragmentViewModel.getActivePage().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(page: Int?) {
                clearSelectedItemsPage()
            }

        })
        mAllSongsFragmentViewModel.getDataList().observe(mActivity as LifecycleOwner, object  : Observer<ArrayList<Any>>{
            override fun onChanged(songList: ArrayList<Any>?) {
                MainScope().launch {
                    addSongsToAdapter(songList as ArrayList<SongItem>)
                }
            }
        })
        mAllSongsFragmentViewModel.getIsLoading().observe(mActivity as LifecycleOwner, object  : Observer<Boolean>{
            override fun onChanged(isLoading: Boolean?) {
                if(isLoading == false){
                    CustomAnimators.crossFadeDown(mLoadingContentProgress as View, true, 50)
                }else{
                    CustomAnimators.crossFadeUp(mLoadingContentProgress as View, true, 100)
                }
            }
        })
    }

    private suspend fun updateSelectedItems(selectMode : Boolean, totalSelected: Int, totalCount : Int){
        if (selectMode != mSongItemAdapter?.selectableGetSelectionMode()){
            mSongItemAdapter?.selectableSetSelectionMode(selectMode)
        }

        if(totalCount > 0 && totalSelected == totalCount)
            mSongItemAdapter?.selectableSelectAll()
        else
            mSongItemAdapter?.selectableClearSelection()
    }

    private fun clearSelectedItemsPage() {
        mSongItemAdapter?.selectableSetSelectionMode(false)
        mSongItemAdapter?.selectableClearSelection()
        mMainExploreFragmentViewModel.setSelectMode(false)
        mMainExploreFragmentViewModel.setTotalSelected(0)
        mMainExploreFragmentViewModel.setTotalCount(mSongList.size)
    }

    private suspend fun addSongsToAdapter(songList: ArrayList<SongItem>?) = coroutineScope{
        if (songList != null) {
            val startPosition: Int = mSongList.size
            val itemCount: Int = songList.size
            mSongList.addAll(songList)
            mSongItemAdapter?.notifyItemRangeInserted(startPosition, itemCount)
        }
        mMainExploreFragmentViewModel.setTotalCount(mSongList.size)
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
                Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
                updateCurrentPlayingSong(0)
            }
            override fun onShuffleButtonClicked() {
                Toast.makeText(mContext, "onShuffleButtonClicked", Toast.LENGTH_SHORT).show()
            }

            override fun onFilterButtonClicked() {
                Toast.makeText(mContext, "onFilterButtonClicked", Toast.LENGTH_SHORT).show()
            }
        })
        //Setup song adapter
        mSongItemAdapter = SongItemAdapter(mSongList, mContext!!, object : SongItemAdapter.OnItemClickListener{
            override fun onSongItemClicked(position: Int) {
                if(mSongItemAdapter?.selectableGetSelectionMode() == true){
                    mSongItemAdapter?.selectableToggleSelection(position)
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

        }, object : SongItemAdapter.OnTouchListener{
            override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
                if (viewHolder != null) {
                    touchHelper?.startDrag(viewHolder)
                }
            }

        },
        object : SelectableRecycleViewAdapter.OnSelectSelectableItemListener {
            override fun onSelectionModeChange(
                selectMode: Boolean,
                totalSelected: Int,
                totalCount: Int
            ) {
                mMainExploreFragmentViewModel.setSelectMode(selectMode)
                mMainExploreFragmentViewModel.setTotalSelected(totalSelected)
                mMainExploreFragmentViewModel.setTotalCount(totalCount)
            }

            override fun onTotalSelectedItemChange(totalSelected: Int, totalCount: Int) {
                mMainExploreFragmentViewModel.setTotalSelected(totalSelected)
                mMainExploreFragmentViewModel.setTotalCount(totalCount)
            }
        })
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

    private fun updateCurrentPlayingSong(position: Int) {
        if(mPlayerFragmentViewModel.getSourceOfQueueList().value != ConstantValues.EXPLORE_ALL_SONGS){
            mPlayerFragmentViewModel.setQueueList(mSongList)
            mPlayerFragmentViewModel.setSourceOfQueueList(ConstantValues.EXPLORE_ALL_SONGS)
        }
        mPlayerFragmentViewModel.setCurrentSong(position)
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