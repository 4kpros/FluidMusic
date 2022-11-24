package com.prosabdev.fluidmusic.ui.dialogs

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.core.text.parseAsHtml
import androidx.core.text.toHtml
import androidx.core.text.toSpanned
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.DialogSongInfoBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongItem
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.GenericBottomSheetDialogFragment
import com.prosabdev.fluidmusic.utils.CustomAudioInfoExtractor
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongInfoDialog(private val mSongItem : SongItem?) : GenericBottomSheetDialogFragment() {

    private lateinit var mDialogSongInfoBinding: DialogSongInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDialogSongInfoBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_song_info, container, false)
        val view = mDialogSongInfoBinding.root

        initViews()
        MainScope().launch {
            showSongDetailsUI(mSongItem)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun showSongDetailsUI(songItem: SongItem?) {
        if(songItem == null) return
        MainScope().launch {
            val ctx : Context = this@SongInfoDialog.context ?: return@launch

            mDialogSongInfoBinding.textSongFilePath.text = getUnderLinedWord(songItem.uriPath)

            //MP3, 44100Hz, 320kbps
            mDialogSongInfoBinding.textSongDetails.text = ctx.getString(
                R.string._song_details_tags,
                songItem.typeMime ?: "",
                (songItem.sampleRate/1000.0f),
                (songItem.bitrate/1000.0f)
            )
            //3:27min = 207sec
            mDialogSongInfoBinding.textSongDuration.text = ctx.getString(
                R.string._song_duration_tags,
                CustomFormatters.formatSongDurationToString(songItem.duration),
                songItem.duration/1000
            )
            //3.5mb = 3730kb
            val fileSizeInKB: Long = songItem.size / 1024
            val fileSizeInMB = fileSizeInKB / 1024.0f
            mDialogSongInfoBinding.textSongSize.text = ctx.getString(
                R.string._song_size_tags,
                fileSizeInMB,
                fileSizeInKB
            )

            mDialogSongInfoBinding.textSongTrack.text = songItem.cdTrackNumber
            mDialogSongInfoBinding.textSongDisc.text = songItem.diskNumber
            mDialogSongInfoBinding.textSongYear.text = getUnderLinedWord(songItem.year)

            mDialogSongInfoBinding.textSongFileName.text = songItem.fileName
            mDialogSongInfoBinding.textSongTitle.text = songItem.title
            mDialogSongInfoBinding.textSongArtist.text = getUnderLinedWord(songItem.artist)
            mDialogSongInfoBinding.textSongAlbum.text = getUnderLinedWord(songItem.album)
            mDialogSongInfoBinding.textSongAlbumArtist.text = getUnderLinedWord(songItem.albumArtist)
            mDialogSongInfoBinding.textSongGenre.text = getUnderLinedWord(songItem.genre)
            mDialogSongInfoBinding.textSongComposer.text = getUnderLinedWord(songItem.composer)
        }
    }

    private fun getUnderLinedWord(word: String?): CharSequence {
        val uWord = SpannableString(word ?: "")
        uWord.setSpan(UnderlineSpan(), 0, uWord.length, 0)
        return uWord
    }

    private fun initViews() {
        //
    }
    companion object {
        const val TAG = "SongInfoDialog"
    }
}