package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.models.playlist.PlaylistSongItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.view.AlbumArtistItem
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.models.view.ArtistItem
import com.prosabdev.common.models.view.ComposerItem
import com.prosabdev.common.models.view.FolderItem
import com.prosabdev.common.models.view.GenreItem
import com.prosabdev.common.models.view.YearItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SleepTimerSP
import com.prosabdev.common.utils.FormattersAndParsers
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.common.utils.IntentActionsManager
import com.prosabdev.common.utils.PermissionsManager
import com.prosabdev.common.utils.SystemSettings
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetSleepTimerBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentForFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import kotlinx.coroutines.*

class PlayerMoreFullBottomSheetDialog : BottomSheetDialogFragment() {

    private var mDataBiding: BottomSheetPlayerMoreBinding? = null

    private val mPlaylistItemViewModel: PlaylistItemViewModel by viewModels()

    private var mPlayingNowFragmentViewModel: PlayingNowFragmentViewModel? = null
    private var mMainFragmentViewModel: MainFragmentViewModel? = null
    private var mScreenShotPlayerView : View? = null

    private var mPlaylists: ArrayList<PlaylistItem> = ArrayList()
    private var mDefaultPlaylistCount: Long = 0

    private var mAlbumItem: AlbumItem? = null
    private var mAlbumArtistItem: AlbumArtistItem? = null
    private var mArtistItem: ArtistItem? = null
    private var mComposerItem: ComposerItem? = null
    private var mFolderItem: FolderItem? = null
    private var mGenreItem: GenreItem? = null
    private var mYearItem: YearItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player_more, container, false)
        val view = mDataBiding.root

        //Load your UI content
        initViews()
        observeLiveData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCurrentPlayingSongUI(mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value)
        checkInteractions()
    }

    private fun observeLiveData() {
        MainScope().launch {
            mPlaylistItemViewModel.getAll()?.observe(viewLifecycleOwner){
                mPlaylists = it as ArrayList<PlaylistItem>
                mPlaylists.reverse()
                this.cancel(null)
            }
            mDefaultPlaylistCount = mPlaylistItemViewModel.getMaxIdLikeName(PlaylistAddFullBottomSheetDialogFragment.DEFAULT_PLAYLIST_NAME) ?: 0
        }
    }

    private fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        if(songItem == null)
            return
        context?.let { ctx ->
            mDataBiding?.let { dataBidingView ->
                dataBidingView.textTitle.text = songItem.title?.ifEmpty { songItem.fileName } ?: songItem.fileName
                dataBidingView.textArtist.text = songItem.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)

                dataBidingView.textDescription.text =
                    ctx.getString(
                        R.string.item_song_card_text_details,
                        FormattersAndParsers.formatSongDurationToString(songItem.duration ),
                        songItem.typeMime
                    )

                val tempUri: Uri? = Uri.parse(songItem.uri)
                val imageRequest: ImageLoaders.ImageRequestItem = ImageLoaders.ImageRequestItem.newOriginalCardInstance()
                imageRequest.uri = tempUri
                imageRequest.imageView = dataBidingView.covertArt
                imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                ImageLoaders.startExploreContentImageLoaderJob(ctx, imageRequest)
            }
        }
    }

    private fun checkInteractions() {
        mDataBiding?.let { dataBidingView ->
            dataBidingView.buttonInfo.setOnClickListener {
                showSongInfoDialog()
            }
            dataBidingView.buttonPlaylistAdd.setOnClickListener {
                showAddToPlaylistDialog()
            }
            dataBidingView.buttonLyrics.setOnClickListener {
                fetchLyrics()
            }
            dataBidingView.buttonShare.setOnClickListener {
                shareSong()
            }
            dataBidingView.buttonTimer.setOnClickListener {
                showSleepTimerDialog()
            }
            dataBidingView.buttonGoto.setOnClickListener {
                showGoToSongDialog()
            }
            dataBidingView.buttonSetAs.setOnClickListener {
                setSongAsRingtone()
            }
            dataBidingView.buttonDelete.setOnClickListener {
                showDeleteSelectionDialog()
            }
        }
    }

    private fun showDeleteSelectionDialog() {
        val tempSongItem: SongItem =
            mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        context?.let { ctx ->
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(ctx.getString(R.string.dialog_delete_selection_title))
                .setIcon(R.drawable.delete)
                .setMessage(ctx.getString(R.string.dialog_delete_selection_description))
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(ctx.getString(R.string.delete_file)) { _, _ ->
                    deleteSelectedSongs(tempSongItem)
                }
                .show()
        }
        dismiss()
    }
    private fun deleteSelectedSongs(songItem: SongItem) {
        //
    }

    private fun setSongAsRingtone() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        context?.let { ctx ->
            if(PermissionsManager.haveWriteSystemSettingsPermission(ctx)){
                val tempUri : Uri = Uri.parse(tempSongItem.uri ?: "") ?: return
                SystemSettings.setRingtone(ctx, tempUri, tempSongItem.fileName, true)
            }else{
                MaterialAlertDialogBuilder(this.requireContext())
                    .setTitle(ctx.getString(R.string.set_as_ringtone))
                    .setIcon(R.drawable.ring_volume)
                    .setMessage(ctx.getString(R.string.Allow_Fluid_Music_to_modify_audio_settings))
                    .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(ctx.getString(R.string.lets_go)) { dialog, _ ->
                        activity?.let { scopeActivity ->
                            PermissionsManager.requestWriteSystemSettingsPermission(
                                scopeActivity
                            )
                        }
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        dismiss()
    }

    private fun showGoToSongDialog() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return

        val mDialogGotoSongBinding : DialogGotoSongBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_goto_song, null, false)
        val tempFragmentManager = activity?.supportFragmentManager ?: return
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.go_to))
                .setIcon(R.drawable.link)
                .setView(mDialogGotoSongBinding.root)
                .setPositiveButton(ctx.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show().apply {
                    mDialogGotoSongBinding.buttonAlbum.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = AlbumItem.castDataItemToGeneric(context, mAlbumItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM,
                                    AlbumsFragment.TAG,
                                    AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.album,
                                    mAlbumItem?.uriImage,
                                    mAlbumItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(AlbumsFragment.TAG)
                        }
                    }
                    mDialogGotoSongBinding.buttonAlbumArtist.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = AlbumArtistItem.castDataItemToGeneric(context, mAlbumArtistItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST,
                                    AlbumArtistsFragment.TAG,
                                    AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.albumArtist,
                                    mAlbumArtistItem?.uriImage,
                                    mAlbumArtistItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(AlbumArtistsFragment.TAG)
                        }
                    }
                    mDialogGotoSongBinding.buttonArtist.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = ArtistItem.castDataItemToGeneric(context, mArtistItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST,
                                    ArtistsFragment.TAG,
                                    ArtistItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.artist,
                                    mArtistItem?.uriImage,
                                    mArtistItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(ArtistsFragment.TAG)
                        }
                    }
                    mDialogGotoSongBinding.buttonComposer.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = ComposerItem.castDataItemToGeneric(context, mComposerItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER,
                                    ComposersFragment.TAG,
                                    ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.composer,
                                    mComposerItem?.uriImage,
                                    mComposerItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(ComposersFragment.TAG)
                        }
                    }
                    mDialogGotoSongBinding.buttonFolder.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = FolderItem.castDataItemToGeneric(context, mFolderItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER,
                                    FoldersFragment.TAG,
                                    FolderItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.folder,
                                    mFolderItem?.uriImage,
                                    mFolderItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(FoldersFragment.TAG)
                        }
                    }
                    mDialogGotoSongBinding.buttonGenre.setOnClickListener{
                        this.dismiss()
                        val temGenericItem = GenreItem.castDataItemToGeneric(context, mGenreItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentForFragment.newInstance(
                                    PersistentStorage
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE,
                                    GenresFragment.TAG,
                                    GenreItem.INDEX_COLUM_TO_SONG_ITEM,
                                    tempSongItem.genre,
                                    mGenreItem?.uriImage,
                                    mGenreItem?.hashedCovertArtSignature ?: -1,
                                    temGenericItem?.title,
                                    temGenericItem?.subtitle,
                                    temGenericItem?.details
                                )
                            )
                            addToBackStack(GenresFragment.TAG)
                        }
                    }
