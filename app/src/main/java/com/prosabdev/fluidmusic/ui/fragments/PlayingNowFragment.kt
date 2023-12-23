package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.utils.Animators
import com.prosabdev.common.utils.FormattersAndParsers
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.common.utils.MathComputations
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayingNowPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayingNowBinding
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerDataViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlayingNowFragment : Fragment() {

    //Data binding
    private lateinit var mDataBinding: FragmentPlayingNowBinding

    //View models
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayingNowFragmentViewModel by activityViewModels<PlayingNowFragmentViewModel> {
        InjectorUtils.provideNowPlayingFragmentViewModel(requireContext())
    }
    private val mMediaPlayerDataViewModel: MediaPlayerDataViewModel by activityViewModels()
    private val mMediaControllerViewModel by activityViewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mMediaPlayerDataViewModel.mediaEventsListener)
    }

    //Dialogs
    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog =
        QueueMusicBottomSheetDialog.newInstance()
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog =
        PlayerMoreFullBottomSheetDialog.newInstance()

    //Page adapter
    private var mPlayingNowPageAdapter: PlayingNowPageAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_playing_now, container, false)
        val view = mDataBinding.root

        //Load your UI content
        runBlocking {
            initViews()
            setupViewPagerAdapter()
            checkInteractions()
            observeLiveData()
        }

        return view
    }

    private fun observeLiveData() {
        mMediaPlayerDataViewModel.mediaItems.observe(viewLifecycleOwner) {
            updateUIEmptyList(it?.size ?: 0)
        }
        mMediaPlayerDataViewModel.isPlaying.observe(viewLifecycleOwner) {
            updateUIIsPlaying(it)
        }
        mMediaPlayerDataViewModel.currentMediaItem.observe(viewLifecycleOwner) {
            updateUIMediaInfo(it)
        }
        mMediaPlayerDataViewModel.positionMs.observe(viewLifecycleOwner) {
            updateUIPositionMs(it, mMediaPlayerDataViewModel.currentMediaItem.value?.clippingConfiguration?.endPositionMs ?: 0)
        }
        mMediaPlayerDataViewModel.repeatMode.observe(viewLifecycleOwner) {
            updateUIRepeat(it)
        }
        mMediaPlayerDataViewModel.shuffleModeEnabled.observe(viewLifecycleOwner) {
            updateUIShuffle(it)
        }
    }

    private fun updateUIRepeat(repeat: Int?) {
        when (repeat) {
            Player.REPEAT_MODE_ALL -> {
                lifecycleScope.launch {
                    mDataBinding.buttonRepeat.alpha = 1.0f
                    mDataBinding.buttonRepeat.icon =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.repeat) }
                }
            }

            Player.REPEAT_MODE_ONE -> {
                lifecycleScope.launch {
                    mDataBinding.buttonRepeat.alpha = 1.0f
                    mDataBinding.buttonRepeat.icon =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.repeat_one) }
                }
            }

            else -> {
                lifecycleScope.launch {
                    mDataBinding.buttonRepeat.alpha = 0.4f
                    mDataBinding.buttonRepeat.icon =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.repeat) }
                }
            }
        }
    }

    private fun updateUIShuffle(shuffle: Boolean?) {
        when (shuffle) {
            true -> {
                lifecycleScope.launch {
                    mDataBinding.buttonShuffle.alpha = 1.0f
                    mDataBinding.buttonShuffle.icon =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.shuffle) }
                }
            }
            else -> {
                lifecycleScope.launch {
                    mDataBinding.buttonShuffle.alpha = 0.4f
                    mDataBinding.buttonShuffle.icon =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.shuffle) }
                }
            }
        }
    }

    private fun updateUIPositionMs(positionMs: Long, totalDuration: Long? = 0) {
        lifecycleScope.launch {
            mDataBinding.slider.value =
                FormattersAndParsers.formatSongDurationToSliderProgress(
                    positionMs,
                    totalDuration ?: 0
                )
            mDataBinding.textPositionMin.text =
                FormattersAndParsers.formatSongDurationToString(positionMs)
        }
    }

    private fun updateUIMediaInfo(mediaItem: MediaItem?) {
        lifecycleScope.launch {
            mDataBinding.textTitle.text = mediaItem?.mediaMetadata?.title ?: context?.getString(R.string.unknown_title) ?: ""
            mDataBinding.textArtist.text = mediaItem?.mediaMetadata?.artist ?: context?.getString(R.string.unknown_artist) ?: ""
            mDataBinding.textDuration.text = FormattersAndParsers.formatSongDurationToString(mediaItem?.clippingConfiguration?.endPositionMs ?: 0)
        }
        updateUINextPrev(mMediaPlayerDataViewModel.currentMediaItemIndex.value, mMediaPlayerDataViewModel.mediaItems.value?.size)
        updateUIViewpager(mMediaPlayerDataViewModel.currentMediaItemIndex.value, mMediaPlayerDataViewModel.mediaItems.value?.size)
        updateUIBlurredBackground(mediaItem?.requestMetadata?.mediaUri, mediaItem?.mediaMetadata?.extras?.getInt(SongItem.IMAGE_SIGNATURE))
    }
    private fun updateUIBlurredBackground(imageUri: Uri?, signature: Int? = -1) {
        val request: ImageLoaders.ImageRequestItem =
            ImageLoaders.ImageRequestItem.newBlurInstance()
        request.uri = imageUri
        request.hashedCovertArtSignature = signature ?: -1
        request.imageView = mDataBinding.blurredImageview
        context?.let { ImageLoaders.startExploreContentImageLoaderJob(it, request) }
    }
    private fun updateUIViewpager(mediaIndex: Int?, itemsCount: Int?) {
        if ((mediaIndex == null) || (mediaIndex < 0) || (itemsCount == null) || (mediaIndex > itemsCount)) return

        val oldValue: Boolean =
            mPlayingNowFragmentViewModel.canSmoothScrollViewpager.value ?: false
        mPlayingNowFragmentViewModel.canSmoothScrollViewpager.value = false
        mDataBinding.viewPagerPlayer.setCurrentItem(mediaIndex, oldValue)
    }
    private fun updateUINextPrev(currentMediaIndex: Int? = 0, totalMediaItems: Int? = 0, ) {
        lifecycleScope.launch {
            mDataBinding.buttonSkipNext.isEnabled = (totalMediaItems ?: 0) > 0 && (currentMediaIndex ?: 0) < (totalMediaItems ?: 0) - 1
            mDataBinding.buttonSkipPrev.isEnabled = (totalMediaItems ?: 0) > 0 && (currentMediaIndex ?: 0) > 0
        }
    }

    private fun updateUIIsPlaying(isPlaying: Boolean?) {
        if (isPlaying == true) {
            lifecycleScope.launch {
                mDataBinding.buttonPlayPause.icon =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.pause_circle) }
            }
        } else {
            lifecycleScope.launch {
                mDataBinding.buttonPlayPause.icon =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.play_circle) }
            }
        }
    }

    private fun updateUIEmptyList(size: Int) {
        mDataBinding.let {
            if (size > 0) {
                it.linearRescanDeviceContainer.visibility = GONE
            } else {
                it.linearRescanDeviceContainer.visibility = VISIBLE
            }
        }
    }

    private fun checkInteractions() {
        //Listen scroll changes for viewpager
        var viewpagerScrollState = ViewPager2.SCROLL_STATE_IDLE
        mDataBinding.viewPagerPlayer.registerOnPageChangeCallback(object :
            OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                MathComputations.fadeWithPageOffset(
                    mDataBinding.blurredImageview,
                    positionOffset
                )
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    viewpagerScrollState = state
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (viewpagerScrollState == ViewPager2.SCROLL_STATE_DRAGGING) {
                    viewpagerScrollState = ViewPager2.SCROLL_STATE_IDLE
                    onViewpagerPageChanged(position)
                } else {
                    onViewpagerPageChangedTwice(position)
                }
            }
        })

        //Listen to slider position changes
        mDataBinding.slider.addOnSliderTouchListener(object : OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                onStopTrackingTouchSlider(slider.value)
            }
        })

        //Listen buttons clicks
        mDataBinding.buttonPlayPause.setOnClickListener {
            onPlayPauseButtonClicked()
        }
        mDataBinding.buttonSkipNext.setOnClickListener {
            onNextButtonClicked()
        }
        mDataBinding.buttonSkipPrev.setOnClickListener {
            onPrevButtonClicked()
        }

        mDataBinding.buttonShuffle.setOnClickListener {
            onShuffleButtonClicked()
        }
        mDataBinding.buttonRepeat.setOnClickListener {
            onRepeatButtonClicked()
        }

        mDataBinding.buttonMore.setOnClickListener {
            onMoreOptionsButtonClicked()
        }
        mDataBinding.buttonEqualizer.setOnClickListener {
            onEqualizerButtonClicked()
        }

        mDataBinding.buttonRescanDevice.setOnClickListener {
            openRescanDeviceButtonClicked()
        }
    }
    private fun showQueueMusicDialog() {
        if (!mQueueMusicBottomSheetDialog.isVisible) {
            mQueueMusicBottomSheetDialog.updateQueueMusicList(
                mPlayingNowPageAdapter?.currentList
            )
            mQueueMusicBottomSheetDialog.show(
                childFragmentManager,
                QueueMusicBottomSheetDialog.TAG
            )
        }
    }

    //Actions received from media player data view model
    private fun onViewpagerPageChanged(position: Int) {
        if ((mDataBinding.viewPagerPlayer.adapter?.itemCount ?: 0) < 1) return
        if (position == mMediaPlayerDataViewModel.currentMediaItemIndex.value) return

        mMediaControllerViewModel.mediaController?.seekTo(position, 0)
    }
    private fun onViewpagerPageChangedTwice(position: Int) {
        if ((mDataBinding.viewPagerPlayer.adapter?.itemCount ?: 0) < 1) return
        if (position == mMediaPlayerDataViewModel.currentMediaItemIndex.value) return

        mMediaPlayerDataViewModel.currentMediaItemIndex.value?.let {
            mDataBinding.viewPagerPlayer.setCurrentItem(
                it, false)
        }
    }
    private fun onStopTrackingTouchSlider(value: Float) {
        if ((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val totalDuration: Long =
            mMediaPlayerDataViewModel.currentMediaItem.value?.clippingConfiguration?.endPositionMs ?: 0
        val tempPositionMs: Long =
            FormattersAndParsers.formatSliderProgressToLongDuration(value, totalDuration)
        mMediaControllerViewModel.mediaController?.seekTo(tempPositionMs)
    }
    private fun openRescanDeviceButtonClicked() {
        startActivity(Intent(context, MediaScannerSettingsActivity::class.java).apply {})
    }
    @OptIn(UnstableApi::class)
    private fun onEqualizerButtonClicked() {
        startActivity(Intent(context, EqualizerActivity::class.java).apply {})
    }
    private fun onMoreOptionsButtonClicked() {
        if (!mPlayerMoreBottomSheetDialog.isVisible) {
            activity?.supportFragmentManager?.let {
                mPlayerMoreBottomSheetDialog.show(it, PlayerMoreFullBottomSheetDialog.TAG)
            }
        }
    }
    private fun onShuffleButtonClicked() {
        mMediaControllerViewModel.toggleShuffleModeEnabled(mMediaPlayerDataViewModel.shuffleModeEnabled.value ?: false)
    }
    private fun onRepeatButtonClicked() {
        mMediaControllerViewModel.toggleRepeatMode(mMediaPlayerDataViewModel.repeatMode.value ?: Player.REPEAT_MODE_OFF)
    }
    private fun onPrevButtonClicked() {
        mMediaControllerViewModel.mediaController?.seekToPreviousMediaItem()
    }
    private fun onNextButtonClicked() {
        mMediaControllerViewModel.mediaController?.seekToNextMediaItem()
    }
    private fun onPlayPauseButtonClicked() {
        mMediaControllerViewModel.togglePlayPause(mMediaPlayerDataViewModel.isPlaying.value ?: false)
    }

    private fun setupViewPagerAdapter() {
        context?.let { ctx ->
            lifecycleScope.launch {
                mPlayingNowPageAdapter = PlayingNowPageAdapter(
                    ctx,
                    object : PlayingNowPageAdapter.OnItemClickListener {
                        override fun onButtonLyricsClicked(position: Int) {
                        }

                        override fun onButtonFullscreenClicked(position: Int) {
                        }
                    })
            }
        }
        lifecycleScope.launch {
            mDataBinding.viewPagerPlayer.adapter = mPlayingNowPageAdapter
            mDataBinding.viewPagerPlayer.clipToPadding = false
            mDataBinding.viewPagerPlayer.clipChildren = false
            mDataBinding.viewPagerPlayer.offscreenPageLimit = 3
            mDataBinding.viewPagerPlayer.getChildAt(0)?.overScrollMode =
                View.OVER_SCROLL_NEVER
        }
        Animators.applyPageTransformer(mDataBinding.viewPagerPlayer)
    }

    private fun initViews() {
        //Select item for list view
        mDataBinding.textTitle.isSelected = true
        mDataBinding.textArtist.isSelected = true

        //Update insets
        InsetModifiers.updateTopViewInsets(mDataBinding.linearRescanDeviceContainer)
        InsetModifiers.updateTopViewInsets(mDataBinding.linearViewpager)

        InsetModifiers.updateBottomViewInsets(mDataBinding.dragHandleViewContainer)
        InsetModifiers.updateBottomViewInsets(mDataBinding.constraintBottomButtonsContainer)

        //Update dialogs data in order to show more quickly
        mPlayerMoreBottomSheetDialog.updateData(
            mPlayingNowFragmentViewModel,
            mMainFragmentViewModel,
            mDataBinding.root
        )
        mQueueMusicBottomSheetDialog.updatePlayerFragmentViewModel(
            mPlayingNowFragmentViewModel
        )
    }

    companion object {
        const val TAG = "PlayerFragment"

        @JvmStatic
        fun newInstance() =
            PlayingNowFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}