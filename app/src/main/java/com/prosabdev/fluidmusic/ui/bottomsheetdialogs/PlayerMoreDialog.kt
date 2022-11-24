package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongItem
import com.prosabdev.fluidmusic.ui.dialogs.SongInfoDialog
import com.prosabdev.fluidmusic.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerMoreDialog : GenericBottomSheetDialogFragment() ,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mBottomSheetPlayerMoreBinding: BottomSheetPlayerMoreBinding

    private var mSongItem: SongItem? = null

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
        val ctx : Context = this@PlayerMoreDialog.context ?: return

        withContext(Dispatchers.IO){
            val currentSong : CurrentPlayingSongItem? = SharedPreferenceManager.loadCurrentPlayingSong(ctx)
            updateCurrentPlayingSongUI(currentSong)
            mSongItem = CustomAudioInfoExtractor.extractAudioInfoFromUri(ctx, Uri.parse(currentSong?.uri))
        }
    }

    private suspend fun updateCurrentPlayingSongUI(currentSong: CurrentPlayingSongItem?) {
        if(currentSong == null)
            return

        val ctx : Context = this@PlayerMoreDialog.context ?: return

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
                    CustomFormatters.formatSongDurationToString(currentSong.duration ?: 0),
                    currentSong.typeMime
                )

            val tempUri: Uri? = Uri.parse(currentSong.uri ?: "")
            CustomUILoaders.loadCovertArtFromSongUri(ctx, mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100)
        }
    }

    private fun observeLiveData() {
    }

    private fun checkInteractions() {
        mBottomSheetPlayerMoreBinding.buttonInfo.setOnClickListener(){
            onGetSongDetails()
        }
        mBottomSheetPlayerMoreBinding.buttonCovertArt.setOnClickListener(){
            onFetchCovertArtSong()
        }
        mBottomSheetPlayerMoreBinding.buttonLyrics.setOnClickListener(){
            onFetchLyrics()
        }
        mBottomSheetPlayerMoreBinding.buttonShare.setOnClickListener(){
            onShareSong()
        }
        mBottomSheetPlayerMoreBinding.buttonTimer.setOnClickListener(){
            onSetTimer()
        }
        mBottomSheetPlayerMoreBinding.buttonGoto.setOnClickListener(){
            onGoToSong()
        }
        mBottomSheetPlayerMoreBinding.buttonSetAs.setOnClickListener(){
            onSetSongAsRingtone()
        }
        mBottomSheetPlayerMoreBinding.buttonDelete.setOnClickListener(){
            onDeleteSong()
        }
    }

    private fun onDeleteSong() {
        //On delete song
    }

    private fun onSetSongAsRingtone() {
        //On delete song
    }

    private fun onGoToSong() {
        //On delete song
    }

    private fun onSetTimer() {
        //On delete song
    }

    private fun onShareSong() {
        //On delete song
    }

    private fun onFetchLyrics() {
        //On delete song
    }

    private fun onFetchCovertArtSong() {
        //On delete song
    }

    private fun onGetSongDetails() {
        showDialog()
    }

    fun showDialog() {
        val songInfoDialog = SongInfoDialog(mSongItem)
        songInfoDialog.show(activity ?.supportFragmentManager!!, SongInfoDialog.TAG)
        dismiss()
//        activity ?.supportFragmentManager?.commit {
//            this.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .add(android.R.id.content, SongInfoDialog())
//            .addToBackStack(null)
//        }
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
        val ctx : Context? = this@PlayerMoreDialog.context
        when (key) {
            ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG -> {
                if(ctx != null){
                    val currentPlayingSong = SharedPreferenceManager.loadCurrentPlayingSong(ctx, sharedPreferences)
                    MainScope().launch {
                        updateCurrentPlayingSongUI(currentPlayingSong)
                    }
                }
            }
        }
    }
}