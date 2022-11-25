package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetDialogSongInfoBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.FormattersUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SongInfoBottomSheetDialog(private val mSongItem : SongItem?) : GenericBottomSheetDialogFragment() {

    private lateinit var mBottomSheetDialogSongInfoBinding: BottomSheetDialogSongInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetDialogSongInfoBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_song_info, container, false)
        val view = mBottomSheetDialogSongInfoBinding.root

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
            val ctx : Context = this@SongInfoBottomSheetDialog.context ?: return@launch

            mBottomSheetDialogSongInfoBinding.textSongFilePath.text = getUnderLinedWord(songItem.uriPath)

            //MP3, 44100Hz, 320kbps
            mBottomSheetDialogSongInfoBinding.textSongDetails.text = ctx.getString(
                R.string._song_details_tags,
                songItem.fileExtension ?: "",
                (songItem.sampleRate/1000.0f),
                (songItem.bitrate/1000.0f)
            )
            //3:27min = 207sec
            mBottomSheetDialogSongInfoBinding.textSongDuration.text = ctx.getString(
                R.string._song_duration_tags,
                FormattersUtils.formatSongDurationToString(songItem.duration),
                songItem.duration/1000
            )
            //3.5mb = 3730kb
            val fileSizeInKB: Long = songItem.size / 1024
            val fileSizeInMB = fileSizeInKB / 1024.0f
            mBottomSheetDialogSongInfoBinding.textSongSize.text = ctx.getString(
                R.string._song_size_tags,
                fileSizeInMB,
                fileSizeInKB
            )

            mBottomSheetDialogSongInfoBinding.textSongTrack.text = songItem.cdTrackNumber
            mBottomSheetDialogSongInfoBinding.textSongDisc.text = songItem.diskNumber
            mBottomSheetDialogSongInfoBinding.textSongYear.text = getUnderLinedWord(songItem.year)

            mBottomSheetDialogSongInfoBinding.textSongFileName.text = songItem.fileName
            mBottomSheetDialogSongInfoBinding.textSongTitle.text = songItem.title
            mBottomSheetDialogSongInfoBinding.textSongArtist.text = getUnderLinedWord(songItem.artist)
            mBottomSheetDialogSongInfoBinding.textSongAlbum.text = getUnderLinedWord(songItem.album)
            mBottomSheetDialogSongInfoBinding.textSongAlbumArtist.text = getUnderLinedWord(songItem.albumArtist)
            mBottomSheetDialogSongInfoBinding.textSongGenre.text = getUnderLinedWord(songItem.genre)
            mBottomSheetDialogSongInfoBinding.textSongComposer.text = getUnderLinedWord(songItem.composer)
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