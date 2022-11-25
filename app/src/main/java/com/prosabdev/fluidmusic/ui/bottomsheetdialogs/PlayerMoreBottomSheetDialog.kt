package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetTimerBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayerMoreBottomSheetDialog(private val mMainFragmentViewModel: MainFragmentViewModel, private val mScreeShotPlayerView : View) : GenericBottomSheetDialogFragment() ,
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

        checkInteractions()
        observeLiveData()
        MainScope().launch {
            loadSharedPreferencesData()
        }
    }

    private suspend fun loadSharedPreferencesData() {
        val ctx : Context = this@PlayerMoreBottomSheetDialog.context ?: return

        withContext(Dispatchers.IO){
            mSleepTimerSP = SharedPreferenceManagerUtils.Player.loadSleepTimer(ctx)
            val currentSong : CurrentPlayingSongSP = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx)
                ?: return@withContext
            updateCurrentPlayingSongUI(currentSong)
            if(currentSong.uri == null) return@withContext
            mSongItem = AudioInfoExtractorUtils.extractAudioInfoFromUri(ctx, Uri.parse(currentSong.uri))
        }
    }

    private suspend fun updateCurrentPlayingSongUI(currentSong: CurrentPlayingSongSP?) {
        if(currentSong == null)
            return

        val ctx : Context = this@PlayerMoreBottomSheetDialog.context ?: return

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
                    resources.getString(R.string.unknown_artist)

            mBottomSheetPlayerMoreBinding.textDescription.text =
                resources.getString(
                    R.string.item_song_card_text_details,
                    FormattersUtils.formatSongDurationToString(currentSong.duration ),
                    currentSong.typeMime
                )

            val tempUri: Uri? = Uri.parse(currentSong.uri ?: "")
            ImageLoadersUtils.loadCovertArtFromSongUri(ctx, mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100)
        }
    }

    private fun observeLiveData() {
    }

    private fun checkInteractions() {
        mBottomSheetPlayerMoreBinding.buttonInfo.setOnClickListener{
            showSongInfoDialog()
        }
        mBottomSheetPlayerMoreBinding.buttonCovertArt.setOnClickListener{
            showCovertArtSong()
        }
        mBottomSheetPlayerMoreBinding.buttonLyrics.setOnClickListener{
            fetchLyrics()
        }
        mBottomSheetPlayerMoreBinding.buttonShare.setOnClickListener{
            shareSong()
        }
        mBottomSheetPlayerMoreBinding.buttonTimer.setOnClickListener{
            showTimerDialog()
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
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(resources.getString(R.string.dialog_delete_selection_title))
            .setIcon(R.drawable.delete)
            .setMessage(resources.getString(R.string.dialog_delete_selection_description))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.delete_file)) { _, _ ->
                deleteSelectedSongs()
            }
            .show()
        dismiss()
    }
    private fun deleteSelectedSongs(){
        //
    }

    private fun setSongAsRingtone() {
        if(haveSystemPermissions()){
            //
        }else{
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(getString(R.string.set_ringtone))
                .setIcon(R.drawable.ring_volume)
                .setMessage(getString(R.string.Allow_Fluid_Music_to_modify_audio_settings))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.lets_go)) { dialog, _ ->
                    openAudioSystemSettings()
                    dialog.dismiss()
                }
                .show()
            dismiss()
        }
    }
    private fun openAudioSystemSettings() {
        //
    }

    private fun haveSystemPermissions(): Boolean {
        return false
    }

    private fun showGoToSongDialog() {
        val ctx : Context = this.context ?: return

        val mDialogGotoSongBinding : DialogGotoSongBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_goto_song, null, false)

        val tempFM = activity?.supportFragmentManager
        MaterialAlertDialogBuilder(ctx)
            .setTitle(resources.getString(R.string.go_to))
            .setIcon(R.drawable.link)
            .setView(mDialogGotoSongBinding.root)
            .setPositiveButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .show().apply {
                mDialogGotoSongBinding.buttonArtist.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ARTISTS, mSongItem?.artist ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonAlbum.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUMS, mSongItem?.album ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonFolder.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_FOLDERS, mSongItem?.folder ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonComposer.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_COMPOSERS, mSongItem?.composer ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonGenre.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_GENRES, mSongItem?.genre ?: ""))
                        addToBackStack(null)
                    }
                }
                mDialogGotoSongBinding.buttonAlbumArtist.setOnClickListener{
                    this.dismiss()
                    mMainFragmentViewModel.setHideSlidingPanel()
                    tempFM?.commit {
                        setReorderingAllowed(false)
                        add(R.id.main_fragment_container, ExploreContentsForFragment.newInstance(ConstantValues.EXPLORE_ALBUM_ARTISTS, mSongItem?.albumArtist ?: ""))
                        addToBackStack(null)
                    }
                }
            }
        dismiss()
    }

    private fun showTimerDialog() {
        val ctx : Context = this.context ?: return
        val dialogSetTimerBinding : DialogSetTimerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_set_timer, null, false)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(resources.getString(R.string.sleep_timer))
            .setIcon(R.drawable.timer)
            .setView(dialogSetTimerBinding.root)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                saveNewTimer(ctx, dialogSetTimerBinding.slider.value, dialogSetTimerBinding.checkboxPlayLastSong.isChecked)
            }
            .show().apply {
                dialogSetTimerBinding.slider.value = mSleepTimerSP?.sliderValue ?: 0.0f
                dialogSetTimerBinding.textRangeValue.text =
                    if((mSleepTimerSP?.sliderValue ?: 0.0f) <= 0)
                        resources.getString(R.string.disabled)
                    else
                        resources.getString(R.string._timer_range_value, (mSleepTimerSP?.sliderValue ?: 0.0f).toInt())
                dialogSetTimerBinding.checkboxPlayLastSong.isChecked = mSleepTimerSP?.playLastSong ?: false

                dialogSetTimerBinding.slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                    dialogSetTimerBinding.textRangeValue.text =
                        if(value <= 0)
                            resources.getString(R.string.disabled)
                        else
                            resources.getString(R.string._timer_range_value, value.toInt())
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
            .setTitle(getString(R.string.share_song))
            .setIcon(R.drawable.share)
            .setView(dialogShareSongBinding.root)
            .setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .show().apply {
                dialogShareSongBinding.buttonShareFile.setOnClickListener{
                    MainScope().launch {
                        withContext(Dispatchers.IO){
                            val tempUri = Uri.parse(mSongItem?.uri) ?: return@withContext
                            val tempDesc =
                                if(mSongItem?.title != null)
                                    resources.getString(R.string._music_content_by, mSongItem?.title, mSongItem?.artist ?: "")
                                else
                                    "${mSongItem?.fileName}"
                            IntentActionsUtils.shareSongFile(ctx, tempUri, tempDesc)
                        }
                    }
                    this@apply.dismiss()
                }
                dialogShareSongBinding.buttonShareScreenshot.setOnClickListener{
                    MainScope().launch {
                        dialogShareSongBinding.hoverButtonShareScreenshot.visibility = VISIBLE
                        withContext(Dispatchers.Default){
                            val tempBitmap = getScreenShotOfView(mScreeShotPlayerView)
                            val tempDesc = getString(R.string.currently_listening_icon)
                            IntentActionsUtils.shareBitmapImage(ctx, tempBitmap, tempDesc)
                            dialogShareSongBinding.hoverButtonShareScreenshot.visibility = GONE
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

    private fun showCovertArtSong() {
        //On delete song
    }

    private fun showSongInfoDialog() {
        val songInfoBottomSheetDialog = SongInfoBottomSheetDialog(mSongItem)
        songInfoBottomSheetDialog.show(activity ?.supportFragmentManager!!, SongInfoBottomSheetDialog.TAG)
        dismiss()
    }
    private fun initViews() {
        mBottomSheetPlayerMoreBinding.covertArt.layout(0,0,0,0)
        mBottomSheetPlayerMoreBinding.textTitle.isSelected = true
        mBottomSheetPlayerMoreBinding.textDescription.isSelected = true
    }

    companion object {
        const val TAG = "PlayerMoreDialog"
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
}