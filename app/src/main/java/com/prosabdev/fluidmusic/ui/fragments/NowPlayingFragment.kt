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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NowPlayingFragment : Fragment() {

    private var mDataBiding: FragmentNowPlayingBinding? = null

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

    private var mPlayingNowPageAdapter: PlayingNowPageAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater,R.layout.fragment_now_playing, container,false)
        val view = mDataBiding?.root

        //Load your UI content
        runBlocking {
            loadLastPlayerSession()
            initViews()
            setupViewPagerAdapter()
            checkInteractions()
            observeLiveData()
        }

        return view
    }

    override fun onDestroy() {
        lifecycleScope.launch {
            saveCurrentPlayingSession()
        }
        super.onDestroy()
    }

    private suspend fun saveCurrentPlayingSession() {
        context?.let { ctx ->
            PersistentStorage.PlayingNow.saveRecentSong(
                mNowPlayingFragmentViewModel.mediaDescription.value, 0
            )
            PersistentStorage.PlayingNow.savePlayingProgressValue(
                mNowPlayingFragmentViewModel.getPlayingProgressValue().value
            )
            PersistentStorage.PlayingNow.saveQueueListSource(
                mNowPlayingFragmentViewModel.getQueueListSource().value
            )
            PersistentStorage.PlayingNow.saveQueueListSourceColumnIndex(
                mNowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value
            )
            PersistentStorage.PlayingNow.saveQueueListSourceColumnValue(
                mNowPlayingFragmentViewModel.getQueueListSourceColumnValue().value
            )
            PersistentStorage.PlayingNow.saveRepeat(
                mNowPlayingFragmentViewModel.getRepeat().value
            )
            PersistentStorage.PlayingNow.saveShuffle(
                mNowPlayingFragmentViewModel.getShuffle().value
            )
            PersistentStorage.PlayingNow.saveSleepTimer(
                mNowPlayingFragmentViewModel.getSleepTimer().value
            )
            val sortOrganize = SortOrganizeItemSP()
            sortOrganize.sortOrderBy = mNowPlayingFragmentViewModel.getSortBy().value ?: SORT_LIST_GRID_DEFAULT_VALUE
            sortOrganize.isInvertSort = mNowPlayingFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
            PersistentStorage.SortAnOrganizeForExploreContents.saveSortOrganizeItemsFor(
                PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYER_QUEUE_MUSIC,
                sortOrganize
            )
        }
    }

    private suspend fun loadLastPlayerSession() {
        context?.let { ctx ->
            val sortOrganize: SortOrganizeItemSP? = PersistentStorage.SortAnOrganizeForExploreContents
                .loadSortOrganizeItemsFor(
                    PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYER_QUEUE_MUSIC
                )
            val queueListSource: String? = PersistentStorage.PlayingNow.loadQueueListSource(null)
            val queueListSourceColumnIndex: String? = PersistentStorage.PlayingNow.loadQueueListSourceColumnIndex()
            val queueListSourceColumnValue: String? = PersistentStorage.PlayingNow.loadQueueListSourceColumnValue()

            mNowPlayingFragmentViewModel.queueListSource.value = queueListSource ?: AllSongsFragment.TAG
            mNowPlayingFragmentViewModel.queueListSourceColumnIndex.value = queueListSourceColumnIndex
            mNowPlayingFragmentViewModel.queueListSourceColumnValue.value = queueListSourceColumnValue
            mNowPlayingFragmentViewModel.sortBy.value = sortOrganize?.sortOrderBy ?: SORT_LIST_GRID_DEFAULT_VALUE
            mNowPlayingFragmentViewModel.isInverted. value = sortOrganize?.isInvertSort ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE

            loadDirectlyQueueMusicListFromDatabase(
                sortOrganize?.sortOrderBy ?: SORT_LIST_GRID_DEFAULT_VALUE,
                sortOrganize?.isInvertSort ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE,
                queueListSource ?: AllSongsFragment.TAG,
                queueListSourceColumnIndex,
                queueListSourceColumnValue
            )
            val songItem: SongItem? = PersistentStorage.PlayingNow.loadRecentSong()
            val progressValue: Long = PersistentStorage.PlayingNow.loadPlayingProgressValue()
            val repeat: Int = PersistentStorage.PlayingNow.loadRepeat()
            val shuffle: Int = PersistentStorage.PlayingNow.loadShuffle()
            val sleepTimer: SleepTimerSP? = PersistentStorage.PlayingNow.loadSleepTimer()

            if(songItem?.uri == null){
                tryToGetFirstSong()
            }else{
                val tempSongItem : SongItem? =
                    mSongItemViewModel.getAtUri(songItem.uri)
                if(tempSongItem?.uri == null){
                    tryToGetFirstSong()
                }else{
                    tempSongItem.position = songItem.position
                    mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
                    mNowPlayingFragmentViewModel.setPlayingProgressValue(progressValue)
                    mNowPlayingFragmentViewModel.setIsPlaying(false)
                    mNowPlayingFragmentViewModel.setRepeatMode(repeat)
                    mNowPlayingFragmentViewModel.setShuffleMode(shuffle)
                    mNowPlayingFragmentViewModel.setSleepTimer(sleepTimer)
                    mNowPlayingFragmentViewModel.setSleepTimerStateStarted(false)
                }
            }
        }
    }
    private suspend fun tryToGetFirstSong() {
        context?.let { ctx ->
            withContext(Dispatchers.IO) {
                mSongItemViewModel.getFirstSong().apply {
                    PersistentStorage.PlayingNow.saveQueueListSource(
                        AllSongsFragment.TAG
                    )
                    PersistentStorage.PlayingNow.saveQueueListSourceColumnIndex(null)
                    PersistentStorage.PlayingNow.saveQueueListSourceColumnValue(null)
                    PersistentStorage.PlayingNow.saveRepeat(
                        PlaybackState.REPEAT_MODE_NONE
                    )
                    PersistentStorage.PlayingNow.saveShuffle(
                        PlaybackState.SHUFFLE_MODE_NONE
                    )
                    PersistentStorage.PlayingNow.saveSleepTimer(ctx, null)
                    PersistentStorage.PlayingNow.savePlayingProgressValue(ctx, 0)

                    if (this@apply?.uri == null) {
                        mNowPlayingFragmentViewModel.setCurrentPlayingSong(null)
                        PersistentStorage.PlayingNow.saveCurrentPlayingSong(null)
                    } else {
                        mNowPlayingFragmentViewModel.setCurrentPlayingSong(this@apply)
                        PersistentStorage.PlayingNow.saveCurrentPlayingSong(this@apply)
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
                val songList = mSongItemViewModel.getAllDirectly(sortOrderBy)
                updateEmptyListUI(songList?.size ?: 0)
                mPlayingNowPageAdapter?.submitList(
                    if(isInvertSort) songList?.reversed() else songList
                )
                mQueueMusicBottomSheetDialog.updateQueueMusicList(
                    if(isInvertSort) songList?.reversed() else songList
                )
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
                val songList = mSongItemViewModel.getAllDirectlyWhereEqual(
                    queueListSourceColumnIndex,
                    queueListSourceColumnValue,
                    sortOrderBy
                )
                updateEmptyListUI(songList?.size ?: 0)
                mPlayingNowPageAdapter?.submitList(
                    if(isInvertSort) songList?.reversed() else songList
                )
                mQueueMusicBottomSheetDialog.updateQueueMusicList(
                    if(isInvertSort) songList?.reversed() else songList
                )
            }
        }
    }

    private fun updateEmptyListUI(size: Int) {
        mDataBiding?.let { dataBidingView ->
            if(size > 0){
                dataBidingView.linearRescanDeviceContainer.visibility = GONE
            }else{
                dataBidingView.linearRescanDeviceContainer.visibility = VISIBLE
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
        if((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val tempSongPosition = MathComputations.randomExcluded(mDataBiding?.viewPagerPlayer?.currentItem ?: -1, mPlayingNowPageAdapter?.itemCount ?: 0)
        if(tempSongPosition >= (mPlayingNowPageAdapter?.itemCount ?: 0) || tempSongPosition < 0)return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempSongPosition))
            ?: return
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setShuffle(PlaybackState.SHUFFLE_MODE_ALL)
        mNowPlayingFragmentViewModel.setRepeat(PlaybackState.REPEAT_MODE_NONE)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun tryToPlaySongAtFromRequest(playSongAtRequest: PlaySongAtRequest?) {
        if (playSongAtRequest == null) return
        if(playSongAtRequest.position < 0 || playSongAtRequest.position > (mPlayingNowPageAdapter?.currentList?.size ?: 0)) return

        val tempCurrentSongRequest = mPlayingNowPageAdapter?.currentList?.get(playSongAtRequest.position)
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
        val queueListSource: String = mNowPlayingFragmentViewModel.getQueueListSource().value ?: AllSongsFragment.TAG
        val tempIsInverted : Boolean = mNowPlayingFragmentViewModel.getIsInverted().value ?: IS_INVERTED_LIST_GRID_DEFAULT_VALUE
        val songList = getNewSongsForSource(queueListSource)
        //Update UI
        updateEmptyListUI(songList.size)
        mPlayingNowPageAdapter?.submitList(
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
                                        view.AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.AlbumArtistItem).name,
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
                                        view.AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.AlbumItem).name,
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
                                        view.ArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.ArtistItem).name,
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
                                        view.ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.ComposerItem).name,
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
                                        view.FolderItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.FolderItem).name,
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
                                        view.GenreItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.GenreItem).name,
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
                                        view.YearItem.INDEX_COLUM_TO_SONG_ITEM,
                                        (dataList[i] as view.YearItem).name,
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
        mDataBiding?.let { dataBidingView ->
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
    private fun updateShuffleUI(shuffle: Int?) {
        mDataBiding?.let { dataBidingView ->
            context?.let {
                when (shuffle) {
                    PlaybackState.SHUFFLE_MODE_ALL -> {
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
    private fun updateProgressValueUI(currentDuration: Long) {
        mDataBiding?.let { dataBidingView ->
            context?.let {
                val totalDuration: Long =
                    mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                dataBidingView.slider.value =
                    FormattersAndParsers.formatSongDurationToSliderProgress(
                        currentDuration,
                        totalDuration
                    )
                dataBidingView.textDurationCurrent.text =
                    FormattersAndParsers.formatSongDurationToString(currentDuration)
            }
        }
    }
    private fun updatePlaybackStateUI(isPlaying: Boolean?) {
        mDataBiding?.let { dataBidingView ->
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
        mDataBiding?.let { dataBidingView ->
            val tempOldPosition: Int = dataBidingView.viewPagerPlayer.currentItem
            val tempOldSongItem = mPlayingNowPageAdapter?.currentList?.get(tempOldPosition)
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
        mDataBiding?.let { dataBidingView ->
            context?.let { ctx ->
                val tempUri: Uri = Uri.parse(songItem?.uri ?: "")
                val imageRequestBlurred: ImageLoaders.ImageRequestItem = ImageLoaders.ImageRequestItem.newBlurInstance()
                imageRequestBlurred.uri = tempUri
                imageRequestBlurred.hashedCovertArtSignature = songItem?.hashedCovertArtSignature ?: -1
                imageRequestBlurred.imageView = dataBidingView.blurredImageview
                ImageLoaders.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
            }
        }
    }
    private fun updateTextTitleSubtitleDurationUI(songItem: SongItem?) {
        mDataBiding?.let { dataBidingView ->
            context?.let { ctx ->
                dataBidingView.textTitle.text =
                    songItem?.title?.ifEmpty { songItem.fileName ?: ctx.getString(R.string.unknown_title) } ?: songItem?.fileName ?: ctx.getString(R.string.unknown_title)
                dataBidingView.textArtist.text =
                    songItem?.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)
                dataBidingView.textDuration.text =
                    FormattersAndParsers.formatSongDurationToString(
                        songItem?.duration ?: 0
                    )
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mDataBiding?.let { dataBidingView ->
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
        mDataBiding?.let { dataBidingView ->
            var viewpagerScrollState =  ViewPager2.SCROLL_STATE_IDLE
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
        val currentItem = mDataBiding?.viewPagerPlayer?.currentItem
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
        mDataBiding?.viewPagerPlayer?.setCurrentItem(tempCurrentSongItem?.position ?: 0, false)
    }

    private fun showQueueMusicDialog() {
        if(!mQueueMusicBottomSheetDialog.isVisible) {
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
        if(!mPlayerMoreBottomSheetDialog.isVisible) {
            activity?.supportFragmentManager?.let {
                mPlayerMoreBottomSheetDialog.show(it, PlayerMoreFullBottomSheetDialog.TAG)
            }
        }
    }
    private fun onRepeatButtonClicked(){
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
    private fun onShuffleButtonClicked(){
        val tempShuffleValue: Int = mNowPlayingFragmentViewModel.getShuffle().value ?: PlaybackState.SHUFFLE_MODE_NONE
        if(tempShuffleValue == PlaybackState.SHUFFLE_MODE_NONE){
            mNowPlayingFragmentViewModel.setShuffle(PlaybackState.SHUFFLE_MODE_ALL)
        }else if(tempShuffleValue == PlaybackState.SHUFFLE_MODE_ALL){
            mNowPlayingFragmentViewModel.setShuffle(PlaybackState.SHUFFLE_MODE_NONE)
        }
    }
    private fun onPrevPageButtonClicked(){
        if((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition - 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun onNextPageButtonClicked(){
        if((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val tempPosition : Int = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.position ?: return
        val tempSongItem : SongItem = getCurrentPlayingSongFromPosition((tempPosition + 1))
            ?: return
        mNowPlayingFragmentViewModel.setCanScrollSmoothViewpager(true)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(0)
        mNowPlayingFragmentViewModel.setCurrentPlayingSong(tempSongItem)
    }
    private fun getCurrentPlayingSongFromPosition(position: Int?): SongItem? {
        if(position == null) return null
        if (position < 0 || position >= (mPlayingNowPageAdapter?.currentList?.size ?: 0)) return null
        val tempSongItem: SongItem = mPlayingNowPageAdapter?.currentList?.get(position) ?: return null
        tempSongItem.position = position
        return tempSongItem
    }
    private fun onPlayPauseButtonClicked(){
        mNowPlayingFragmentViewModel.toggleIsPlaying()
    }
    private fun updateOnStopTrackingTouch(value: Float) {
        if((mPlayingNowPageAdapter?.currentList?.size ?: 0) <= 0) return
        val totalDuration : Long = mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
        val tempProgress : Long = FormattersAndParsers.formatSliderProgressToLongDuration(value, totalDuration)
        mNowPlayingFragmentViewModel.setPlayingProgressValue(tempProgress)
    }
    private fun onViewpagerPageChanged(position: Int) {
        if((mDataBiding?.viewPagerPlayer?.adapter?.itemCount ?: 0) < 1) return
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
        mDataBiding?.let { dataBidingView ->
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
            NowPlayingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}