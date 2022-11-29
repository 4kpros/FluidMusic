package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetQueueMusicBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MathComputationsUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel

class QueueMusicBottomSheetDialog : GenericFullBottomSheetDialogFragment() {

    private lateinit var mBottomSheetQueueMusicBinding: BottomSheetQueueMusicBinding

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel

    private var mQueueMusicItemAdapter :QueueMusicItemListAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mSongList: List<SongItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetQueueMusicBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_queue_music, container, false)
        val view = mBottomSheetQueueMusicBinding.root

        initViews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        checkInteractions()
        observeLiveData()
    }

    private fun observeLiveData() {
        //
    }

    private fun checkInteractions() {
        mBottomSheetQueueMusicBinding.dragHandle.setOnClickListener{
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        context?.let { ctx ->
            mQueueMusicItemAdapter = QueueMusicItemListAdapter(
                ctx,
                object : QueueMusicItemListAdapter.OnItemClickListener{
                    override fun onSongItemClicked(position: Int) {
                    }
                },
                object : QueueMusicItemListAdapter.OnTouchListener{
                    override fun onItemMovedTo(position: Int) {
                        //
                    }
                    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
                        //
                    }
                }
            )
            //Add Layout manager
            mLayoutManager = GridLayoutManager(ctx, 1, GridLayoutManager.VERTICAL, false)
            mBottomSheetQueueMusicBinding.recyclerView.adapter = mQueueMusicItemAdapter
            mBottomSheetQueueMusicBinding.recyclerView.layoutManager = mLayoutManager
            mQueueMusicItemAdapter?.submitList(mSongList)

            val tempCurrentSong: SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
            mLayoutManager?.scrollToPosition(tempCurrentSong.position)
        }
    }

    private fun initViews() {
        //
    }

    fun updateQueueMusicList(songList: List<SongItem>?){
        mSongList = songList
    }

    companion object {
        const val TAG = "QueueMusicBottomSheetDialog"

        @JvmStatic
        fun newInstance(playerFragmentViewModel : PlayerFragmentViewModel, songList: List<SongItem>?) =
            QueueMusicBottomSheetDialog().apply {
                mPlayerFragmentViewModel = playerFragmentViewModel
                updateQueueMusicList(songList)
            }
    }

}