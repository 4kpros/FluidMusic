package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SleepTimerSP
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.common.utils.*
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayingNowPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayingNowBinding
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.activities.MainActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerStateViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PlayingNowFragment : Fragment() {

    private lateinit var mDataBinding: FragmentPlayingNowBinding

    private val mNowPlayingFragmentViewModel by activityViewModels<NowPlayingFragmentViewModel> {
        InjectorUtils.provideNowPlayingFragmentViewModel(requireContext())
    }
    private val mMediaPlayerStateViewModel: MediaPlayerStateViewModel by activityViewModels()
    private val mMediaControllerViewModel by viewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mMediaPlayerStateViewModel.mediaEventsListener)
    }
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mExploreContentsForFragmentViewModel: ExploreContentsForFragmentViewModel by activityViewModels()
    private val mAlbumArtistsFragmentViewModel: AlbumArtistsFragmentViewModel by activityViewModels()
    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mArtistsFragmentViewModel: ArtistsFragmentViewModel by activityViewModels()
    private val mComposersFragmentViewModel: ComposersFragmentViewModel by activityViewModels()
    private val mFoldersFragmentViewModel: FoldersFragmentViewModel by activityViewModels()
    private val mGenresFragmentViewModel: GenresFragmentViewModel by activityViewModels()
    private val mYearsFragmentViewModel: YearsFragmentViewModel by activityViewModels()

    private val mSongItemViewModel: SongItemViewModel by activityViewModels()

    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog =
        QueueMusicBottomSheetDialog.newInstance()
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog =
        PlayerMoreFullBottomSheetDialog.newInstance()

    private var mPlayingNowPageAdapter: PlayingNowPageAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
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

    private fun updateEmptyListUI(size: Int) {
        mDataBinding.let {
            if (size > 0) {
                it.linearRescanDeviceContainer.visibility = GONE
            } else {
                it.linearRescanDeviceContainer.visibility = VISIBLE
            }
        }
    }

    private fun observeLiveData() {
        //
    }

    private fun updateRepeatUI(repeat: Int?) {
        mDataBinding.let { dataBidingView ->
            context?.let {
                when (repeat) {
                    PlaybackState.REPEAT_MODE_ALL -> {
                        dataBidingView.buttonRepeat.alpha = 1.0f
                        dataBidingView.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat)
                    }

                    PlaybackState.REPEAT_MODE_ONE -> {
                        dataBidingView.buttonRepeat.alpha = 1.0f
                        dataBidingView.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat_one)
                    }

                    else -> {
                        dataBidingView.buttonRepeat.alpha = 0.4f
                        dataBidingView.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat)
                    }
                }
            }
        }
    }

    private fun updateShuffleUI(shuffle: Boolean?) {
        mDataBinding.let { dataBidingView ->
            context?.let {
                when (shuffle) {
                    true -> {
                        dataBidingView.buttonShuffle.alpha = 1.0f
                        dataBidingView.buttonShuffle.icon =
                            ContextCompat.getDrawable(it, R.drawable.shuffle)
                    }
                    else -> {
                        dataBidingView.buttonShuffle.alpha = 0.4f
                        dataBidingView.buttonShuffle.icon =
                            ContextCompat.getDrawable(it, R.drawable.shuffle)
                    }
                }
            }
        }
    }

    private fun updateProgressValueUI(positionMs: Long) {
        mDataBinding.let { dataBidingView ->
            context?.let {
                val totalDuration: Long =
                    mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                dataBidingView.slider.value =
                    FormattersAndParsers.formatSongDurationToSliderProgress(
                        positionMs,
                        totalDuration
                    )
                dataBidingView.textDurationCurrent.text =
                    FormattersAndParsers.formatSongDurationToString(positionMs)
            }
        }
    }

    private fun updatePlaybackStateUI(isPlaying: Boolean?) {
        mDataBinding.let { dataBidingView ->
            context?.let {
                if (isPlaying == true) {
                    dataBidingView.buttonPlayPause.icon =
                        ContextCompat.getDrawable(it, R.drawable.pause_circle)
                } else {
                    dataBidingView.buttonPlayPause.icon =
                        ContextCompat.getDrawable(it, R.drawable.play_circle)
                }
            }
        }
    }

    private fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        updateViewpagerUI(songItem)
        updateTextTitleSubtitleDurationUI(songItem)
        updateBlurredBackgroundUIFromUri(songItem)
    }

    private fun updateViewpagerUI(songItem: SongItem?) {
        if (songItem == null || (mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        mDataBinding.let { dataBidingView ->
            val tempOldPosition: Int = dataBidingView.viewPagerPlayer.currentItem
            val tempOldSongItem = mPlayingNowPageAdapter?.currentList?.get(tempOldPosition)
            if (
                tempOldPosition != songItem.position &&
                tempOldSongItem?.uri != songItem.uri &&
                tempOldSongItem?.id != songItem.id &&
                tempOldSongItem?.fileName != songItem.fileName
            ) {
                val smoothScroll: Boolean =
                    mNowPlayingFragmentViewModel.getCanScrollSmoothViewpager().value ?: false
                mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(false)
                dataBidingView.viewPagerPlayer.setCurrentItem(songItem.position, smoothScroll)
            }
        }
    }

    private fun updateBlurredBackgroundUIFromUri(songItem: SongItem?) {
        mDataBinding.let { dataBidingView ->
            context?.let { ctx ->
                val tempUri: Uri = Uri.parse(songItem?.uri ?: "")
                val imageRequestBlurred: ImageLoaders.ImageRequestItem =
                    ImageLoaders.ImageRequestItem.newBlurInstance()
                imageRequestBlurred.uri = tempUri
                imageRequestBlurred.hashedCovertArtSignature =
                    songItem?.hashedCovertArtSignature ?: -1
                imageRequestBlurred.imageView = dataBidingView.blurredImageview
                ImageLoaders.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
            }
        }
    }

    private fun updateTextTitleSubtitleDurationUI(songItem: SongItem?) {
        mDataBinding.let { dataBidingView ->
            context?.let { ctx ->
                dataBidingView.textTitle.text =
                    songItem?.title?.ifEmpty {
                        songItem.fileName ?: ctx.getString(R.string.unknown_title)
                    } ?: songItem?.fileName ?: ctx.getString(R.string.unknown_title)
                dataBidingView.textArtist.text =
                    songItem?.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) }
                        ?: ctx.getString(R.string.unknown_artist)
                dataBidingView.textDuration.text =
                    FormattersAndParsers.formatSongDurationToString(
                        songItem?.duration ?: 0
                    )
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mDataBinding.let { dataBidingView ->
            context?.let { ctx ->
                mPlayingNowPageAdapter = PlayerPageAdapter(
                    ctx,
                    object : PlayerPageAdapter.OnItemClickListener {
                        override fun onButtonLyricsClicked(position: Int) {
                        }

                        override fun onButtonFullscreenClicked(position: Int) {
                        }
                    })
                dataBidingView.viewPagerPlayer.adapter = mPlayingNowPageAdapter
                dataBidingView.viewPagerPlayer.clipToPadding = false
                dataBidingView.viewPagerPlayer.clipChildren = false
                dataBidingView.viewPagerPlayer.offscreenPageLimit = 3
                dataBidingView.viewPagerPlayer.getChildAt(0)?.overScrollMode =
                    View.OVER_SCROLL_NEVER
                Animators.applyPageTransformer(dataBidingView.viewPagerPlayer)
            }
        }
    }

    private fun checkInteractions() {
        mDataBinding.let { dataBidingView ->
            var viewpagerScrollState = ViewPager2.SCROLL_STATE_IDLE
            dataBidingView.viewPagerPlayer.registerOnPageChangeCallback(object :
                OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    MathComputations.fadeWithPageOffset(
                        dataBidingView.blurredImageview,
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
                        updateViewpagerAfterPageChangedUI()
                    }
                }
            })
            dataBidingView.slider.addOnSliderTouchListener(object : OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    updateOnStopTrackingTouch(slider.value)
                }
            })
            dataBidingView.buttonPlayPause.setOnClickListener {
                onPlayPauseButtonClicked()
            }
            dataBidingView.buttonSkipNext.setOnClickListener {
                onNextPageButtonClicked()
            }
            dataBidingView.buttonSkipPrev.setOnClickListener {
                onPrevPageButtonClicked()
            }
            dataBidingView.buttonShuffle.setOnClickListener {
                onShuffleButtonClicked()
            }
            dataBidingView.buttonRepeat.setOnClickListener {
                onRepeatButtonClicked()
            }
            dataBidingView.buttonMore.setOnClickListener {
                showMoreOptionsDialog()
            }
            dataBidingView.buttonEqualizer.setOnClickListener {
                openEqualizerActivity()
            }
            dataBidingView.buttonRescanDevice.setOnClickListener {
                openMediaScannerActivity()
            }
            dataBidingView.dragHandleViewContainer.setOnClickListener {
                showQueueMusicDialog()
            }
        }
    }

    private fun updateViewpagerAfterPageChangedUI() {
        val currentItem = mDataBinding.viewPagerPlayer.currentItem
        val tempCurrentSongItem = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value
        val tempSongItem = getCurrentPlayingSongFromPosition(currentItem)
        if (
            currentItem == tempCurrentSongItem?.position &&
            tempSongItem?.uri == tempCurrentSongItem?.uri &&
            tempSongItem.id == tempCurrentSongItem?.id &&
            tempSongItem.folderUri == tempCurrentSongItem?.folderUri
        ) {
            return
        }
        mDataBinding.viewPagerPlayer?.setCurrentItem(tempCurrentSongItem?.position ?: 0, false)
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

    private fun openMediaScannerActivity() {
        startActivity(Intent(context, MediaScannerSettingsActivity::class.java).apply {})
    }

    private fun openEqualizerActivity() {
        startActivity(Intent(context, EqualizerActivity::class.java).apply {

        })
    }

    private fun showMoreOptionsDialog() {
        if (!mPlayerMoreBottomSheetDialog.isVisible) {
            activity?.supportFragmentManager?.let {
                mPlayerMoreBottomSheetDialog.show(it, PlayerMoreFullBottomSheetDialog.TAG)
            }
        }
    }

    private fun onRepeatButtonClicked() {
        when (mNowPlayingFragmentViewModel.getRepeat().value ?: PlaybackState.REPEAT_MODE_NONE) {
            PlaybackState.REPEAT_MODE_NONE -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackState.REPEAT_MODE_ALL)
            }

            PlaybackState.REPEAT_MODE_ALL -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackState.REPEAT_MODE_ONE)
            }

            PlaybackState.REPEAT_MODE_ONE -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackState.REPEAT_MODE_NONE)
            }
        }
    }

    private fun onShuffleButtonClicked() {
        val tempShuffleValue: Int =
            mNowPlayingFragmentViewModel.getShuffle().value ?: PlaybackState.SHUFFLE_MODE_NONE
        if (tempShuffleValue == PlaybackState.SHUFFLE_MODE_NONE) {
            mNowPlayingFragmentViewModel.setShuffle(PlaybackState.SHUFFLE_MODE_ALL)
        } else if (tempShuffleValue == PlaybackState.SHUFFLE_MODE_ALL) {
            mNowPlayingFragmentViewModel.setShuffle(PlaybackState.SHUFFLE_MODE_NONE)
        }
    }

    private fun onPrevPageButtonClicked() {
        if ((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition: Int =
            mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem: SongItem = getCurrentPlayingSongFromPosition((tempPosition - 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun onNextPageButtonClicked() {
        if ((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition: Int =
            mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem: SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun getCurrentPlayingSongFromPosition(position: Int?): SongItem? {
        if (position == null) return null
        if (position < 0 || position >= (mPlayingNowPageAdapter?.currentList?.size
                ?: 0)
        ) return null
        val tempSongItem: SongItem =
            mPlayingNowPageAdapter?.currentList?.get(position) ?: return null
        tempSongItem.position = position
        return tempSongItem
    }

    private fun onPlayPauseButtonClicked() {
        mNowPlayingFragmentViewModel.toggleIsPlaying()
    }

    private fun updateOnStopTrackingTouch(value: Float) {
        if ((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val totalDuration: Long =
            mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
        val tempProgress: Long =
            FormattersAndParsers.formatSliderProgressToLongDuration(value, totalDuration)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(tempProgress)
    }

    private fun onViewpagerPageChanged(position: Int) {
        if ((mDataBinding.viewPagerPlayer?.adapter?.itemCount ?: 0) < 1) return
        val tempCurrentSongItem = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value
        val tempSongItem = getCurrentPlayingSongFromPosition(position)
        if (
            position == tempCurrentSongItem?.position &&
            tempSongItem?.uri == tempCurrentSongItem.uri &&
            tempSongItem.id == tempCurrentSongItem.id &&
            tempSongItem.folderUri == tempCurrentSongItem.folderUri
        ) {
            return
        }

        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun initViews() {
        mDataBinding.let { dataBidingView ->
            dataBidingView.textTitle.isSelected = true
            dataBidingView.textArtist.isSelected = true

            InsetModifiers.updateTopViewInsets(dataBidingView.linearRescanDeviceContainer)
            InsetModifiers.updateTopViewInsets(dataBidingView.linearViewpager)

            InsetModifiers.updateBottomViewInsets(dataBidingView.dragHandleViewContainer)
            InsetModifiers.updateBottomViewInsets(dataBidingView.constraintBottomButtonsContainer)

            mPlayerMoreBottomSheetDialog.updateData(
                mNowPlayingFragmentViewModel,
                mMainFragmentViewModel,
                dataBidingView.root
            )
            mQueueMusicBottomSheetDialog.updatePlayerFragmentViewModel(
                mNowPlayingFragmentViewModel
            )
        }
    }

    companion object {
        const val TAG = "PlayerFragment"

        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = "playOrder"
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance() =
            PlayingNowFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}