package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.QueueMusicItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment() {

    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog? = null
    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()

    private lateinit var mSongItemViewModel: SongItemViewModel
    private val mQueueMusicItemViewModel: QueueMusicItemViewModel by activityViewModels()

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog? = null

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
    ): View {
        mFragmentPlayerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_player,container,false)
        val view = mFragmentPlayerBinding.root

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
        MainScope().launch {
            if(size > 0){
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = GONE
            }else{
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = VISIBLE
            }
        }
    }
    private suspend fun updateBlurredBackgroundUI(position: Int) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
            return
        withContext(Dispatchers.Default){
            val ctx : Context = this@PlayerFragment.context ?: return@withContext
            val tempUri : Uri = Uri.parse(mPlayerPagerAdapter?.currentList?.get(position)?.uri ?: "")
            ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                ctx,
                mFragmentPlayerBinding.blurredImageview,
                tempUri,
                100
            )
        }
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getQueueListSource().observe(viewLifecycleOwner){
            val queueListSource: String = it ?: ConstantValues.EXPLORE_ALL_SONGS
            if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
                mAllSongsFragmentViewModel.getAllSongs().observe(viewLifecycleOwner){ songList ->
                    updateEmptyListUI(songList?.size ?: 0)
                    mPlayerPagerAdapter?.submitList(songList)
                    mQueueMusicBottomSheetDialog?.updateQueueMusicList(songList)
                }
            }
        }
        mPlayerFragmentViewModel.getSourceOfQueueListValue().observe(this as LifecycleOwner){
            //
        }
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(this as LifecycleOwner){
            updateCurrentPlayingSongUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(this as LifecycleOwner){
            updatePlaybackStateUI(it)
        }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(this as LifecycleOwner){
            updateProgressValueUI(it)
        }
        mPlayerFragmentViewModel.getShuffle().observe(this as LifecycleOwner){
            updateShuffleUI(it)
        }
        mPlayerFragmentViewModel.getRepeat().observe(this as LifecycleOwner){
            updateRepeatUI(it)
        }
        mPlayerFragmentViewModel.getSkipNextTrackCounter().observe(this as LifecycleOwner){
            onSkipToNextTrack(it)
        }
        mPlayerFragmentViewModel.getSkipPrevTrackCounter().observe(this as LifecycleOwner){
            onSkipToPrevTrack(it)
        }
        mPlayerFragmentViewModel.getSleepTimer().observe(this as LifecycleOwner){
            //
        }
        mPlayerFragmentViewModel.getSleepTimerStateStarted().observe(this as LifecycleOwner){
            //
        }

        //Observe changes from all songs fragment

        val tempIsInverted: Boolean = mAllSongsFragmentViewModel.getIsInverted().value ?: false
        val tempOrganizeListGrid: Int = mAllSongsFragmentViewModel.getOrganizeListGrid().value ?: ConstantValues.ORGANIZE_LIST
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
        MainScope().launch {
            context?.let {
                when (repeat) {
                    PlaybackStateCompat.REPEAT_MODE_ALL -> {
                        mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                        mFragmentPlayerBinding.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat)
                    }
                    PlaybackStateCompat.REPEAT_MODE_ONE -> {
                        mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                        mFragmentPlayerBinding.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat_one)
                    }
                    else -> {
                        mFragmentPlayerBinding.buttonRepeat.alpha = 0.4f
                        mFragmentPlayerBinding.buttonRepeat.icon =
                            ContextCompat.getDrawable(it, R.drawable.repeat)
                    }
                }
            }
        }
    }
    private fun updateShuffleUI(shuffle: Int?) {
        MainScope().launch {
            context?.let {
                when (shuffle) {
                    PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                        mFragmentPlayerBinding.buttonShuffle.alpha = 1.0f
                        mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(it, R.drawable.shuffle)
                    }
                    else -> {
                        mFragmentPlayerBinding.buttonShuffle.alpha = 0.4f
                        mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(it, R.drawable.shuffle)
                    }
                }
            }

        }
    }
    private fun updateProgressValueUI(currentDuration: Long) {
        MainScope().launch {
            context?.let {
                val totalDuration: Long = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                mFragmentPlayerBinding.slider.value = FormattersUtils.formatSongDurationToSliderProgress(currentDuration, totalDuration)
                mFragmentPlayerBinding.textDurationCurrent.text = FormattersUtils.formatSongDurationToString(currentDuration)
            }
        }
    }
    private fun updatePlaybackStateUI(isPlaying: Boolean?) {
        MainScope().launch {
            context?.let {
                if(isPlaying == true){
                    mFragmentPlayerBinding.buttonPlayPause.icon = ContextCompat.getDrawable(it, R.drawable.pause_circle)
                }else{
                    mFragmentPlayerBinding.buttonPlayPause.icon = ContextCompat.getDrawable(it, R.drawable.play_circle)
                }
            }
        }
    }
    private fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        updateTextTitleSubtitleDurationUI(songItem)
        updateViewpagerUI(songItem)
        updateBlurredBackgroundUIFromUri(songItem?.uri)
    }
    private fun updateViewpagerUI(songItem: SongItem?) {
        if (songItem == null || (mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempOldPosition : Int = mFragmentPlayerBinding.viewPagerPlayer.currentItem
        val tempOldSongItem : SongItem? = mPlayerPagerAdapter?.currentList?.get(tempOldPosition)
        if(
            tempOldSongItem == null ||
            tempOldSongItem.id != songItem.id ||
            tempOldSongItem.uri != songItem.uri ||
            tempOldSongItem.title != songItem.title ||
            tempOldSongItem.artist != songItem.artist ||
            tempOldSongItem.duration != songItem.duration ||
            tempOldSongItem.position != songItem.position
        ){
            mFragmentPlayerBinding.viewPagerPlayer.currentItem = songItem.position
        }
    }

    private fun updateBlurredBackgroundUIFromUri(uriString: String?) {
        MainScope().launch {
            context?.let {
                val tempUri : Uri = Uri.parse(uriString ?: "")
                ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                    it,
                    mFragmentPlayerBinding.blurredImageview,
                    tempUri,
                    100
                )
            }
        }
    }
    private fun updateTextTitleSubtitleDurationUI(songItem: SongItem?) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= (songItem?.position ?: 0))
            return
        MainScope().launch {
            context?.let {
                mFragmentPlayerBinding.textTitle.text =
                    if(songItem?.title != null )
                        songItem.title
                    else
                        songItem?.fileName
                mFragmentPlayerBinding.textArtist.text =
                    if(songItem?.artist != null )
                        songItem.artist
                    else
                        it.getString(R.string.unknown_artist)
                mFragmentPlayerBinding.textDuration.text =
                    FormattersUtils.formatSongDurationToString(
                    songItem?.duration ?: 0
                    )
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mPlayerPagerAdapter =
            PlayerPageAdapter(this.requireContext(), object : PlayerPageAdapter.OnItemClickListener {
                override fun onButtonLyricsClicked(position: Int) {
                }
                override fun onButtonFullscreenClicked(position: Int) {
                }

            })
        mFragmentPlayerBinding.viewPagerPlayer.adapter = mPlayerPagerAdapter
        mFragmentPlayerBinding.viewPagerPlayer.clipToPadding = false
        mFragmentPlayerBinding.viewPagerPlayer.clipChildren = false
        mFragmentPlayerBinding.viewPagerPlayer.offscreenPageLimit = 5
        mFragmentPlayerBinding.viewPagerPlayer.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
        ViewAnimatorsUtils.transformScaleViewPager(mFragmentPlayerBinding.viewPagerPlayer)
    }

    private fun checkInteractions() {
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                MathComputationsUtils.fadeWithPageOffset(mFragmentPlayerBinding.blurredImageview, positionOffset)
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewpagerPageChanged(position)
            }
        })
        mFragmentPlayerBinding.slider.addOnSliderTouchListener(object : OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
            }
            override fun onStopTrackingTouch(slider: Slider) {
                updateOnStopTrackingTouch(slider.value)
            }
        })
        mFragmentPlayerBinding.buttonPlayPause.setOnClickListener {
            onPlayPauseButtonClicked()
        }
        mFragmentPlayerBinding.buttonSkipNext.setOnClickListener {
            onNextPageButtonClicked()
        }
        mFragmentPlayerBinding.buttonSkipPrev.setOnClickListener {
            onPrevPageButtonClicked()
        }
        mFragmentPlayerBinding.buttonShuffle.setOnClickListener {
            onShuffleButtonClicked()
        }
        mFragmentPlayerBinding.buttonRepeat.setOnClickListener {
            onRepeatButtonClicked()
        }
        mFragmentPlayerBinding.buttonMore.setOnClickListener {
            showMoreOptionsDialog()
        }
        mFragmentPlayerBinding.buttonEqualizer.setOnClickListener {
            openEqualizerFragment()
        }
        mFragmentPlayerBinding.buttonRescanDevice.setOnClickListener {
            openMediaScannerFragment()
        }
        mFragmentPlayerBinding.dragHandleViewContainer.setOnClickListener{
            showQueueMusicDialog()
        }
    }
    private fun showQueueMusicDialog() {
        mQueueMusicBottomSheetDialog?.show(childFragmentManager, QueueMusicBottomSheetDialog.TAG)
    }
    private fun openMediaScannerFragment() {
        //
    }
    private fun openEqualizerFragment() {
        //
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
        val tempPosition : Int = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition - 1))
            ?: return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun onNextPageButtonClicked(){
        val tempPosition : Int = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun getCurrentPlayingSongFromPosition(position: Int): SongItem? {
        if (position < 0 || position >= (mPlayerPagerAdapter?.currentList?.size ?: 0)) return null
        val tempSongItem: SongItem = mPlayerPagerAdapter?.currentList?.get(position.toInt()) ?: return null
        tempSongItem.position = position
        return tempSongItem
    }
    private fun onPlayPauseButtonClicked(){
        mPlayerFragmentViewModel.toggleIsPlaying()
    }
    private fun updateOnStopTrackingTouch(value: Float) {
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
        mSongItemViewModel = ModelsViewModelFactory(this.requireContext()).create(SongItemViewModel::class.java)

        mFragmentPlayerBinding.blurredImageview.layout(0,0,0,0)
        mFragmentPlayerBinding.textTitle.isSelected = true
        mFragmentPlayerBinding.textArtist.isSelected = true

        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentPlayerBinding.linearRescanDeviceContainer)
        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentPlayerBinding.linearViewpager)

        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentPlayerBinding.dragHandleViewContainer)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentPlayerBinding.constraintBottomButtonsContainer)

        if(mPlayerMoreBottomSheetDialog == null)
            mPlayerMoreBottomSheetDialog = PlayerMoreFullBottomSheetDialog.newInstance(mPlayerFragmentViewModel, mMainFragmentViewModel, mFragmentPlayerBinding.root)
        if(mQueueMusicBottomSheetDialog == null)
            mQueueMusicBottomSheetDialog = QueueMusicBottomSheetDialog.newInstance(mPlayerFragmentViewModel, mPlayerPagerAdapter?.currentList)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}