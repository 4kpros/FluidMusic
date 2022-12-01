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
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetSleepTimerBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistSongItemViewModel
import kotlinx.coroutines.*

class PlayerMoreFullBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var mBottomSheetPlayerMoreBinding: BottomSheetPlayerMoreBinding

    private val mPlaylistItemViewModel: PlaylistItemViewModel by viewModels()

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel
    private lateinit var mMainFragmentViewModel: MainFragmentViewModel
    private lateinit var mScreenShotPlayerView : View

    private var mPlaylists: ArrayList<PlaylistItem> = ArrayList()
    private var mDefaultPlaylistCount: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBottomSheetPlayerMoreBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player_more, container, false)
        val view = mBottomSheetPlayerMoreBinding.root

        initViews()
        observeLiveData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            updateCurrentPlayingSongUI(mPlayerFragmentViewModel.getCurrentPlayingSong().value)
            checkInteractions()
        }
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

    private suspend fun updateCurrentPlayingSongUI(songItem: SongItem?) {
        if(songItem == null)
            return
        context?.let { ctx ->
            MainScope().launch {
                mBottomSheetPlayerMoreBinding.textTitle.text =
                    if(
                        songItem.title != null && songItem.title!!.isNotEmpty()
                    )
                        songItem.title
                    else
                        songItem.fileName

                mBottomSheetPlayerMoreBinding.textArtist.text =
                    if(songItem.artist != null && songItem.artist!!.isNotEmpty())
                        songItem.artist
                    else
                        ctx.getString(R.string.unknown_artist)

                mBottomSheetPlayerMoreBinding.textDescription.text =
                    ctx.getString(
                        R.string.item_song_card_text_details,
                        FormattersUtils.formatSongDurationToString(songItem.duration ),
                        songItem.typeMime
                    )

                val tempUri: Uri? = Uri.parse(songItem.uri ?: "")
                ImageLoadersUtils.loadCovertArtFromSongUri(ctx, mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100, 200, true)
            }
        }
    }

    private fun checkInteractions() {
        mBottomSheetPlayerMoreBinding.buttonInfo.setOnClickListener{
            showSongInfoDialog()
        }
        mBottomSheetPlayerMoreBinding.buttonPlaylistAdd.setOnClickListener{
            showAddToPlaylistDialog()
        }
        mBottomSheetPlayerMoreBinding.buttonLyrics.setOnClickListener{
            fetchLyrics()
        }
        mBottomSheetPlayerMoreBinding.buttonShare.setOnClickListener{
            shareSong()
        }
        mBottomSheetPlayerMoreBinding.buttonTimer.setOnClickListener{
            showSleepTimerDialog()
        }
        mBottomSheetPlayerMoreBinding.buttonGoto.setOnClickListener{
            showGoToSongDialog()
        }
        mBottomSheetPlayerMoreBinding.buttonSetAs.setOnClickListener{
            setSongAsRingtone()
        }
        mBottomSheetPlayerMoreBinding.buttonDelete.setOnClickListener{
            showDeleteSelectionDialog()
        }
    }

    private fun showDeleteSelectionDialog() {
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
        context?.let { ctx ->
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(ctx.getString(R.string.dialog_delete_selection_title))
                .setIcon(R.drawable.delete)
                .setMessage(ctx.getString(R.string.dialog_delete_selection_description))
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(ctx.getString(R.string.delete_file)) { _, _ ->
                    deleteSelectedSongs()
                }
                .show()
        }
        dismiss()
    }
    private fun deleteSelectedSongs(){
        //
    }

    private fun setSongAsRingtone() {
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
        context?.let { ctx ->
            if(PermissionsManagerUtils.haveWriteSystemSettingsPermission(ctx)){
                val tempUri : Uri = Uri.parse(tempSongItem.uri ?: return) ?: return
                SystemSettingsUtils.setRingtone(ctx, tempUri, tempSongItem.fileName, true)
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
                            PermissionsManagerUtils.requestWriteSystemSettingsPermission(
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
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return

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
                    mDialogGotoSongBinding.buttonArtist.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ARTISTS, tempSongItem.artist ?: ""))
                            addToBackStack(null)
                        }
                    }
                    mDialogGotoSongBinding.buttonAlbum.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUMS, tempSongItem.album ?: ""))
                            addToBackStack(null)
                        }
                    }
                    mDialogGotoSongBinding.buttonFolder.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_FOLDERS, tempSongItem.folder ?: ""))
                            addToBackStack(null)
                        }
                    }
                    mDialogGotoSongBinding.buttonComposer.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_COMPOSERS, tempSongItem.composer ?: ""))
                            addToBackStack(null)
                        }
                    }
                    mDialogGotoSongBinding.buttonGenre.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_GENRES, tempSongItem.genre ?: ""))
                            addToBackStack(null)
                        }
                    }
                    mDialogGotoSongBinding.buttonAlbumArtist.setOnClickListener{
                        this.dismiss()
                        mMainFragmentViewModel.setHideSlidingPanelCounter()
                        tempFragmentManager.commit {
                            setReorderingAllowed(false)
                            add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUM_ARTISTS, tempSongItem.albumArtist ?: ""))
                            addToBackStack(null)
                        }
                    }
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
                    val tempSleepTimer: Float = mPlayerFragmentViewModel.getSleepTimer().value?.sliderValue ?: 0.0f
                    val tempPlayLastSong: Boolean = mPlayerFragmentViewModel.getSleepTimer().value?.playLastSong ?: false
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
        mPlayerFragmentViewModel.setSleepTimer(tempSleepTimerSP)
    }

    private fun shareSong() {
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
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
                                val tempUri = Uri.parse(tempSongItem.uri) ?: return@withContext
                                val tempDesc =
                                    if(tempSongItem.title != null)
                                        ctx.getString(R.string._music_content_by, tempSongItem.title, tempSongItem.artist ?: "")
                                    else
                                        "${tempSongItem.fileName}"
                                IntentActionsUtils.shareSongFile(ctx, tempUri, tempDesc)
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
                                IntentActionsUtils.shareBitmapImage(ctx, tempBitmap, tempDesc)
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
    private fun getScreenShotOfView(view: View): Bitmap {
        return view.rootView.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun fetchLyrics() {
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
    }

    private fun showAddToPlaylistDialog() {
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return

        val songsToAddOnPlaylist : ArrayList<PlaylistSongItem> = ArrayList()
        val psI = PlaylistSongItem()
        psI.songId = tempSongItem.id
        psI.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
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
        val tempSongItem : SongItem = mPlayerFragmentViewModel.getCurrentPlayingSong().value ?: return
        val songInfoBottomSheetDialog = SongInfoFullBottomSheetDialogFragment.newInstance(tempSongItem)
        activity ?.supportFragmentManager?.let { songInfoBottomSheetDialog.show(it, SongInfoFullBottomSheetDialogFragment.TAG) }
        dismiss()
    }
    private fun initViews() {
        mBottomSheetPlayerMoreBinding.covertArt.layout(0,0,0,0)
        mBottomSheetPlayerMoreBinding.textTitle.isSelected = true
        mBottomSheetPlayerMoreBinding.textDescription.isSelected = true
    }

    companion object {
        const val TAG = "PlayerMoreDialog"

        @JvmStatic
        fun newInstance(playerFragmentViewModel : PlayerFragmentViewModel, mainFragmentViewModel : MainFragmentViewModel, screenShootPlayerView : View) =
            PlayerMoreFullBottomSheetDialog().apply {
                mPlayerFragmentViewModel = playerFragmentViewModel
                mMainFragmentViewModel = mainFragmentViewModel
                mScreenShotPlayerView = screenShootPlayerView
            }
    }
}