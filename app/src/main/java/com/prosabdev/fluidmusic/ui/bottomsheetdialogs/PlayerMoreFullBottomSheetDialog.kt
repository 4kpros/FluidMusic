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
import com.prosabdev.common.utils.IntentActionsManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetSleepTimerBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import kotlinx.coroutines.*

class PlayerMoreFullBottomSheetDialog : BottomSheetDialogFragment() {

    private var mDataBidingView: BottomSheetPlayerMoreBinding? = null

    private val mPlaylistItemViewModel: PlaylistItemViewModel by viewModels()

    private var mNowPlayingFragmentViewModel: NowPlayingFragmentViewModel? = null
    private var mMainFragmentViewModel: MainFragmentViewModel? = null
    private var mScreenShotPlayerView : View? = null

    private var mPlaylists: ArrayList<com.prosabdev.common.models.playlist.PlaylistItem> = ArrayList()
    private var mDefaultPlaylistCount: Long = 0

    private var mAlbumItem: com.prosabdev.common.models.view.AlbumItem? = null
    private var mAlbumArtistItem: com.prosabdev.common.models.view.AlbumArtistItem? = null
    private var mArtistItem: com.prosabdev.common.models.view.ArtistItem? = null
    private var mComposerItem: com.prosabdev.common.models.view.ComposerItem? = null
    private var mFolderItem: com.prosabdev.common.models.view.FolderItem? = null
    private var mGenreItem: com.prosabdev.common.models.view.GenreItem? = null
    private var mYearItem: com.prosabdev.common.models.view.YearItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player_more, container, false)
        val view = mDataBidingView?.root

        initViews()
        observeLiveData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCurrentPlayingSongUI(mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value)
        checkInteractions()
    }

    private fun observeLiveData() {
        MainScope().launch {
            mPlaylistItemViewModel.getAll()?.observe(viewLifecycleOwner){
                mPlaylists = it as ArrayList<com.prosabdev.common.models.playlist.PlaylistItem>
                mPlaylists.reverse()
                this.cancel(null)
            }
            mDefaultPlaylistCount = mPlaylistItemViewModel.getMaxIdLikeName(PlaylistAddFullBottomSheetDialogFragment.DEFAULT_PLAYLIST_NAME) ?: 0
        }
    }

    private fun updateCurrentPlayingSongUI(songItem: com.prosabdev.common.models.songitem.SongItem?) {
        if(songItem == null)
            return
        context?.let { ctx ->
            mDataBidingView?.let { dataBidingView ->
                dataBidingView.textTitle.text = songItem.title?.ifEmpty { songItem.fileName } ?: songItem.fileName
                dataBidingView.textArtist.text = songItem.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)

                dataBidingView.textDescription.text =
                    ctx.getString(
                        R.string.item_song_card_text_details,
                        com.prosabdev.common.utils.FormattersAndParsersUtils.formatSongDurationToString(songItem.duration ),
                        songItem.typeMime
                    )

                val tempUri: Uri? = Uri.parse(songItem.uri)
                val imageRequest: com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem = com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
                imageRequest.uri = tempUri
                imageRequest.imageView = dataBidingView.covertArt
                imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                com.prosabdev.common.utils.ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
            }
        }
    }

    private fun checkInteractions() {
        mDataBidingView?.let { dataBidingView ->
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
        val tempSongItem: com.prosabdev.common.models.songitem.SongItem =
            mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
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
    private fun deleteSelectedSongs(songItem: com.prosabdev.common.models.songitem.SongItem) {
        //
    }

    private fun setSongAsRingtone() {
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        context?.let { ctx ->
            if(com.prosabdev.common.utils.PermissionsManagerUtils.haveWriteSystemSettingsPermission(ctx)){
                val tempUri : Uri = Uri.parse(tempSongItem.uri ?: "") ?: return
                com.prosabdev.common.utils.SystemSettingsUtils.setRingtone(ctx, tempUri, tempSongItem.fileName, true)
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
                            com.prosabdev.common.utils.PermissionsManagerUtils.requestWriteSystemSettingsPermission(
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
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return

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
                        val temGenericItem = com.prosabdev.common.models.view.AlbumItem.castDataItemToGeneric(context, mAlbumItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM,
                                    AlbumsFragment.TAG,
                                    com.prosabdev.common.models.view.AlbumItem.INDEX_COLUM_TO_SONG_ITEM,
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
                        val temGenericItem = com.prosabdev.common.models.view.AlbumArtistItem.castDataItemToGeneric(context, mAlbumArtistItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST,
                                    AlbumArtistsFragment.TAG,
                                    com.prosabdev.common.models.view.AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM,
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
                        val temGenericItem = com.prosabdev.common.models.view.ArtistItem.castDataItemToGeneric(context, mArtistItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST,
                                    ArtistsFragment.TAG,
                                    com.prosabdev.common.models.view.ArtistItem.INDEX_COLUM_TO_SONG_ITEM,
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
                        val temGenericItem = com.prosabdev.common.models.view.ComposerItem.castDataItemToGeneric(context, mComposerItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER,
                                    ComposersFragment.TAG,
                                    com.prosabdev.common.models.view.ComposerItem.INDEX_COLUM_TO_SONG_ITEM,
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
                        val temGenericItem = com.prosabdev.common.models.view.FolderItem.castDataItemToGeneric(context, mFolderItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER,
                                    FoldersFragment.TAG,
                                    com.prosabdev.common.models.view.FolderItem.INDEX_COLUM_TO_SONG_ITEM,
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
                        val temGenericItem = com.prosabdev.common.models.view.GenreItem.castDataItemToGeneric(context, mGenreItem, true)
                        mMainFragmentViewModel?.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(true)
                            add(
                                R.id.main_fragment_container,
                                ExploreContentsForFragment.newInstance(
                                    com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
                                        .SortAnOrganizeForExploreContents
                                        .SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE,
                                    GenresFragment.TAG,
                                    com.prosabdev.common.models.view.GenreItem.INDEX_COLUM_TO_SONG_ITEM,
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
//                                ExploreContentsForFragment.newInstance(
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
                    val tempSleepTimer: Float = mNowPlayingFragmentViewModel?.getSleepTimer()?.value?.sliderValue ?: 0.0f
                    val tempPlayLastSong: Boolean = mNowPlayingFragmentViewModel?.getSleepTimer()?.value?.playLastSong ?: false
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
        val tempSleepTimerSP = com.prosabdev.common.sharedprefs.models.SleepTimerSP()
        tempSleepTimerSP.sliderValue = value
        tempSleepTimerSP.playLastSong = playLastSong
        mNowPlayingFragmentViewModel?.setSleepTimer(tempSleepTimerSP)
    }

    private fun shareSong() {
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
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
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
    }

    private fun showAddToPlaylistDialog() {
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return

        val songsToAddOnPlaylist : ArrayList<com.prosabdev.common.models.playlist.PlaylistSongItem> = ArrayList()
        val psI = com.prosabdev.common.models.playlist.PlaylistSongItem()
        psI.songUri = tempSongItem.uri
        psI.lastAddedDateToLibrary = com.prosabdev.common.utils.SystemSettingsUtils.getCurrentDateInMilli()
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
        val tempSongItem : com.prosabdev.common.models.songitem.SongItem = mNowPlayingFragmentViewModel?.getCurrentPlayingSong()?.value ?: return
        val songInfoBottomSheetDialog = SongInfoFullBottomSheetDialogFragment.newInstance(tempSongItem)
        activity ?.supportFragmentManager?.let { songInfoBottomSheetDialog.show(it, SongInfoFullBottomSheetDialogFragment.TAG) }
        dismiss()
    }
    private fun initViews() {
        mDataBidingView?.textTitle?.isSelected = true
        mDataBidingView?.textDescription?.isSelected = true
    }

    fun updateData(nowPlayingFragmentViewModel : NowPlayingFragmentViewModel, mainFragmentViewModel : MainFragmentViewModel, screenShootPlayerView : View){
        mNowPlayingFragmentViewModel = nowPlayingFragmentViewModel
        mMainFragmentViewModel = mainFragmentViewModel
        mScreenShotPlayerView = screenShootPlayerView
    }
    fun updateContentExploreDetails(
        albumItem: com.prosabdev.common.models.view.AlbumItem,
        albumArtistItem: com.prosabdev.common.models.view.AlbumArtistItem,
        artistItem: com.prosabdev.common.models.view.ArtistItem,
        composerItem: com.prosabdev.common.models.view.ComposerItem,
        folderItem: com.prosabdev.common.models.view.FolderItem,
        genreItem: com.prosabdev.common.models.view.GenreItem,
        yearItem: com.prosabdev.common.models.view.YearItem,
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