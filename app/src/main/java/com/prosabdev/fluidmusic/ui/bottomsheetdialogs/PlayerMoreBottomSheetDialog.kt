package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.databinding.DialogGotoSongBinding
import com.prosabdev.fluidmusic.databinding.DialogSetTimerBinding
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


class PlayerMoreBottomSheetDialog(private val mMainFragmentViewModel: MainFragmentViewModel) : GenericBottomSheetDialogFragment() ,
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
            mSleepTimerSP = SharedPreferenceManager.Player.loadSleepTimer(ctx)
            val currentSong : CurrentPlayingSongSP = SharedPreferenceManager.Player.loadCurrentPlayingSong(ctx)
                ?: return@withContext
            updateCurrentPlayingSongUI(currentSong)
            if(currentSong.uri == null) return@withContext
            mSongItem = CustomAudioInfoExtractor.extractAudioInfoFromUri(ctx, Uri.parse(currentSong.uri))
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
                    ctx.getString(R.string.unknown_artist)

            mBottomSheetPlayerMoreBinding.textDescription.text =
                ctx.getString(
                    R.string.item_song_card_text_details,
                    CustomFormatters.formatSongDurationToString(currentSong.duration ),
                    currentSong.typeMime
                )

            val tempUri: Uri? = Uri.parse(currentSong.uri ?: "")
            CustomUILoaders.loadCovertArtFromSongUri(ctx, mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100)
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
            .setMessage(resources.getString(R.string.dialog_delete_selection_description))
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.delete_file)) { dialog, which ->
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
                .setTitle("Set song as ringtone")
                .setMessage("Allow Fluid Music to modify audio settings ?")
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.lets_go)) { dialog, which ->
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
        dismiss()
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Sleep timer")
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
    }

    private fun showTimerDialog() {
        val ctx : Context = this.context ?: return
        val dialogSetTimerBinding : DialogSetTimerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_set_timer, null, false)

        MaterialAlertDialogBuilder(ctx)
            .setTitle("Sleep timer")
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
                        ctx.getString(R.string.disabled)
                    else
                        ctx.getString(R.string._timer_range_value, (mSleepTimerSP?.sliderValue ?: 0.0f).toInt())
                dialogSetTimerBinding.checkboxPlayLastSong.isChecked = mSleepTimerSP?.playLastSong ?: false

                dialogSetTimerBinding.slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                    dialogSetTimerBinding.textRangeValue.text =
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
        SharedPreferenceManager.Player.saveSleepTimer(ctx, tempSleepTimerSP)
    }

    private fun shareSong() {
        if(mSongItem == null || mSongItem?.uri == null)
            return
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Currently listening : " + mSongItem?.fileName)
        shareIntent.setDataAndType(Uri.parse(mSongItem?.uri), mSongItem?.typeMime)
        startActivity(Intent.createChooser(shareIntent,"Share To :"))
        dismiss()
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

                val currentPlayingSong = SharedPreferenceManager.Player.loadCurrentPlayingSong(ctx, sharedPreferences)
                MainScope().launch {
                    updateCurrentPlayingSongUI(currentPlayingSong)
                }
            }
            ConstantValues.SHARED_PREFERENCES_SLEEP_TIMER -> {
                mSleepTimerSP = SharedPreferenceManager.Player.loadSleepTimer(ctx, sharedPreferences)
            }
        }
    }
}