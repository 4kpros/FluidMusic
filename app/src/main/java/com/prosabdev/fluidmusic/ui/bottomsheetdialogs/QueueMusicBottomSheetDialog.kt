package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetQueueMusicBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class QueueMusicBottomSheetDialog(private val mPlayerFragmentViewModel: PlayerFragmentViewModel) : GenericFullBottomSheetDialogFragment() {

    private lateinit var mBottomSheetQueueMusicBinding: BottomSheetQueueMusicBinding

    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null
    private var mQueueMusicItemAdapter :QueueMusicItemListAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetQueueMusicBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_queue_music, container, false)
        val view = mBottomSheetQueueMusicBinding.root

        initViews()
        MainScope().launch {
            setupRecyclerView()
            checkInteractions()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun updateSongList(songList : ArrayList<SongItem>){
        mQueueMusicItemAdapter?.submitList(songList as List<Any>)
    }

    fun setPlayingPosition(position : Int){
        mQueueMusicItemAdapter?.setCurrentPlayingSong(position)
    }

    private fun checkInteractions() {
        mBottomSheetQueueMusicBinding.dragHandle.setOnClickListener{
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val ctx : Context = context ?: return

        mQueueMusicItemAdapter = QueueMusicItemListAdapter(
            ctx,
            object : QueueMusicItemListAdapter.OnItemClickListener{
                override fun onSongItemClicked(position: Int) {
                    mPlayerFragmentViewModel.setIsPlaying(true)
                    val tempSong = mQueueMusicItemAdapter?.currentList?.get(position) as SongItem
                    val currentPlayingSongSP : CurrentPlayingSongSP = CurrentPlayingSongSP().apply {
                        this.id = tempSong.id
                        this.position = position.toLong()
                        this.uri = tempSong.uri
                        this.uriTreeId = tempSong.uriTreeId
                        this.title = tempSong.title
                        this.artist = tempSong.artist
                        this.duration = tempSong.duration
                        this.fileName = tempSong.fileName
                        this.typeMime = tempSong.typeMime
                        this.currentSeekDuration = 0
                    }
                    mPlayerFragmentViewModel.setCurrentSong(currentPlayingSongSP)
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
    }

    private fun initViews() {
        //
    }
    companion object {
        const val TAG = "QueueMusicBottomSheetDialog"
    }

}