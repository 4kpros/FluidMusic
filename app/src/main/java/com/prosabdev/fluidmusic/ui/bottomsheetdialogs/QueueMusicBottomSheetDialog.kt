package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.GridSpacingItemDecoration
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetQueueMusicBinding
import com.prosabdev.fluidmusic.ui.fragments.communication.FragmentsCommunication
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerDataViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class QueueMusicBottomSheetDialog : GenericFullBottomSheetDialogFragment() {

    //Data binding
    private lateinit var mDataBinding: BottomSheetQueueMusicBinding

    //View models
    private val mMediaPlayerDataViewModel: MediaPlayerDataViewModel by activityViewModels()
    private val mMediaControllerViewModel by activityViewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mMediaPlayerDataViewModel.mediaEventsListener)
    }

    //Variables
    private var mQueueMusicItemAdapter :QueueMusicItemListAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var mItemDecoration: GridSpacingItemDecoration? = null

    private var mMediaItems: List<MediaItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_queue_music, container, false)
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        setupRecyclerView()
        checkInteractions()
        observeLiveData()

        return view
    }

    private fun observeLiveData() {
        //Listen to player changes
        mMediaPlayerDataViewModel.currentMediaItemIndex.observe(viewLifecycleOwner) {
            updateUICurrentMediaItemIndex(it)
        }
        mMediaPlayerDataViewModel.isPlaying.observe(viewLifecycleOwner) {
            updateUIIsPlaying(it)
        }
    }
    private fun updateUICurrentMediaItemIndex(position: Int) {
        if((mQueueMusicItemAdapter?.getPlayingPosition() ?: -1) != position)
            mQueueMusicItemAdapter?.setPlayingPosition(position)
    }

    private fun updateUIIsPlaying(isPlaying: Boolean) {
        if(mQueueMusicItemAdapter?.getIsPlaying() != isPlaying)
            mQueueMusicItemAdapter?.setIsPlaying(isPlaying)
    }

    private fun checkInteractions() {
        mDataBinding.buttonClearQueueMusic.setOnClickListener{
            clearQueueMusicList()
        }
        mDataBinding.buttonAddToPlaylist.setOnClickListener{
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
            mDataBinding.recyclerView.adapter = mQueueMusicItemAdapter
            mDataBinding.recyclerView.layoutManager = mLayoutManager
            mQueueMusicItemAdapter?.submitList(mMediaItems)
            //
            mQueueMusicItemAdapter?.setIsPlaying(mMediaPlayerDataViewModel.isPlaying.value ?: false)
            mQueueMusicItemAdapter?.setPlayingPosition(mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1)
            mLayoutManager?.scrollToPosition(mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: -1)
            val spanCount = 1
            mItemDecoration = GridSpacingItemDecoration(spanCount)
            MainScope().launch {
                mItemDecoration?.let {
                    mDataBinding.recyclerView.addItemDecoration(it)
                }
                mDataBinding.fastScroller.attachRecyclerView(mDataBinding.recyclerView)
                mDataBinding.fastScroller.setFastScrollListener(object :
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

    private fun onPlaySongAtPosition(position: Int) {
        val tempSongItem: com.prosabdev.common.models.songitem.SongItem = mQueueMusicItemAdapter?.currentList?.get(position) as com.prosabdev.common.models.songitem.SongItem? ?: return
        tempSongItem.position = position
//        FragmentsCommunication.playSongAtPositionFromQueueMusic(
//            mMediaPlayerDataViewModel,
//            tempSongItem
//        )
    }

    private fun initViews() {
        //
    }

    fun updatePlayerFragmentViewModel(
        playingNowFragmentViewModel: PlayingNowFragmentViewModel
    ){
//        if(mMediaPlayerDataViewModel == null){
//            mMediaPlayerDataViewModel = playingNowFragmentViewModel
//        }
    }
    fun updateQueueMusicList(
        mediaItems: List<MediaItem>?
    ){
        mMediaItems = mediaItems
        mQueueMusicItemAdapter?.submitList(mMediaItems)
    }

    companion object {
        const val TAG = "QueueMusicBSD"

        @JvmStatic
        fun newInstance() =
            QueueMusicBottomSheetDialog().apply {
            }
    }

}