package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
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
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.PlaySongAtRequest
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment() {

    private var mFragmentPlayerBinding: FragmentPlayerBinding? = null

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()

    private val mSongItemViewModel: SongItemViewModel by activityViewModels()

    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog = QueueMusicBottomSheetDialog.newInstance()
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog = PlayerMoreFullBottomSheetDialog.newInstance()

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()

        if(savedInstanceState == null){
            loadLastPlayerSession()
            loadDirectlyQueueMusicListFromDatabase()
        }

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
        setupViewPagerAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    private fun loadDirectlyQueueMusicListFromDatabase() {
        when (mPlayerFragmentViewModel.getQueueListSource().value ?: AllSongsFragment.TAG) {
            AllSongsFragment.TAG -> {
                MainScope().launch {
                    val tempIsInverted : Boolean = mAllSongsFragmentViewModel.getIsInverted().value ?: false
                    val songList =
                        if(tempIsInverted)
                            mSongItemViewModel.getAllDirectly(mPlayerFragmentViewModel.getSortBy().value ?: "title")
                                ?.reversed()
                        else
                            mSongItemViewModel.getAllDirectly(mPlayerFragmentViewModel.getSortBy().value ?: "title")
                    updateEmptyListUI(songList?.size ?: 0)
                    mPlayerPagerAdapter?.submitList(songList)
                    mQueueMusicBottomSheetDialog.updateQueueMusicList(songList)
                }
            }
            AlbumsFragment.TAG -> {
                //
            }
            AlbumArtistsFragment.TAG -> {
                //
            }
            ArtistsFragment.TAG -> {
                //
            }
            ComposersFragment.TAG -> {
                //
            }
            FoldersFragment.TAG -> {
                //
            }
            GenresFragment.TAG -> {
                //
            }
            YearsFragment.TAG -> {
                //
            }

            ExploreContentsForFragment.TAG -> {
                //
            }

            FoldersHierarchyFragment.TAG -> {
                //
            }
            PlaylistsFragment.TAG -> {
                //
            }
            StreamsFragment.TAG -> {
                //
            }
            else -> {
                //
            }
        }
    }

    override fun onDestroy() {
        saveCurrentPlayingSession()
        super.onDestroy()
    }

    private fun saveCurrentPlayingSession() {
        context?.let { ctx ->
            SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(
                ctx,
                mPlayerFragmentViewModel.getCurrentPlayingSong().value
            )
            SharedPreferenceManagerUtils.Player.savePlayingProgressValue(
                ctx,
                mPlayerFragmentViewModel.getPlayingProgressValue().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSource(
                ctx,
                mPlayerFragmentViewModel.getQueueListSource().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(
                ctx,
                mPlayerFragmentViewModel.getSourceOfQueueListValue().value
            )
            SharedPreferenceManagerUtils.Player.saveRepeat(
                ctx,
                mPlayerFragmentViewModel.getRepeat().value
            )
            SharedPreferenceManagerUtils.Player.saveShuffle(
                ctx,
                mPlayerFragmentViewModel.getShuffle().value
            )
            SharedPreferenceManagerUtils.Player.saveSleepTimer(
                ctx,
                mPlayerFragmentViewModel.getSleepTimer().value
            )
            val sortOrganize: SortOrganizeItemSP = SortOrganizeItemSP()
            sortOrganize.sortOrderBy = mPlayerFragmentViewModel.getSortBy().value ?: "title"
            sortOrganize.isInvertSort = mPlayerFragmentViewModel.getIsInverted().value ?:false
            SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.saveSortOrganizeItemsFor(
                ctx,
                SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_PLAYER_QUEUE_MUSIC,
                sortOrganize
            )
        }
    }

    private fun loadLastPlayerSession() {
        context?.let { ctx ->
            val sortOrganize: SortOrganizeItemSP? = SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents
                .loadSortOrganizeItemsFor(
                    ctx,
                    SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SHARED_PREFERENCES_SORT_ORGANIZE_PLAYER_QUEUE_MUSIC
                )
            val queueListSource: String? = SharedPreferenceManagerUtils.Player.loadQueueListSource(ctx)
            mPlayerFragmentViewModel.setSortBy(sortOrganize?.sortOrderBy ?: "title")
            mPlayerFragmentViewModel.setIsInverted(sortOrganize?.isInvertSort ?: false)
            mPlayerFragmentViewModel.setQueueListSource(queueListSource ?: AllSongsFragment.TAG)

            val songItem: SongItem? = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx)
            val progressValue: Long = SharedPreferenceManagerUtils.Player.loadPlayingProgressValue(ctx)
            val queueListSourceValue: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceValue(ctx)
            val repeat: Int = SharedPreferenceManagerUtils.Player.loadRepeat(ctx)
            val shuffle: Int = SharedPreferenceManagerUtils.Player.loadShuffle(ctx)
            val sleepTimer: SleepTimerSP? = SharedPreferenceManagerUtils.Player.loadSleepTimer(ctx)

            if(songItem?.uri == null){
                MainScope().launch {
                    tryToGetFirstSong()
                }
            }else{
                MainScope().launch {
                    val tempSongItem : SongItem? =
                        mSongItemViewModel.getAtUri(songItem.uri ?: "")
                    if(tempSongItem?.uri == null){
                        MainScope().launch {
                            tryToGetFirstSong()
                        }
                    }else{
                        tempSongItem.position = songItem.position
                        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
                        mPlayerFragmentViewModel.setPlayingProgressValue(progressValue)
                        mPlayerFragmentViewModel.setIsPlaying(false)
                        mPlayerFragmentViewModel.setRepeat(repeat)
                        mPlayerFragmentViewModel.setShuffle(shuffle)
                        mPlayerFragmentViewModel.setQueueListSourceValue(queueListSourceValue)
                        mPlayerFragmentViewModel.setSleepTimer(sleepTimer)
                        mPlayerFragmentViewModel.setSleepTimerStateStarted(false)
                        Log.i(ConstantValues.TAG, "CURRENT SONG sdadasdasd playingPosition : ${tempSongItem?.position}")
                    }
                }
            }
        }
    }
    private suspend fun tryToGetFirstSong() {
        context?.let { ctx ->
            withContext(Dispatchers.IO) {
                mSongItemViewModel.getFirstSong().apply {
                    SharedPreferenceManagerUtils.Player.saveQueueListSource(
                        ctx,
                        AllSongsFragment.TAG
                    )
                    SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(ctx, "")
                    SharedPreferenceManagerUtils.Player.saveRepeat(
                        ctx,
                        PlaybackStateCompat.REPEAT_MODE_NONE
                    )
                    SharedPreferenceManagerUtils.Player.saveShuffle(
                        ctx,
                        PlaybackStateCompat.SHUFFLE_MODE_NONE
                    )
                    SharedPreferenceManagerUtils.Player.saveSleepTimer(ctx, null)
                    SharedPreferenceManagerUtils.Player.savePlayingProgressValue(ctx, 0)

                    if (this@apply?.uri == null) {
                        mPlayerFragmentViewModel.setCurrentPlayingSong(null)
                        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, null)
                    } else {
                        mPlayerFragmentViewModel.setCurrentPlayingSong(this@apply)
                        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, this@apply)
                    }
                }
            }
        }
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

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getUpdatePlaylistCounter().observe(viewLifecycleOwner){
            if(it > 0){
                updatePlayListData()
            }
        }
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner){
            updateCurrentPlayingSongUI(it, mPlayerFragmentViewModel.getIsQueueMusicUpdated().value)
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
        mPlayerFragmentViewModel.getRequestPlaySongAt().observe(viewLifecycleOwner){
            tryToPlaySongAtFromRequest(it)
        }
        mPlayerFragmentViewModel.getRequestPlaySongShuffleCounter().observe(viewLifecycleOwner){
            tryToShuffleSongFromRequest(it)
        }
    }

    private fun tryToShuffleSongFromRequest(requestCounter: Int?) {
        if (requestCounter == null || requestCounter <= 0) return
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempSongPosition = MathComputationsUtils.randomExcluded(mFragmentPlayerBinding?.viewPagerPlayer?.currentItem ?: -1, mPlayerPagerAdapter?.itemCount ?: 0)
        if(tempSongPosition >= (mPlayerPagerAdapter?.itemCount ?: 0) || tempSongPosition < 0)return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempSongPosition))
            ?: return
        mPlayerFragmentViewModel.setCanScrollSmoothViewpager(false)
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        mPlayerFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_NONE)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun tryToPlaySongAtFromRequest(playSongAtRequest: PlaySongAtRequest?) {
        if (playSongAtRequest == null) return
        if(playSongAtRequest.position < 0 || playSongAtRequest.position > (mPlayerPagerAdapter?.currentList?.size ?: 0)) return

        val tempCurrentSongRequest = mPlayerPagerAdapter?.currentList?.get(playSongAtRequest.position)
        if(tempCurrentSongRequest != null){
            tempCurrentSongRequest.position = playSongAtRequest.position
            val tempShuffle : Int = if(playSongAtRequest.shuffle != null && (playSongAtRequest.shuffle ?: -1) >= 0) playSongAtRequest.shuffle ?: -1 else -1
            val tempRepeat : Int = if(playSongAtRequest.repeat != null && (playSongAtRequest.repeat ?: -1) >= 0) playSongAtRequest.repeat ?: -1 else -1
            if(tempRepeat > 0){
                mPlayerFragmentViewModel.setShuffle(tempShuffle)
            }
            if(tempRepeat > 0){
                mPlayerFragmentViewModel.setRepeat(tempRepeat)
            }
            mPlayerFragmentViewModel.setIsPlaying(playSongAtRequest.playDirectly)
            mPlayerFragmentViewModel.setCurrentPlayingSong(tempCurrentSongRequest)
        }
    }

    private fun updatePlayListData() {
        val queueListSource: String = mPlayerFragmentViewModel.getQueueListSource().value ?: AllSongsFragment.TAG
        if(queueListSource == AllSongsFragment.TAG) {
            val tempSortBy : String = mAllSongsFragmentViewModel.getSortBy().value ?: "title"
            val tempIsInverted : Boolean = mAllSongsFragmentViewModel.getIsInverted().value ?: false
            mPlayerFragmentViewModel.setSortBy(tempSortBy)
            mPlayerFragmentViewModel.setIsInverted(tempIsInverted)
            //Get songs
            val songList = (
                    if(tempIsInverted)
                        mAllSongsFragmentViewModel.getAllDirectly()?.reversed()
                    else
                        mAllSongsFragmentViewModel.getAllDirectly()
                    ) as List<SongItem>
            mPlayerPagerAdapter?.submitList(songList)
            mQueueMusicBottomSheetDialog?.updateQueueMusicList(songList)
            updateEmptyListUI(songList.size)
            mPlayerFragmentViewModel.setIsQueueMusicUpdated()
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
                        FormattersAndParsersUtils.formatSongDurationToSliderProgress(
                            currentDuration,
                            totalDuration
                        )
                    fragmentPlayerBinding.textDurationCurrent.text =
                        FormattersAndParsersUtils.formatSongDurationToString(currentDuration)
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
    private fun updateCurrentPlayingSongUI(songItem: SongItem?, isQueueMusicUpdated: Boolean?) {
        if(isQueueMusicUpdated == false) return
        updateViewpagerUI(songItem)
        updateTextTitleSubtitleDurationUI(songItem)
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
                val smoothScroll: Boolean = mPlayerFragmentViewModel.getCanScrollSmoothViewpager().value ?: false
                fragmentPlayerBinding.viewPagerPlayer.setCurrentItem(songItem.position, smoothScroll)
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
                        FormattersAndParsersUtils.formatSongDurationToString(
                            songItem?.duration ?: 0
                        )
                }
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            context?.let { ctx ->
                mPlayerPagerAdapter = PlayerPageAdapter(
                    ctx,
                    object : PlayerPageAdapter.OnItemClickListener {
                        override fun onButtonLyricsClicked(position: Int) {
                        }

                        override fun onButtonFullscreenClicked(position: Int) {
                        }

                    })
                fragmentPlayerBinding.viewPagerPlayer.adapter = mPlayerPagerAdapter
                fragmentPlayerBinding.viewPagerPlayer.clipToPadding = false
                fragmentPlayerBinding.viewPagerPlayer.clipChildren = false
                fragmentPlayerBinding.viewPagerPlayer.offscreenPageLimit = 3
                fragmentPlayerBinding.viewPagerPlayer.getChildAt(0)?.overScrollMode =
                    View.OVER_SCROLL_NEVER
                AnimatorsUtils.transformScaleViewPager(fragmentPlayerBinding.viewPagerPlayer)
            }
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
        mQueueMusicBottomSheetDialog.show(childFragmentManager, QueueMusicBottomSheetDialog.TAG)
    }
    private fun openMediaScannerActivity() {
        startActivity(Intent(context, MediaScannerSettingsActivity::class.java).apply {})
    }
    private fun openEqualizerFragment() {
        //
    }
    private fun showMoreOptionsDialog() {
        if(!mPlayerMoreBottomSheetDialog.isVisible)
            activity?.supportFragmentManager?.let {
                mPlayerMoreBottomSheetDialog.show(it, PlayerMoreFullBottomSheetDialog.TAG)
            }
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
        mPlayerFragmentViewModel.setCanScrollSmoothViewpager(true)
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun onNextPageButtonClicked(){
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mPlayerFragmentViewModel.setCanScrollSmoothViewpager(true)
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
        val tempProgress : Long = FormattersAndParsersUtils.formatSliderProgressToLongDuration(value, totalDuration)
        mPlayerFragmentViewModel.setPlayingProgressValue(tempProgress)
    }
    private fun onViewpagerPageChanged(position: Int) {
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((position))
            ?: return
        if (position == (mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0)) return
        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun initViews() {
        mFragmentPlayerBinding?.let { fragmentPlayerBinding ->
            fragmentPlayerBinding.textTitle.isSelected = true
            fragmentPlayerBinding.textArtist.isSelected = true

            InsetModifiersUtils.updateTopViewInsets(fragmentPlayerBinding.linearRescanDeviceContainer)
            InsetModifiersUtils.updateTopViewInsets(fragmentPlayerBinding.linearViewpager)

            InsetModifiersUtils.updateBottomViewInsets(fragmentPlayerBinding.dragHandleViewContainer)
            InsetModifiersUtils.updateBottomViewInsets(fragmentPlayerBinding.constraintBottomButtonsContainer)

            mPlayerMoreBottomSheetDialog.updateData(
                mPlayerFragmentViewModel,
                mMainFragmentViewModel,
                fragmentPlayerBinding.root
            )
            mQueueMusicBottomSheetDialog.updatePlayerFragmentViewModel(
                mPlayerFragmentViewModel
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