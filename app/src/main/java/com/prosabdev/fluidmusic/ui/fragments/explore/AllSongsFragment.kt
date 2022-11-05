package com.prosabdev.fluidmusic.ui.fragments.explore

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel
import kotlinx.coroutines.*

class AllSongsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: SongItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: LinearProgressIndicator? = null

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

        runBlocking {
            launch {
            }
        }
        return view
    }

    private suspend fun observeLiveData() {
        //Hide all views
        CustomAnimators.crossFadeDown(mRecyclerView as View)
        CustomAnimators.crossFadeDown(mLoadingContentProgress as View)
        //Request load songs from database or media file scanner
        mAllSongsFragmentViewModel.requestLoadAsyncSongs(mActivity as Activity)
        //Listen when current page = this page, then try to load view data
        mMainExploreFragmentViewModel.getActivePage().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(page: Int?) {
                MainScope().launch {
                    launch {
                        clearSelectedItemsPage()
                    }
                }
            }

        })
        mAllSongsFragmentViewModel.getSongs().observe(mActivity as LifecycleOwner, object  : Observer<ArrayList<SongItem>>{
            override fun onChanged(songList: ArrayList<SongItem>?) {
                addSongsToAdapter(songList)
            }
        })
        mAllSongsFragmentViewModel.getIsLoading().observe(mActivity as LifecycleOwner, object  : Observer<Boolean>{
            override fun onChanged(isLoading: Boolean?) {
                Log.i(ConstantValues.TAG, "Loading : $isLoading")
                if(isLoading == false){
                    CustomAnimators.hideLoadingView(mRecyclerView as View, mLoadingContentProgress as View, true)
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

    private suspend fun clearSelectedItemsPage() = coroutineScope{
        mSongItemAdapter?.selectableSetSelectionMode(false)
        mSongItemAdapter?.selectableClearSelection()
        mMainExploreFragmentViewModel.setSelectMode(false)
        mMainExploreFragmentViewModel.setTotalSelected(0)
        mMainExploreFragmentViewModel.setTotalCount(0)
    }

    private fun addSongsToAdapter(songList: ArrayList<SongItem>?) {
        if (songList != null) {
            val startPosition = mSongList.size -1
            mSongList.addAll(songList)
            mSongItemAdapter?.notifyItemRangeInserted(startPosition, mSongList.size-1)
        }
        mMainExploreFragmentViewModel.setTotalCount(mSongList.size)
    }

    private fun checkInteractions() {

    }

    private fun setupRecyclerViewAdapter() {
        val spanCount = 1

        val listHeadlines : ArrayList<Long> = ArrayList<Long>()
        listHeadlines.add(1)
        var touchHelper : ItemTouchHelper ? = null
        //Setup headline adapter
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(mContext, "onSongItemClicked", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSongItemPlayClicked(position: Int) {
                Toast.makeText(mContext, "onSongItemPlayClicked", Toast.LENGTH_SHORT).show()
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

        //Setup adapter
        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mHeadlineTopPlayShuffleAdapter!!)
        concatAdapter.addAdapter(mSongItemAdapter!!)
        mRecyclerView?.adapter = concatAdapter

        //Add Layout manager
        val layoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager

        //Setup Item move callback
        val callback : ItemTouchHelper.Callback = SongItemMoveCallback(mSongItemAdapter!!)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
    }
    fun scrollDrag(position: Int) {
        Log.i(ConstantValues.TAG, "scrollDrag To $position")
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.content_recycler_view)
        mLoadingContentProgress = view.findViewById<LinearProgressIndicator>(R.id.loading_content_progress)

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