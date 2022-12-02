package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.QueueMusicItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistSongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment() {

    private var mFragmentPlayerBinding: FragmentPlayerBinding? = null

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mQueueMusicItemViewModel: QueueMusicItemViewModel by activityViewModels()
    private val mPlaylistItemViewModel: PlaylistItemViewModel by activityViewModels()
    private val mPlaylistSongItemViewModel: PlaylistSongItemViewModel by activityViewModels()

    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog? = null
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog? = null

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentPlayerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_player,container,false)
        val view = mFragmentPlayerBinding?.root

        initViews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        setupViewPagerAdapter()
        observeLiveData()
    }

    private fun updateEmptyListUI(size: Int) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                if(size > 0){
                    fragmentPlayerBinding.linearRescanDeviceContainer.visibility = GONE
                }else{
                    fragmentPlayerBinding.linearRescanDeviceContainer.visibility = VISIBLE
                }
            }
        }
    }
    private suspend fun updateBlurredBackgroundUI(position: Int) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
                return
            withContext(Dispatchers.Default) {
                val ctx: Context = this@PlayerFragment.context ?: return@withContext
                val tempSongItem: SongItem? = mPlayerPagerAdapter?.currentList?.get(position)
                val tempUri = Uri.parse(tempSongItem?.uri ?: "")
                ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                    ctx,
                    fragmentPlayerBinding.blurredImageview,
                    tempUri,
                    tempSongItem?.hashedCovertArtSignature ?: -1,
                    100
                )
            }
        }
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getQueueListSource().observe(viewLifecycleOwner){
            val queueListSource: String = it ?: ConstantValues.EXPLORE_ALL_SONGS
            if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
                mAllSongsFragmentViewModel.getAll().observe(viewLifecycleOwner){ songList ->
                    updateEmptyListUI(songList?.size ?: 0)
                    mPlayerPagerAdapter?.submitList(songList as ArrayList<SongItem>?)
                    mQueueMusicBottomSheetDialog?.updateQueueMusicList(songList as ArrayList<SongItem>?)
                }
            }
        }
        mPlayerFragmentViewModel.getSourceOfQueueListValue().observe(viewLifecycleOwner){
            //
        }
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner){
            updateCurrentPlayingSongUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner){
            updatePlaybackStateUI(it)
        }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(viewLifecycleOwner){
            updateProgressValueUI(it)
        }
        mPlayerFragmentViewModel.getShuffle().observe(viewLifecycleOwner){
            updateShuffleUI(it)
        }
        mPlayerFragmentViewModel.getRepeat().observe(viewLifecycleOwner){
            updateRepeatUI(it)
        }
        mPlayerFragmentViewModel.getSkipNextTrackCounter().observe(viewLifecycleOwner){
            onSkipToNextTrack(it)
        }
        mPlayerFragmentViewModel.getSkipPrevTrackCounter().observe(viewLifecycleOwner){
            onSkipToPrevTrack(it)
        }
        mPlayerFragmentViewModel.getSleepTimer().observe(viewLifecycleOwner){
            //
        }
        mPlayerFragmentViewModel.getSleepTimerStateStarted().observe(viewLifecycleOwner){
            //
        }
    }
    private fun onSkipToPrevTrack(skipCounter: Int?) {
        if((skipCounter ?: 0) <= 0) return
        onPrevPageButtonClicked()
    }
    private fun onSkipToNextTrack(skipCounter: Int?) {
        if((skipCounter ?: 0) <= 0) return
        onNextPageButtonClicked()
    }
    private fun updateRepeatUI(repeat: Int?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                context?.let {
                    when (repeat) {
                        PlaybackStateCompat.REPEAT_MODE_ALL -> {
                            fragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                            fragmentPlayerBinding.buttonRepeat.icon =
                                ContextCompat.getDrawable(it, R.drawable.repeat)
                        }
                        PlaybackStateCompat.REPEAT_MODE_ONE -> {
                            fragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                            fragmentPlayerBinding.buttonRepeat.icon =
                                ContextCompat.getDrawable(it, R.drawable.repeat_one)
                        }
                        else -> {
                            fragmentPlayerBinding.buttonRepeat.alpha = 0.4f
                            fragmentPlayerBinding.buttonRepeat.icon =
                                ContextCompat.getDrawable(it, R.drawable.repeat)
                        }
                    }
                }
            }
        }
    }
    private fun updateShuffleUI(shuffle: Int?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                context?.let {
                    when (shuffle) {
                        PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                            fragmentPlayerBinding.buttonShuffle.alpha = 1.0f
                            fragmentPlayerBinding.buttonShuffle.icon =
                                ContextCompat.getDrawable(it, R.drawable.shuffle)
                        }
                        else -> {
                            fragmentPlayerBinding.buttonShuffle.alpha = 0.4f
                            fragmentPlayerBinding.buttonShuffle.icon =
                                ContextCompat.getDrawable(it, R.drawable.shuffle)
                        }
                    }
                }
            }
        }
    }
    private fun updateProgressValueUI(currentDuration: Long) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                context?.let {
                    val totalDuration: Long =
                        mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                    fragmentPlayerBinding.slider.value =
                        FormattersUtils.formatSongDurationToSliderProgress(
                            currentDuration,
                            totalDuration
                        )
                    fragmentPlayerBinding.textDurationCurrent.text =
                        FormattersUtils.formatSongDurationToString(currentDuration)
                }
            }
        }
    }
    private fun updatePlaybackStateUI(isPlaying: Boolean?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                context?.let {
                    if (isPlaying == true) {
                        fragmentPlayerBinding.buttonPlayPause.icon =
                            ContextCompat.getDrawable(it, R.drawable.pause_circle)
                    } else {
                        fragmentPlayerBinding.buttonPlayPause.icon =
                            ContextCompat.getDrawable(it, R.drawable.play_circle)
                    }
                }
            }
        }
    }
    private fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        updateTextTitleSubtitleDurationUI(songItem)
        updateViewpagerUI(songItem)
        updateBlurredBackgroundUIFromUri(songItem)
    }
    private fun updateViewpagerUI(songItem: SongItem?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            if (songItem == null || (mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
            val tempOldPosition: Int = fragmentPlayerBinding.viewPagerPlayer.currentItem
            val tempOldSongItem: SongItem? = mPlayerPagerAdapter?.currentList?.get(tempOldPosition)
            if (
                tempOldSongItem == null ||
                tempOldSongItem.id != songItem.id ||
                tempOldSongItem.uri != songItem.uri ||
                tempOldSongItem.title != songItem.title ||
                tempOldSongItem.artist != songItem.artist ||
                tempOldSongItem.duration != songItem.duration ||
                tempOldSongItem.position != songItem.position
            ) {
                fragmentPlayerBinding.viewPagerPlayer.currentItem = songItem.position
            }
        }
    }

    private fun updateBlurredBackgroundUIFromUri(songItem: SongItem?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            MainScope().launch {
                context?.let { ctx ->
                    val tempUri: Uri = Uri.parse(songItem?.uri ?: "")
                    val imageRequestBlurred: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newBlurInstance()
                    imageRequestBlurred.uri = tempUri
                    imageRequestBlurred.hashedCovertArtSignature = songItem?.hashedCovertArtSignature ?: -1
                    imageRequestBlurred.imageView = fragmentPlayerBinding.blurredImageview
                    ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
                }
            }
        }
    }
    private fun updateTextTitleSubtitleDurationUI(songItem: SongItem?) {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= (songItem?.position ?: 0))
                return
            MainScope().launch {
                context?.let {
                    fragmentPlayerBinding.textTitle.text =
                        if (songItem?.title != null)
                            songItem.title
                        else
                            songItem?.fileName
                    fragmentPlayerBinding.textArtist.text =
                        if (songItem?.artist != null)
                            songItem.artist
                        else
                            it.getString(R.string.unknown_artist)
                    fragmentPlayerBinding.textDuration.text =
                        FormattersUtils.formatSongDurationToString(
                            songItem?.duration ?: 0
                        )
                }
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            mPlayerPagerAdapter =
                PlayerPageAdapter(
                    this.requireContext(),
                    object : PlayerPageAdapter.OnItemClickListener {
                        override fun onButtonLyricsClicked(position: Int) {
                        }

                        override fun onButtonFullscreenClicked(position: Int) {
                        }

                    })
            fragmentPlayerBinding.viewPagerPlayer.adapter = mPlayerPagerAdapter
            fragmentPlayerBinding.viewPagerPlayer.clipToPadding = false
            fragmentPlayerBinding.viewPagerPlayer.clipChildren = false
            fragmentPlayerBinding.viewPagerPlayer.offscreenPageLimit = 5
            fragmentPlayerBinding.viewPagerPlayer.getChildAt(0)?.overScrollMode =
                View.OVER_SCROLL_NEVER
            ViewAnimatorsUtils.transformScaleViewPager(fragmentPlayerBinding.viewPagerPlayer)
        }
    }

    private fun checkInteractions() {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            fragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object :
                OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    MathComputationsUtils.fadeWithPageOffset(
                        fragmentPlayerBinding.blurredImageview,
                        positionOffset
                    )
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    onViewpagerPageChanged(position)
                }
            })
            fragmentPlayerBinding.slider.addOnSliderTouchListener(object : OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    updateOnStopTrackingTouch(slider.value)
                }
            })
            fragmentPlayerBinding.buttonPlayPause.setOnClickListener {
                onPlayPauseButtonClicked()
            }
            fragmentPlayerBinding.buttonSkipNext.setOnClickListener {
                onNextPageButtonClicked()
            }
            fragmentPlayerBinding.buttonSkipPrev.setOnClickListener {
                onPrevPageButtonClicked()
            }
            fragmentPlayerBinding.buttonShuffle.setOnClickListener {
                onShuffleButtonClicked()
            }
            fragmentPlayerBinding.buttonRepeat.setOnClickListener {
                onRepeatButtonClicked()
            }
            fragmentPlayerBinding.buttonMore.setOnClickListener {
                showMoreOptionsDialog()
            }
            fragmentPlayerBinding.buttonEqualizer.setOnClickListener {
                openEqualizerFragment()
            }
            fragmentPlayerBinding.buttonRescanDevice.setOnClickListener {
                openMediaScannerActivity()
            }
            fragmentPlayerBinding.dragHandleViewContainer.setOnClickListener {
                showQueueMusicDialog()
            }
        }
    }
    private fun showQueueMusicDialog() {
        mQueueMusicBottomSheetDialog?.show(childFragmentManager, QueueMusicBottomSheetDialog.TAG)
    }
    private fun openMediaScannerActivity() {
        startActivity(Intent(context, MediaScannerSettingsActivity::class.java).apply {})
    }
    private fun openEqualizerFragment() {
        mPlayerFragmentViewModel.setShowEqualizerFragmentCounter()
    }
    private fun showMoreOptionsDialog() {
        if(mPlayerMoreBottomSheetDialog?.isVisible == false)
            activity?.supportFragmentManager?.let { mPlayerMoreBottomSheetDialog?.show(it, PlayerMoreFullBottomSheetDialog.TAG) }
    }
    private fun onRepeatButtonClicked(){
        when (mPlayerFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                mPlayerFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_ALL)
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                mPlayerFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_ONE)
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                mPlayerFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_NONE)
            }
        }
    }
    private fun onShuffleButtonClicked(){
        val tempShuffleValue: Int = mPlayerFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE
        if(tempShuffleValue == PlaybackStateCompat.SHUFFLE_MODE_NONE){
            mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        }else if(tempShuffleValue == PlaybackStateCompat.SHUFFLE_MODE_ALL){
            mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        }
    }
    private fun onPrevPageButtonClicked(){
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition - 1))
            ?: return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun onNextPageButtonClicked(){
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun getCurrentPlayingSongFromPosition(position: Int): SongItem? {
        if (position < 0 || position >= (mPlayerPagerAdapter?.currentList?.size ?: 0)) return null
        val tempSongItem: SongItem = mPlayerPagerAdapter?.currentList?.get(position) ?: return null
        tempSongItem.position = position
        return tempSongItem
    }
    private fun onPlayPauseButtonClicked(){
        mPlayerFragmentViewModel.toggleIsPlaying()
    }
    private fun updateOnStopTrackingTouch(value: Float) {
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val totalDuration : Long = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
        val tempProgress : Long = FormattersUtils.formatSliderProgressToLongDuration(value, totalDuration)
        mPlayerFragmentViewModel.setPlayingProgressValue(tempProgress)
    }
    private fun onViewpagerPageChanged(position: Int) {
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((position))
            ?: return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun initViews() {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            fragmentPlayerBinding.textTitle.isSelected = true
            fragmentPlayerBinding.textArtist.isSelected = true

            ViewInsetModifiersUtils.updateTopViewInsets(fragmentPlayerBinding.linearRescanDeviceContainer)
            ViewInsetModifiersUtils.updateTopViewInsets(fragmentPlayerBinding.linearViewpager)

            ViewInsetModifiersUtils.updateBottomViewInsets(fragmentPlayerBinding.dragHandleViewContainer)
            ViewInsetModifiersUtils.updateBottomViewInsets(fragmentPlayerBinding.constraintBottomButtonsContainer)

            if (mPlayerMoreBottomSheetDialog == null)
                mPlayerMoreBottomSheetDialog = PlayerMoreFullBottomSheetDialog.newInstance(
                    mPlayerFragmentViewModel,
                    mMainFragmentViewModel,
                    fragmentPlayerBinding.root
                )
            if (mQueueMusicBottomSheetDialog == null)
                mQueueMusicBottomSheetDialog = QueueMusicBottomSheetDialog.newInstance(
                    mPlayerFragmentViewModel,
                    mPlayerPagerAdapter?.currentList
                )
        }
    }

    companion object {
        const val TAG = "PlayerFragment"

        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}