package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetSleepTimerBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerMoreFullBottomSheetDialog(private val mMainFragmentViewModel: MainFragmentViewModel, private val mScreeShotPlayerView : View) : BottomSheetDialogFragment() ,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mBottomSheetPlayerMoreBinding: BottomSheetPlayerMoreBinding

    private var mSongItem: SongItem? = null
    private var mSleepTimerSP: SleepTimerSP? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBottomSheetPlayerMoreBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player_more, container, false)
        val view = mBottomSheetPlayerMoreBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            loadSharedPreferencesData()
            checkInteractions()
        }
    }

    private suspend fun loadSharedPreferencesData() {
        val ctx : Context = this@PlayerMoreFullBottomSheetDialog.context ?: return

        withContext(Dispatchers.IO){
            mSleepTimerSP = SharedPreferenceManagerUtils.Player.loadSleepTimer(ctx)
            val currentSong : CurrentPlayingSongSP = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx)
                ?: return@withContext
            updateCurrentPlayingSongUI(currentSong)
            if(currentSong.uri == null) return@withContext
            mSongItem = AudioInfoExtractorUtils.extractAudioInfoFromUri(ctx, Uri.parse(currentSong.uri))
            mSongItem?.id = currentSong.id
        }
    }

    private suspend fun updateCurrentPlayingSongUI(currentSong: CurrentPlayingSongSP?) {
        if(currentSong == null)
            return

        val ctx : Context = this@PlayerMoreFullBottomSheetDialog.context ?: return

        MainScope().launch {
            mBottomSheetPlayerMoreBinding.textTitle.text =
                if(
                    currentSong.title != null && currentSong.title!!.isNotEmpty()
                )
                    currentSong.title
                else
                    currentSong.fileName

            mBottomSheetPlayerMoreBinding.textArtist.text =
                if(currentSong.artist != null && currentSong.artist!!.isNotEmpty())
                    currentSong.artist
                else
                    ctx.getString(R.string.unknown_artist)

            mBottomSheetPlayerMoreBinding.textDescription.text =
                ctx.getString(
                    R.string.item_song_card_text_details,
                    FormattersUtils.formatSongDurationToString(currentSong.duration ),
                    currentSong.typeMime
                )

            val tempUri: Uri? = Uri.parse(currentSong.uri ?: "")
            ImageLoadersUtils.loadCovertArtFromSongUri(ctx, mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100, 200, true)
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
        val ctx : Context = this@PlayerMoreFullBottomSheetDialog.context ?: return

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
        dismiss()
    }
    private fun deleteSelectedSongs(){
        //
    }

    private fun setSongAsRingtone() {
        val ctx : Context = this@PlayerMoreFullBottomSheetDialog.context ?: return
        val tempActivity : Activity = this@PlayerMoreFullBottomSheetDialog.activity ?: return

        if(PermissionsManagerUtils.haveWriteSystemSettingsPermission(ctx)){
            val tempUri : Uri = Uri.parse(mSongItem?.uri ?: return) ?: return
            SystemSettingsUtils.setRingtone(ctx, tempUri, mSongItem?.fileName, true)
        }else{
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(ctx.getString(R.string.set_as_ringtone))
                .setIcon(R.drawable.ring_volume)
                .setMessage(ctx.getString(R.string.Allow_Fluid_Music_to_modify_audio_settings))
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(ctx.getString(R.string.lets_go)) { dialog, _ ->
                    PermissionsManagerUtils.requestWriteSystemSettingsPermission(tempActivity)
                    dialog.dismiss()
                }
                .show()
        }
        dismiss()
    }

    private fun showGoToSongDialog() {
        val ctx : Context = this.context ?: return

        val mDialogGotoSongBinding : DialogGotoSongBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_goto_song, null, false)

        val tempFragmentManager = activity?.supportFragmentManager ?: return
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
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ARTISTS, mSongItem?.artist ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonAlbum.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUMS, mSongItem?.album ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonFolder.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_FOLDERS, mSongItem?.folder ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonComposer.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_COMPOSERS, mSongItem?.composer ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonGenre.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_GENRES, mSongItem?.genre ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonAlbumArtist.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFragmentManager.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUM_ARTISTS, mSongItem?.albumArtist ?: ""))
                        addToBackStack(null)
                    }
                }
            }
        dismiss()
    }

    private fun showSleepTimerDialog() {
        val ctx : Context = this.context ?: return
        val mDialogSetSleepTimerBinding : DialogSetSleepTimerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_set_sleep_timer, null, false)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(ctx.getString(R.string.sleep_timer))
            .setIcon(R.drawable.timer)
            .setView(mDialogSetSleepTimerBinding.root)
            .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(ctx.getString(R.string.ok)) { _, _ ->
                saveNewTimer(
                    ctx,
                    mDialogSetSleepTimerBinding.slider.value,
                    mDialogSetSleepTimerBinding.checkboxPlayLastSong.isChecked
                )
            }
            .show().apply {
            mDialogSetSleepTimerBinding.slider.value = mSleepTimerSP?.sliderValue ?: 0.0f
            mDialogSetSleepTimerBinding.textRangeValue.text =
                if((mSleepTimerSP?.sliderValue ?: 0.0f) <= 0)
                    ctx.getString(R.string.disabled)
                else
                    ctx.getString(R.string._timer_range_value, (mSleepTimerSP?.sliderValue ?: 0.0f).toInt())
            mDialogSetSleepTimerBinding.checkboxPlayLastSong.isChecked = mSleepTimerSP?.playLastSong ?: false

            mDialogSetSleepTimerBinding.slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                mDialogSetSleepTimerBinding.textRangeValue.text =
                    if(value <= 0)
                        ctx.getString(R.string.disabled)
                    else
                        ctx.getString(R.string._timer_range_value, value.toInt())
            })
        }
        dismiss()
    }

    private fun saveNewTimer(ctx: Context, value: Float, playLastSong: Boolean = false) {
        val tempSleepTimerSP = SleepTimerSP()
        tempSleepTimerSP.sliderValue = value
        tempSleepTimerSP.playLastSong = playLastSong
        SharedPreferenceManagerUtils.Player.saveSleepTimer(ctx, tempSleepTimerSP)
    }

    private fun shareSong() {
        if(mSongItem == null || mSongItem?.uri == null)
            return

        val ctx : Context = this.context ?: return

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
                            val tempUri = Uri.parse(mSongItem?.uri) ?: return@withContext
                            val tempDesc =
                                if(mSongItem?.title != null)
                                    ctx.getString(R.string._music_content_by, mSongItem?.title, mSongItem?.artist ?: "")
                                else
                                    "${mSongItem?.fileName}"
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
                            val tempBitmap = getScreenShotOfView(mScreeShotPlayerView)
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
        dismiss()
    }
    private fun getScreenShotOfView(view: View): Bitmap {
        return view.rootView.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    private fun fetchLyrics() {
        //On delete song
    }

    private fun showAddToPlaylistDialog() {
        if(mSongItem?.uri.isNullOrEmpty() || (mSongItem?.size ?: 0) <= 0){
            MainScope().launch {
                Toast.makeText(requireContext(), "Please select valid song !", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val songIdList : ArrayList<PlaylistSongItem> = ArrayList()
        val psI = PlaylistSongItem()
        psI.songId = mSongItem?.id ?: -1
        psI.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
        songIdList.add(psI)

        val playlistAddBottomSheetDialog = PlaylistAddFullBottomSheetDialogFragment(songIdList)
        activity ?.supportFragmentManager?.let { playlistAddBottomSheetDialog.show(it, PlaylistAddFullBottomSheetDialogFragment.TAG) }
        dismiss()
    }

    private fun showSongInfoDialog() {
        val songInfoBottomSheetDialog = SongInfoFullBottomSheetDialogFragment(mSongItem)
        activity ?.supportFragmentManager?.let { songInfoBottomSheetDialog.show(it, SongInfoFullBottomSheetDialogFragment.TAG) }
        dismiss()
    }
    private fun initViews() {
        mBottomSheetPlayerMoreBinding.covertArt.layout(0,0,0,0)
        mBottomSheetPlayerMoreBinding.textTitle.isSelected = true
        mBottomSheetPlayerMoreBinding.textDescription.isSelected = true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val ctx : Context = this.context ?: return
        when (key) {
            ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG -> {

                val currentPlayingSong = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx, sharedPreferences)
                MainScope().launch {
                    updateCurrentPlayingSongUI(currentPlayingSong)
                }
            }
            ConstantValues.SHARED_PREFERENCES_SLEEP_TIMER -> {
                mSleepTimerSP = SharedPreferenceManagerUtils.Player.loadSleepTimer(ctx, sharedPreferences)
            }
        }
    }

    companion object {
        const val TAG = "PlayerMoreDialog"
    }
}