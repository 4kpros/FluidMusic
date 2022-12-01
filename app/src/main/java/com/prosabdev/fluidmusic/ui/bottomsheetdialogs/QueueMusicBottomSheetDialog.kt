package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetQueueMusicBinding
import com.prosabdev.fluidmusic.models.SongItem
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
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner) {
            updatePlayingSongUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner) {
            updatePlaybackStateUI(it)
        }
    }
    private fun updatePlayingSongUI(songItem: SongItem?) {
        val songPosition: Int = songItem?.position ?: -1
        if((mQueueMusicItemAdapter?.getPlayingPosition() ?: -1) != songPosition)
            mQueueMusicItemAdapter?.setPlayingPosition(songPosition)
        tryToScrollOnCurrentItem(songPosition)
    }
    private fun tryToScrollOnCurrentItem(songPosition : Int) {
        if(songPosition <= 0) return
        //This is required to create every time new smooth scroller
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.apply {
            targetPosition = songPosition
        }
        mLayoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun updatePlaybackStateUI(isPlaying: Boolean) {
        if(mQueueMusicItemAdapter?.getIsPlaying() != isPlaying)
            mQueueMusicItemAdapter?.setIsPlaying(isPlaying)
    }

    private fun checkInteractions() {
        mBottomSheetQueueMusicBinding.dragHandle.setOnClickListener{
            dismiss()
        }
        mBottomSheetQueueMusicBinding.buttonClearQueueMusic.setOnClickListener{
            clearQueueMusicList()
        }
        mBottomSheetQueueMusicBinding.buttonAddToPlaylist.setOnClickListener{
            addAllQueueMusicToPlaylist()
        }
    }

    private fun addAllQueueMusicToPlaylist() {
        val queueMusicSize : Int = mQueueMusicItemAdapter?.currentList?.size ?: 0
        if(queueMusicSize <= 0) return
    }

    private fun clearQueueMusicList() {
        val queueMusicSize : Int = mQueueMusicItemAdapter?.currentList?.size ?: 0
        if(queueMusicSize <= 0) return
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

            val tempCurrentSong: SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value
                ?: return
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