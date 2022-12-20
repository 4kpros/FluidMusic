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
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.PlaySongAtRequest
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.*
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.activities.EqualizerActivity
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreFullBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicBottomSheetDialog
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.ExploreContentsForFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment() {

    private var mDataBidingView: FragmentPlayerBinding? = null

    private val mAlbumItemViewModel: AlbumItemViewModel by viewModels()
    private val mAlbumArtistItemViewModel: AlbumArtistItemViewModel by viewModels()
    private val mArtistItemViewModel: ArtistItemViewModel by viewModels()
    private val mComposerItemViewModel: ComposerItemViewModel by viewModels()
    private val mFolderItemViewModel: FolderItemViewModel by viewModels()
    private val mGenreItemViewModel: GenreItemViewModel by viewModels()
    private val mYearItemViewModel: YearItemViewModel by viewModels()

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
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

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
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_player,container,false)
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
            SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnIndex(
                ctx,
                mPlayerFragmentViewModel.getQueueListSourceColumnIndex().value
            )
            SharedPreferenceManagerUtils.Player.saveQueueListSourceColumnValue(
                ctx,
                mPlayerFragmentViewModel.getQueueListSourceColumnValue().value
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
            val sortOrganize = SortOrganizeItemSP()
            sortOrganize.sortOrderBy = mPlayerFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            sortOrganize.isInvertSort = mPlayerFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
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
            val queueListSourceColumnIndex: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceColumnIndex(ctx)
            val queueListSourceColumnValue: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceColumnValue(ctx)

            mPlayerFragmentViewModel.setQueueListSource(queueListSource ?: AllSongsFragment.TAG)
            mPlayerFragmentViewModel.setQueueListSourceColumnIndex(queueListSourceColumnIndex)
            mPlayerFragmentViewModel.setQueueListSourceColumnValue(queueListSourceColumnValue)
            mPlayerFragmentViewModel.setSortBy(sortOrganize?.sortOrderBy ?: SORT_LIST_GRID_DEFAULT_VALUE)
            mPlayerFragmentViewModel.setIsInverted(sortOrganize?.isInvertSort ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE)

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
                        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
                        mPlayerFragmentViewModel.setPlayingProgressValue(progressValue)
                        mPlayerFragmentViewModel.setIsPlaying(false)
                        mPlayerFragmentViewModel.setRepeat(repeat)
                        mPlayerFragmentViewModel.setShuffle(shuffle)
                        mPlayerFragmentViewModel.setSleepTimer(sleepTimer)
                        mPlayerFragmentViewModel.setSleepTimerStateStarted(false)
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
        mPlayerFragmentViewModel.getUpdatePlaylistCounter().observe(viewLifecycleOwner){
            if(it > 0){
                updatePlayListData()
            }
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
        val tempSongPosition = MathComputationsUtils.randomExcluded(mDataBidingView?.viewPagerPlayer?.currentItem ?: -1, mPlayerPagerAdapter?.itemCount ?: 0)
        if(tempSongPosition >= (mPlayerPagerAdapter?.itemCount ?: 0) || tempSongPosition < 0)return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempSongPosition))
            ?: return
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
        MainScope().launch {
            val queueListSource: String = mPlayerFragmentViewModel.getQueueListSource().value ?: AllSongsFragment.TAG
            val tempIsInverted : Boolean = mPlayerFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
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
                                        AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as AlbumArtistItem).name,
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
                                        AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as AlbumItem).name,
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
                                        ArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as ArtistItem).name,
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
                                        ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as ComposerItem).name,
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
                                        FolderItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as FolderItem).name,
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
                                        GenreItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as GenreItem).name,
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
                                        YearItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as YearItem).name,
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
                        mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
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
                val smoothScroll: Boolean = mPlayerFragmentViewModel.getCanScrollSmoothViewpager().value ?: false
                mPlayerFragmentViewModel.setCanScrollSmoothViewpager(false)
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
                        songItem?.artist?.ifEmpty { ctx.getString(com.prosabdev.fluidmusic.R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)
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
        val tempCurrentSongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value
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
    private fun getCurrentPlayingSongFromPosition(position: Int?): SongItem? {
        if(position == null) return null
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
        if((mDataBidingView?.viewPagerPlayer?.adapter?.itemCount ?: 0) < 1) return
        val tempCurrentSongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value
        val tempSongItem = getCurrentPlayingSongFromPosition(position)
        if (
            position == tempCurrentSongItem?.position &&
            tempSongItem?.uri == tempCurrentSongItem.uri &&
            tempSongItem?.id == tempCurrentSongItem.id &&
            tempSongItem.folderUri == tempCurrentSongItem.folderUri
        ){
            return
        }

        mPlayerFragmentViewModel.setPlayingProgressValue(0)
        mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
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
                mPlayerFragmentViewModel,
                mMainFragmentViewModel,
                dataBidingView.root
            )
            mQueueMusicBottomSheetDialog.updatePlayerFragmentViewModel(
                mPlayerFragmentViewModel
            )
        }
    }

    companion object {
        const val TAG = "PlayerFragment"

        private const val SORT_LIST_GRID_DEFAULT_VALUE: String = "playOrder"
        private const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}