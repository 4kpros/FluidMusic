package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.adapters.SelectableItemListAdapter

class QueueMusicDialog : BottomSheetDialogFragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mSongItemAdapter: SongItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mSongList : ArrayList<SongItem> = ArrayList<SongItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mContext = requireContext()
        mActivity = requireActivity()

        val view = layoutInflater.inflate(R.layout.dialog_queue_music, container, false)

        initViews(view)
        setupRecyclerViewAdapter()
        checkInteractions()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

        observeLiveData()
    }

    private fun observeLiveData() {
    }

    private fun updateCurrentPlayingSong(currentSong: Int?) {

    }
    private fun updateQueueList(songList: ArrayList<SongItem>?) {
//        if (songList != null) {
//            mSongList.clear()
//            val startPosition: Int = mSongList.size
//            val itemCount: Int = songList.size
//            mSongList.addAll(startPosition, songList)
//            Log.i(ConstantValues.TAG, "SIZE : ${mSongList.size}")
//            mPlayerPagerAdapter?.notifyItemRangeInserted(startPosition, itemCount)
//        }
//        mPlayerViewPager?.currentItem = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
    }

    private fun setupRecyclerViewAdapter() {
        val spanCount = 1
        var touchHelper : ItemTouchHelper? = null
        //Setup song adapter
        mSongItemAdapter = SongItemAdapter(
            mContext!!,
            object : SongItemAdapter.OnItemClickListener{
                override fun onSongItemClicked(position: Int) {
                    if(mSongItemAdapter?.selectableGetSelectionMode() == true){
                        mSongItemAdapter?.selectableToggleSelection(position, mLayoutManager)
//                        mMainFragmentViewModel.setTotalSelected(mSongItemAdapter?.selectableGetSelectedItemCount() ?: 0)
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
                override fun onItemMovedTo(position: Int) {
                    scrollDrag(position)
                }

            },
            object : SelectableItemListAdapter.OnSelectSelectableItemListener {
                override fun onTotalSelectedItemChange(totalSelected: Int) {
//                    mMainFragmentViewModel.setTotalSelected(totalSelected)
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
        mRecyclerView?.adapter = mSongItemAdapter
        //Add Layout manager
        mLayoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = mLayoutManager
        //Setup Item touch helper callback for drag feature
        val callback : ItemTouchHelper.Callback = SongItemMoveCallback(mSongItemAdapter!!)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
    }
    fun scrollDrag(position: Int) {
        Log.i(ConstantValues.TAG, "scrollDrag To $position")
    }
    private fun onLongPressedToItemSong(position: Int) {

    }
    private fun onPlayButton(position: Int) {
    }

    private fun checkInteractions() {
        //
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.queue_music_recycler_view)
    }
    companion object {
        const val TAG = "PlayerQueueMusicDialog"
    }
}