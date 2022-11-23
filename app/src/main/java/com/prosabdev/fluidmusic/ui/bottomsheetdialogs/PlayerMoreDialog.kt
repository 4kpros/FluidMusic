package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

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
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerMoreDialog : GenericBottomSheetDialogFragment() ,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mBottomSheetPlayerMoreBinding: BottomSheetPlayerMoreBinding

    private lateinit var mSongItemViewModel: SongItemViewModel

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
            checkInteractions()
            observeLiveData()
        }
    }

    private fun observeLiveData() {
    }

    private fun updatePlayerUI(songItem: SongItem) {
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
                this.getString(R.string.unknown_artist)

        mBottomSheetPlayerMoreBinding.textDescription.text =
            this.getString(
                R.string.item_song_card_text_details,
                CustomFormatters.formatSongDurationToString(songItem.duration),
                songItem.typeMime
            )

        MainScope().launch {
            val tempUri: Uri? = Uri.parse(songItem.uri)
            CustomUILoaders.loadCovertArtFromSongUri(this@PlayerMoreDialog.requireContext(), mBottomSheetPlayerMoreBinding.covertArt, tempUri, 100)
        }
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
        //On delete song
    }

    private fun initViews() {
        mBottomSheetPlayerMoreBinding.covertArt.layout(0,0,0,0)

        mSongItemViewModel = ModelsViewModelFactory(this.requireContext()).create(SongItemViewModel::class.java)
    }

    companion object {
        const val TAG = "PlayerMoreDialog"
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG)
            decodeSharedPrefsChanged(sharedPreferences)
    }

    private fun decodeSharedPrefsChanged(sharedPreferences: SharedPreferences?) {
//        updatePlayerUI()
    }
}