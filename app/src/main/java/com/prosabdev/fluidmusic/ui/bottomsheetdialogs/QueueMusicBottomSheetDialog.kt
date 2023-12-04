package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetQueueMusicBinding
import com.prosabdev.fluidmusic.ui.fragments.actions.PlaybackActions
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class QueueMusicBottomSheetDialog : GenericFullBottomSheetDialogFragment() {

    private var mBottomSheetQueueMusicBinding: BottomSheetQueueMusicBinding? = null

    private var mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel? = null

    private var mQueueMusicItemAdapter :QueueMusicItemListAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var mItemDecoration: GridSpacingItemDecoration? = null

    private var mSongList: List<com.prosabdev.common.models.songitem.SongItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBottomSheetQueueMusicBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_queue_music, container, false)
        val view = mBottomSheetQueueMusicBinding?.root

        initViews()
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    private fun observeLiveData() {
        mNowPlayingFragmentViewModel?.let { playerFragmentViewModel ->
            playerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner) {
                updatePlayingSongUI(it)
            }
            playerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner) {
                updatePlaybackStateUI(it)
            }
        }
    }
    private fun updatePlayingSongUI(songItem: com.prosabdev.common.models.songitem.SongItem?) {
        val songPosition: Int = songItem?.position ?: -1
        if((mQueueMusicItemAdapter?.getPlayingPosition() ?: -1) != songPosition)
            mQueueMusicItemAdapter?.setPlayingPosition(songPosition)
    }

    private fun updatePlaybackStateUI(isPlaying: Boolean) {
        if(mQueueMusicItemAdapter?.getIsPlaying() != isPlaying)
            mQueueMusicItemAdapter?.setIsPlaying(isPlaying)
    }

    private fun checkInteractions() {
        mBottomSheetQueueMusicBinding?.let { bottomSheetQueueMusicBinding ->
            bottomSheetQueueMusicBinding.buttonClearQueueMusic.setOnClickListener{
                clearQueueMusicList()
            }
            bottomSheetQueueMusicBinding.buttonAddToPlaylist.setOnClickListener{
                addAllQueueMusicToPlaylist()
            }
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
            mBottomSheetQueueMusicBinding?.let { bottomSheetQueueMusicBinding ->
                mQueueMusicItemAdapter = QueueMusicItemListAdapter(
                    ctx,
                    object : QueueMusicItemListAdapter.OnItemClickListener {
                        override fun onSongItemClicked(position: Int) {
                            onPlaySongAtPosition(position)
                        }
                    },
                    object : QueueMusicItemListAdapter.OnTouchListener {
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
                bottomSheetQueueMusicBinding.recyclerView.adapter = mQueueMusicItemAdapter
                bottomSheetQueueMusicBinding.recyclerView.layoutManager = mLayoutManager
                mQueueMusicItemAdapter?.submitList(mSongList)
                //
                mQueueMusicItemAdapter?.setIsPlaying(mNowPlayingFragmentViewModel?.getIsPlaying()?.value ?: false)
                mQueueMusicItemAdapter?.setPlayingPosition(mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value?.position ?: -1)
                mNowPlayingFragmentViewModel?.let { playerFragmentViewModel ->
                    val tempCurrentSong: com.prosabdev.common.models.songitem.SongItem =
                        playerFragmentViewModel.getCurrentPlayingSong().value
                            ?: return
                    mLayoutManager?.scrollToPosition(tempCurrentSong.position)
                }
                val spanCount = 1
                mItemDecoration = GridSpacingItemDecoration(spanCount)
                MainScope().launch {
                    mItemDecoration?.let {
                        bottomSheetQueueMusicBinding.recyclerView.addItemDecoration(it)
                    }
                    bottomSheetQueueMusicBinding.fastScroller.attachRecyclerView(bottomSheetQueueMusicBinding.recyclerView)
                    bottomSheetQueueMusicBinding.fastScroller.setFastScrollListener(object :
                        FastScroller.FastScrollListener {
                        override fun onFastScrollStart(fastScroller: FastScroller) {
                            println("FAST SCROLLING STARTED")
                        }

                        override fun onFastScrollStop(fastScroller: FastScroller) {
                            println("FAST SCROLLING STOPPED")
                        }

                    })
                }
            }
        }
    }

    private fun onPlaySongAtPosition(position: Int) {
        val tempSongItem: com.prosabdev.common.models.songitem.SongItem = mQueueMusicItemAdapter?.currentList?.get(position) as com.prosabdev.common.models.songitem.SongItem? ?: return
        tempSongItem.position = position
        PlaybackActions.playSongAtPositionFromQueueMusic(
            mNowPlayingFragmentViewModel,
            tempSongItem
        )
    }

    private fun initViews() {
        //
    }

    fun updatePlayerFragmentViewModel(
        nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    ){
        if(mNowPlayingFragmentViewModel == null){
            mNowPlayingFragmentViewModel = nowPlayingFragmentViewModel
        }
    }
    fun updateQueueMusicList(
        songList: List<com.prosabdev.common.models.songitem.SongItem>?
    ){
        mSongList = songList
        mQueueMusicItemAdapter?.submitList(songList)
    }

    companion object {
        const val TAG = "QueueMusicBSD"

        @JvmStatic
        fun newInstance() =
            QueueMusicBottomSheetDialog().apply {
            }
    }

}