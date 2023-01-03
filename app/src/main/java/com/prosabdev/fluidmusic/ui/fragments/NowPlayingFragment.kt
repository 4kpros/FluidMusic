package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.common.sharedprefs.models.SleepTimerSP
import com.prosabdev.common.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.common.utils.*
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentNowPlayingBinding
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@BuildCompat.PrereleaseSdkCheck class NowPlayingFragment : Fragment() {

    private var mDataBidingView: FragmentNowPlayingBinding? = null

    private val mSongItemViewModel: SongItemViewModel by activityViewModels()

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mExploreContentsForFragmentViewModel: ExploreContentsForFragmentViewModel by activityViewModels()
    private val mAlbumArtistsFragmentViewModel: AlbumArtistsFragmentViewModel by activityViewModels()
    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by activityViewModels()
    private val mArtistsFragmentViewModel: ArtistsFragmentViewModel by activityViewModels()
    private val mComposersFragmentViewModel: ComposersFragmentViewModel by activityViewModels()
    private val mFoldersFragmentViewModel: FoldersFragmentViewModel by activityViewModels()
    private val mGenresFragmentViewModel: GenresFragmentViewModel by activityViewModels()
    private val mYearsFragmentViewModel: YearsFragmentViewModel by activityViewModels()

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mNowPlayingFragmentViewModel by activityViewModels<NowPlayingFragmentViewModel> {
        InjectorUtils.provideNowPlayingFragmentViewModel(requireContext())
    }

    private var mQueueMusicBottomSheetDialog: QueueMusicBottomSheetDialog = QueueMusicBottomSheetDialog.newInstance()
    private var mPlayerMoreBottomSheetDialog: PlayerMoreFullBottomSheetDialog = PlayerMoreFullBottomSheetDialog.newInstance()

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            loadLastPlayerSession()
        }

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_now_playing,
            container,
            false
        )
        val view = mDataBidingView?.root

        initViews()
        setupViewPagerAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    override fun onDestroy() {
        saveCurrentPlayingSession()
        super.onDestroy()
    }

    private fun saveCurrentPlayingSession() {
        context?.let { ctx ->
            SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(
                ctx,
                mNowPlayingFragmentViewModel.getCurrentPlayingSong().value
            )
            SharedPreferenceManagerUtils.Player.savePlayingProgressValue(
                ctx,
                mNowPlayingFragmentViewModel.getPlayingProgressValue().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSource(
                ctx,
                mNowPlayingFragmentViewModel.getQueueListSource().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnIndex(
                ctx,
                mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnValue(
                ctx,
                mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value
            )
            SharedPreferenceManagerUtils.Player.saveRepeat(
                ctx,
                mNowPlayingFragmentViewModel.getRepeat().value
            )
            SharedPreferenceManagerUtils.Player.saveShuffle(
                ctx,
                mNowPlayingFragmentViewModel.getShuffle().value
            )
            SharedPreferenceManagerUtils.Player.saveSleepTimer(
                ctx,
                mNowPlayingFragmentViewModel.getSleepTimer().value
            )
            val sortOrganize = SortOrganizeItemSP()
            sortOrganize.sortOrderBy = mNowPlayingFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            sortOrganize.isInvertSort = mNowPlayingFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.saveSortOrganizeItemsFor(
                ctx,
                SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYER_QUEUE_MUSIC,
                sortOrganize
            )
        }
    }

    private fun loadLastPlayerSession() {
        context?.let { ctx ->
            val sortOrganize: SortOrganizeItemSP? = SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents
                .loadSortOrganizeItemsFor(
                    ctx,
                    SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYER_QUEUE_MUSIC
                )
            val queueListSource: String? = SharedPreferenceManagerUtils.Player.loadQueueListSource(ctx, null)
            val queueListSourceColumnIndex: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceColumnIndex(ctx)
            val queueListSourceColumnValue: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceColumnValue(ctx)

            mNowPlayingFragmentViewModel.setQueueListSource(queueListSource ?: AllSongsFragment.TAG)
            mNowPlayingFragmentViewModel.setQueueListSourceColumnIndex(queueListSourceColumnIndex)
            mNowPlayingFragmentViewModel.setQueueListSourceColumnValue(queueListSourceColumnValue)
            mNowPlayingFragmentViewModel.setSortBy(sortOrganize?.sortOrderBy ?: SORT_LIST_GRID_DEFAULT_VALUE)
            mNowPlayingFragmentViewModel.setIsInverted(sortOrganize?.isInvertSort ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE)

            loadDirectlyQueueMusicListFromDatabase(
                sortOrganize?.sortOrderBy ?: SORT_LIST_GRID_DEFAULT_VALUE,
                sortOrganize?.isInvertSort ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE,
                queueListSource ?: AllSongsFragment.TAG,
                queueListSourceColumnIndex,
                queueListSourceColumnValue
            )
            val songItem: SongItem? = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx)
            val progressValue: Long = SharedPreferenceManagerUtils.Player.loadPlayingProgressValue(ctx)
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
                        mSongItemViewModel.getAtUri(songItem.uri)
                    if(tempSongItem?.uri == null){
                        MainScope().launch {
                            tryToGetFirstSong()
                        }
                    }else{
                        tempSongItem.position = songItem.position
                        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
                        mNowPlayingFragmentViewModel.setPlayingProgressValue(progressValue)
                        mNowPlayingFragmentViewModel.setIsPlaying(false)
                        mNowPlayingFragmentViewModel.setRepeat(repeat)
                        mNowPlayingFragmentViewModel.setShuffle(shuffle)
                        mNowPlayingFragmentViewModel.setSleepTimer(sleepTimer)
                        mNowPlayingFragmentViewModel.setSleepTimerStateStarted(false)
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
                    SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnIndex(ctx, null)
                    SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnValue(ctx, null)
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
                        mNowPlayingFragmentViewModel.setCurrentPlayingSong(null)
                        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, null)
                    } else {
                        mNowPlayingFragmentViewModel.setCurrentPlayingSong(this@apply)
                        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, this@apply)
                    }
                }
            }
        }
    }
    private fun loadDirectlyQueueMusicListFromDatabase(
        sortOrderBy: String,
        isInvertSort: Boolean,
        queueListSource: String,
        queueListSourceColumnIndex: String?,
        queueListSourceColumnValue: String?
    ) {
        when (queueListSource) {
            AllSongsFragment.TAG -> {
                MainScope().launch {
                    val songList = mSongItemViewModel.getAllDirectly(sortOrderBy)
                    updateEmptyListUI(songList?.size ?: 0)
                    mPlayerPagerAdapter?.submitList(
                        if(isInvertSort) songList?.reversed() else songList
                    )
                    mQueueMusicBottomSheetDialog.updateQueueMusicList(
                        if(isInvertSort) songList?.reversed() else songList
                    )
                }
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
                MainScope().launch {
                    val songList = mSongItemViewModel.getAllDirectlyWhereEqual(
                        queueListSourceColumnIndex,
                        queueListSourceColumnValue,
                        sortOrderBy
                    )
                    updateEmptyListUI(songList?.size ?: 0)
                    mPlayerPagerAdapter?.submitList(
                        if(isInvertSort) songList?.reversed() else songList
                    )
                    mQueueMusicBottomSheetDialog.updateQueueMusicList(
                        if(isInvertSort) songList?.reversed() else songList
                    )
                }
            }
        }
    }

    private fun updateEmptyListUI(size: Int) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                if(size > 0){
                    dataBidingView.linearRescanDeviceContainer.visibility = GONE
                }else{
                    dataBidingView.linearRescanDeviceContainer.visibility = VISIBLE
                }
            }
        }
    }

    private fun observeLiveData() {
        mNowPlayingFragmentViewModel.getUpdatePlaylistCounter().observe(viewLifecycleOwner){
            if(it > 0){
                updatePlayListData()
            }
        }
        mNowPlayingFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner){
            updateCurrentPlayingSongUI(it)
        }
        mNowPlayingFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner){
            updatePlaybackStateUI(it)
        }
        mNowPlayingFragmentViewModel.getPlayingProgressValue().observe(viewLifecycleOwner){
            updateProgressValueUI(it)
        }
        mNowPlayingFragmentViewModel.getShuffle().observe(viewLifecycleOwner){
            updateShuffleUI(it)
        }
        mNowPlayingFragmentViewModel.getRepeat().observe(viewLifecycleOwner){
            updateRepeatUI(it)
        }
        mNowPlayingFragmentViewModel.getSkipNextTrackCounter().observe(viewLifecycleOwner){
            onSkipToNextTrack(it)
        }
        mNowPlayingFragmentViewModel.getSkipPrevTrackCounter().observe(viewLifecycleOwner){
            onSkipToPrevTrack(it)
        }
        mNowPlayingFragmentViewModel.getSleepTimer().observe(viewLifecycleOwner){
            //
        }
        mNowPlayingFragmentViewModel.getSleepTimerStateStarted().observe(viewLifecycleOwner){
            //
        }
        mNowPlayingFragmentViewModel.getRequestPlaySongAt().observe(viewLifecycleOwner){
            tryToPlaySongAtFromRequest(it)
        }
        mNowPlayingFragmentViewModel.getRequestPlaySongShuffleCounter().observe(viewLifecycleOwner){
            tryToShuffleSongFromRequest(it)
        }
    }

    private fun tryToShuffleSongFromRequest(requestCounter: Int?) {
        if (requestCounter == null || requestCounter <= 0) return
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempSongPosition = MathComputationsUtils.randomExcluded(mDataBidingView?.viewPagerPlayer?.currentItem ?: -1, mPlayerPagerAdapter?.itemCount ?: 0)
        if(tempSongPosition >= (mPlayerPagerAdapter?.itemCount ?: 0) || tempSongPosition < 0)return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempSongPosition))
            ?: return
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        mNowPlayingFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_NONE)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun tryToPlaySongAtFromRequest(playSongAtRequest: com.prosabdev.common.models.PlaySongAtRequest?) {
        if (playSongAtRequest == null) return
        if(playSongAtRequest.position < 0 || playSongAtRequest.position > (mPlayerPagerAdapter?.currentList?.size ?: 0)) return

        val tempCurrentSongRequest = mPlayerPagerAdapter?.currentList?.get(playSongAtRequest.position)
        if(tempCurrentSongRequest != null){
            tempCurrentSongRequest.position = playSongAtRequest.position
            val tempShuffle : Int = if(playSongAtRequest.shuffle != null && (playSongAtRequest.shuffle ?: -1) >= 0) playSongAtRequest.shuffle ?: -1 else -1
            val tempRepeat : Int = if(playSongAtRequest.repeat != null && (playSongAtRequest.repeat ?: -1) >= 0) playSongAtRequest.repeat ?: -1 else -1
            if(tempRepeat > 0){
                mNowPlayingFragmentViewModel.setShuffle(tempShuffle)
            }
            if(tempRepeat > 0){
                mNowPlayingFragmentViewModel.setRepeat(tempRepeat)
            }
            mNowPlayingFragmentViewModel.setIsPlaying(playSongAtRequest.playDirectly)
            mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempCurrentSongRequest)
        }
    }

    private fun updatePlayListData() {
        MainScope().launch {
            val queueListSource: String = mNowPlayingFragmentViewModel.getQueueListSource().value ?: AllSongsFragment.TAG
            val tempIsInverted : Boolean = mNowPlayingFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            val songList = getNewSongsForSource(queueListSource)
            //Update UI
            updateEmptyListUI(songList.size)
            mPlayerPagerAdapter?.submitList(
                if(tempIsInverted)
                    songList.reversed() as List<SongItem>?
                else
                    songList as List<SongItem>?
            )
            mQueueMusicBottomSheetDialog.updateQueueMusicList(
                if(tempIsInverted)
                    songList.reversed() as List<SongItem>?
                else
                    songList as List<SongItem>?
            )
        }
    }

    private suspend fun getNewSongsForSource(queueListSource: String): List<Any> {
        val songList = ArrayList<Any>()
        withContext(Dispatchers.Default){
            when (queueListSource) {
                AllSongsFragment.TAG -> {
                    mAllSongsFragmentViewModel.getAllDirectly()
                        ?.let {
                            songList.addAll(it)
                        }
                }
                ExploreContentsForFragment.TAG -> {
                    mExploreContentsForFragmentViewModel.getAllDirectly()
                        ?.let {
                            songList.addAll(it)
                        }
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
                    when (queueListSource) {
                        AlbumArtistsFragment.TAG -> {
                            val dataList = mAlbumArtistsFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.AlbumArtistItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        AlbumsFragment.TAG -> {
                            val dataList = mAlbumsFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.AlbumItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        ArtistsFragment.TAG -> {
                            val dataList = mArtistsFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.ArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.ArtistItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        ComposersFragment.TAG -> {
                            val dataList = mComposersFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.ComposerItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        FoldersFragment.TAG -> {
                            val dataList = mFoldersFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.FolderItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.FolderItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        GenresFragment.TAG -> {
                            val dataList = mGenresFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.GenreItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.GenreItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                        YearsFragment.TAG -> {
                            val dataList = mYearsFragmentViewModel.getAllDirectly() ?: return@withContext
                            if(dataList.isEmpty()) return@withContext
                            for (i in dataList.indices){
                                val tempNewSongs: List<SongItem>? =
                                    mSongItemViewModel.getAllDirectlyWhereEqual(
                                        com.prosabdev.common.models.view.YearItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as com.prosabdev.common.models.view.YearItem).name,
                                        SongItem.DEFAULT_INDEX
                                    )
                                tempNewSongs?.let {
                                    songList.addAll(it)
                                }
                            }
                        }
                    }
                }
            }
        }
        return songList
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
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                context?.let {
                    when (repeat) {
                        PlaybackStateCompat.REPEAT_MODE_ALL -> {
                            dataBidingView.buttonRepeat.alpha = 1.0f
                            dataBidingView.buttonRepeat.icon =
                                ContextCompat.getDrawable(it, R.drawable.repeat)
                        }
                        PlaybackStateCompat.REPEAT_MODE_ONE -> {
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
    }
    private fun updateShuffleUI(shuffle: Int?) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                context?.let {
                    when (shuffle) {
                        PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
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
    }
    private fun updateProgressValueUI(currentDuration: Long) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                context?.let {
                    val totalDuration: Long =
                        mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                    dataBidingView.slider.value =
                        FormattersAndParsersUtils.formatSongDurationToSliderProgress(
                            currentDuration,
                            totalDuration
                        )
                    dataBidingView.textDurationCurrent.text =
                        FormattersAndParsersUtils.formatSongDurationToString(currentDuration)
                }
            }
        }
    }
    private fun updatePlaybackStateUI(isPlaying: Boolean?) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
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
    }
    private fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        updateViewpagerUI(songItem)
        updateTextTitleSubtitleDurationUI(songItem)
        updateBlurredBackgroundUIFromUri(songItem)
    }
    private fun updateViewpagerUI(songItem: SongItem?) {
        if (songItem == null || (mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        mDataBidingView?.let { dataBidingView ->
            val tempOldPosition: Int = dataBidingView.viewPagerPlayer.currentItem
            val tempOldSongItem = mPlayerPagerAdapter?.currentList?.get(tempOldPosition)
            if (
                tempOldPosition != songItem.position &&
                tempOldSongItem?.uri != songItem.uri &&
                tempOldSongItem?.id != songItem.id &&
                tempOldSongItem?.fileName != songItem.fileName
            ) {
                val smoothScroll: Boolean = mNowPlayingFragmentViewModel.getCanScrollSmoothViewpager().value ?: false
                mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(false)
                dataBidingView.viewPagerPlayer.setCurrentItem(songItem.position, smoothScroll)
            }
        }
    }

    private fun updateBlurredBackgroundUIFromUri(songItem: SongItem?) {
        mDataBidingView?.let { dataBidingView ->
            context?.let { ctx ->
                val tempUri: Uri = Uri.parse(songItem?.uri ?: "")
                val imageRequestBlurred: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newBlurInstance()
                imageRequestBlurred.uri = tempUri
                imageRequestBlurred.hashedCovertArtSignature = songItem?.hashedCovertArtSignature ?: -1
                imageRequestBlurred.imageView = dataBidingView.blurredImageview
                ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
            }
        }
    }
    private fun updateTextTitleSubtitleDurationUI(songItem: SongItem?) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                context?.let { ctx ->
                    dataBidingView.textTitle.text =
                        songItem?.title?.ifEmpty { songItem.fileName ?: ctx.getString(R.string.unknown_title) } ?: songItem?.fileName ?: ctx.getString(R.string.unknown_title)
                    dataBidingView.textArtist.text =
                        songItem?.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)
                    dataBidingView.textDuration.text =
                        FormattersAndParsersUtils.formatSongDurationToString(
                            songItem?.duration ?: 0
                        )
                }
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mDataBidingView?.let { dataBidingView ->
            context?.let { ctx ->
                mPlayerPagerAdapter = PlayerPageAdapter(
                    ctx,
                    object : PlayerPageAdapter.OnItemClickListener {
                        override fun onButtonLyricsClicked(position: Int) {
                        }

                        override fun onButtonFullscreenClicked(position: Int) {
                        }
                    })
                dataBidingView.viewPagerPlayer.adapter = mPlayerPagerAdapter
                dataBidingView.viewPagerPlayer.clipToPadding = false
                dataBidingView.viewPagerPlayer.clipChildren = false
                dataBidingView.viewPagerPlayer.offscreenPageLimit = 3
                dataBidingView.viewPagerPlayer.getChildAt(0)?.overScrollMode =
                    View.OVER_SCROLL_NEVER
                AnimatorsUtils.applyPageTransformer(dataBidingView.viewPagerPlayer)
            }
        }
    }

    private fun checkInteractions() {
        mDataBidingView?.let { dataBidingView ->
            var viewpagerScrollState =  ViewPager2.SCROLL_STATE_IDLE
            dataBidingView.viewPagerPlayer.registerOnPageChangeCallback(object :
                OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    MathComputationsUtils.fadeWithPageOffset(
                        dataBidingView.blurredImageview,
                        positionOffset
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if(state == ViewPager2.SCROLL_STATE_DRAGGING){
                        viewpagerScrollState = state
                    }
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(viewpagerScrollState == ViewPager2.SCROLL_STATE_DRAGGING){
                        viewpagerScrollState = ViewPager2.SCROLL_STATE_IDLE
                        onViewpagerPageChanged(position)
                    }else{
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
        val currentItem = mDataBidingView?.viewPagerPlayer?.currentItem
        val tempCurrentSongItem = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value
        val tempSongItem = getCurrentPlayingSongFromPosition(currentItem)
        if (
            currentItem == tempCurrentSongItem?.position &&
            tempSongItem?.uri == tempCurrentSongItem?.uri &&
            tempSongItem?.id == tempCurrentSongItem?.id &&
            tempSongItem?.folderUri == tempCurrentSongItem?.folderUri
        ){
            return
        }
        mDataBidingView?.viewPagerPlayer?.setCurrentItem(tempCurrentSongItem?.position ?: 0, false)
    }

    private fun showQueueMusicDialog() {
        if(!mQueueMusicBottomSheetDialog.isVisible) {
            mQueueMusicBottomSheetDialog.updateQueueMusicList(
                mPlayerPagerAdapter?.currentList
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
        if(!mPlayerMoreBottomSheetDialog.isVisible) {
            activity?.supportFragmentManager?.let {
                mPlayerMoreBottomSheetDialog.show(it, PlayerMoreFullBottomSheetDialog.TAG)
            }
        }
    }
    private fun onRepeatButtonClicked(){
        when (mNowPlayingFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_ALL)
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_ONE)
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                mNowPlayingFragmentViewModel.setRepeat(PlaybackStateCompat.REPEAT_MODE_NONE)
            }
        }
    }
    private fun onShuffleButtonClicked(){
        val tempShuffleValue: Int = mNowPlayingFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE
        if(tempShuffleValue == PlaybackStateCompat.SHUFFLE_MODE_NONE){
            mNowPlayingFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        }else if(tempShuffleValue == PlaybackStateCompat.SHUFFLE_MODE_ALL){
            mNowPlayingFragmentViewModel.setShuffle(PlaybackStateCompat.SHUFFLE_MODE_NONE)
        }
    }
    private fun onPrevPageButtonClicked(){
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition - 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun onNextPageButtonClicked(){
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun getCurrentPlayingSongFromPosition(position: Int?): SongItem? {
        if(position == null) return null
        if (position < 0 || position >= (mPlayerPagerAdapter?.currentList?.size ?: 0)) return null
        val tempSongItem: SongItem = mPlayerPagerAdapter?.currentList?.get(position) ?: return null
        tempSongItem.position = position
        return tempSongItem
    }
    private fun onPlayPauseButtonClicked(){
        mNowPlayingFragmentViewModel.toggleIsPlaying()
    }
    private fun updateOnStopTrackingTouch(value: Float) {
        if((mPlayerPagerAdapter?.currentList?.size ?: 0) <= 0) return
        val totalDuration : Long = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
        val tempProgress : Long = FormattersAndParsersUtils.formatSliderProgressToLongDuration(value, totalDuration)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(tempProgress)
    }
    private fun onViewpagerPageChanged(position: Int) {
        if((mDataBidingView?.viewPagerPlayer?.adapter?.itemCount ?: 0) < 1) return
        val tempCurrentSongItem = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value
        val tempSongItem = getCurrentPlayingSongFromPosition(position)
        if (
            position == tempCurrentSongItem?.position &&
            tempSongItem?.uri == tempCurrentSongItem.uri &&
            tempSongItem?.id == tempCurrentSongItem.id &&
            tempSongItem.folderUri == tempCurrentSongItem.folderUri
        ){
            return
        }

        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }

    private fun initViews() {
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.textTitle.isSelected = true
            dataBidingView.textArtist.isSelected = true

            InsetModifiersUtils.updateTopViewInsets(dataBidingView.linearRescanDeviceContainer)
            InsetModifiersUtils.updateTopViewInsets(dataBidingView.linearViewpager)

            InsetModifiersUtils.updateBottomViewInsets(dataBidingView.dragHandleViewContainer)
            InsetModifiersUtils.updateBottomViewInsets(dataBidingView.constraintBottomButtonsContainer)
//            InsetModifiersUtils.updateRightViewInsets(dataBidingView.dragHandleViewContainer)
//            InsetModifiersUtils.updateRightViewInsets(dataBidingView.constraintBottomButtonsContainer)
//            InsetModifiersUtils.updateRightViewInsets(dataBidingView.linearControls)

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
            NowPlayingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}