//                    mDialogGotoSongBinding.buttonYear.setOnClickListener{
//                        this.dismiss()
//                        val temGenericItem = YearItem.castDataItemToGeneric(context, mYearItem, true)
//                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
//                        tempFragmentManager.commit {
//                            setReorderingAllowed(true)
//                            add(
//                                R.id.main_fragment_container,
//                                ExploreContentForFragment.newInstance(
//                                    SharedPreferenceManagerUtils
//                                        .SortAnOrganizeForExploreContents
//                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR,
//                                    YearsFragment.TAG,
//                                    YearItem.INDEX_COLUM_TO_SONG_ITEM,
//                                    tempSongItem.genre,
//                                    mYearItem?.uriImage,
//                                    mYearItem?.hashedCovertArtSignature ?: -1,
//                                    temGenericItem?.title,
//                                    temGenericItem?.subtitle,
//                                    temGenericItem?.details
//                                )
//                            )
//                            addToBackStack(YearsFragment.TAG)
//                        }
//                    }
                }
        }
        dismiss()
    }

    private fun showSleepTimerDialog() {
        val mDialogSetSleepTimerBinding : DialogSetSleepTimerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_set_sleep_timer, null, false)

        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.sleep_timer))
                .setIcon(R.drawable.timer)
                .setView(mDialogSetSleepTimerBinding.root)
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(ctx.getString(R.string.ok)) { _, _ ->
                    saveSleepTimer(
                        mDialogSetSleepTimerBinding.slider.value,
                        mDialogSetSleepTimerBinding.checkboxPlayLastSong.isChecked
                    )
                }
                .show().apply {
                    val tempSleepTimer: Float = mPlayingNowFragmentViewModel?.sleepTimer?.value?.sliderValue ?: 0.0f
                    val tempPlayLastSong: Boolean = mPlayingNowFragmentViewModel?.sleepTimer?.value?.playLastSong ?: false
                    mDialogSetSleepTimerBinding.slider.value = tempSleepTimer
                    mDialogSetSleepTimerBinding.textRangeValue.text =
                        if(tempSleepTimer <= 0)
                            ctx.getString(R.string.disabled)
                        else
                            ctx.getString(R.string._timer_range_value, tempSleepTimer.toInt())
                    mDialogSetSleepTimerBinding.checkboxPlayLastSong.isChecked = tempPlayLastSong

                    mDialogSetSleepTimerBinding.slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                        mDialogSetSleepTimerBinding.textRangeValue.text =
                            if(value <= 0)
                                ctx.getString(R.string.disabled)
                            else
                                ctx.getString(R.string._timer_range_value, value.toInt())
                    })
                }
        }

        dismiss()
    }

    private fun saveSleepTimer(value: Float, playLastSong: Boolean = false) {
        val tempSleepTimerSP = SleepTimerSP()
        tempSleepTimerSP.sliderValue = value
        tempSleepTimerSP.playLastSong = playLastSong
        mPlayingNowFragmentViewModel?.sleepTimer?.value = tempSleepTimerSP
    }

    private fun shareSong() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        context?.let { ctx ->
            val dialogShareSongBinding : DialogShareSongBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_share_song, null, false)
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(ctx.getString(R.string.share_song))
                .setIcon(R.drawable.share)
                .setView(dialogShareSongBinding.root)
                .setNegativeButton(ctx.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show().apply {
                    dialogShareSongBinding.buttonShareFile.setOnClickListener{
                        MainScope().launch {
                            withContext(Dispatchers.IO){
                                val tempUri = Uri.parse(tempSongItem.uri ?: "") ?: return@withContext
                                val tempDesc =
                                    if(tempSongItem.title != null)
                                        ctx.getString(R.string._music_content_by, tempSongItem.title, tempSongItem.artist ?: "")
                                    else
                                        "${tempSongItem.fileName}"
                                IntentActionsManager.shareSongFile(ctx, tempUri, tempDesc)
                            }
                        }
                        this@apply.dismiss()
                    }
                    dialogShareSongBinding.buttonShareScreenshot.setOnClickListener{
                        MainScope().launch {
                            dialogShareSongBinding.buttonShareScreenshot.alpha = 0.4f
                            dialogShareSongBinding.buttonShareScreenshot.isClickable = false
                            dialogShareSongBinding.buttonShareScreenshot.isFocusable = false
                            withContext(Dispatchers.Default){
                                val tempBitmap = getScreenShotOfView(mScreenShotPlayerView)
                                val tempDesc = ctx.getString(R.string.currently_listening_icon)
                                IntentActionsManager.shareBitmapImage(ctx, tempBitmap, tempDesc)
                                MainScope().launch {
                                    dialogShareSongBinding.buttonShareScreenshot.alpha = 1.0f
                                    dialogShareSongBinding.buttonShareScreenshot.isClickable = true
                                    dialogShareSongBinding.buttonShareScreenshot.isFocusable = true
                                }
                            }
                            this@apply.dismiss()
                        }
                    }
                }
        }
        dismiss()
    }
    private fun getScreenShotOfView(view: View?): Bitmap? {
        return view?.rootView?.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun fetchLyrics() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
    }

    private fun showAddToPlaylistDialog() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return

        val songsToAddOnPlaylist : ArrayList<PlaylistSongItem> = ArrayList()
        val psI = PlaylistSongItem()
        psI.songUri = tempSongItem.uri
        psI.lastAddedDateToLibrary = SystemSettings.getCurrentDateInMillis()
        songsToAddOnPlaylist.add(psI)

        val playlistAddBottomSheetDialog = PlaylistAddFullBottomSheetDialogFragment.newInstance(
            songsToAddOnPlaylist,
            mPlaylists,
            mDefaultPlaylistCount
        )
        activity ?.supportFragmentManager?.let {
            playlistAddBottomSheetDialog.show(
                it,
                PlaylistAddFullBottomSheetDialogFragment.TAG
            )
        }
        dismiss()
    }

    private fun showSongInfoDialog() {
        val tempSongItem : SongItem = mPlayingNowFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        val songInfoBottomSheetDialog = SongInfoFullBottomSheetDialogFragment.newInstance(tempSongItem)
        activity ?.supportFragmentManager?.let { songInfoBottomSheetDialog.show(it, SongInfoFullBottomSheetDialogFragment.TAG) }
        dismiss()
    }
    private fun initViews() {
        mDataBiding?.textTitle?.isSelected = true
        mDataBiding?.textDescription?.isSelected = true
    }

    fun updateData(playingNowFragmentViewModel : PlayingNowFragmentViewModel, mainFragmentViewModel : MainFragmentViewModel, screenShootPlayerView : View){
        mPlayingNowFragmentViewModel = playingNowFragmentViewModel
        mMainFragmentViewModel = mainFragmentViewModel
        mScreenShotPlayerView = screenShootPlayerView
    }
    fun updateContentExploreDetails(
        albumItem: AlbumItem,
        albumArtistItem: AlbumArtistItem,
        artistItem: ArtistItem,
        composerItem: ComposerItem,
        folderItem: FolderItem,
        genreItem: GenreItem,
        yearItem: YearItem,
    ){
        mAlbumItem = albumItem
        mAlbumArtistItem = albumArtistItem
        mArtistItem = artistItem
        mComposerItem = composerItem
        mFolderItem = folderItem
        mGenreItem = genreItem
        mYearItem = yearItem
    }

    companion object {
        const val TAG = "PlayerMoreFullBottomSheetDialog"

        @JvmStatic
        fun newInstance() =
            PlayerMoreFullBottomSheetDialog().apply {
            }
    }
}