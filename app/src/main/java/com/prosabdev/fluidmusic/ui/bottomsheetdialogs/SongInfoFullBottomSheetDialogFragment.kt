package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prosabdev.common.utils.FormattersAndParsers
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetDialogSongInfoBinding
import kotlinx.coroutines.launch


class SongInfoFullBottomSheetDialogFragment : GenericFullBottomSheetDialogFragment() {

    //Data binding
    private lateinit var mDataBinding: BottomSheetDialogSongInfoBinding

    //Variables
    private var mMediaItem : MediaItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_song_info, container, false)
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        showSongDetailsUI(mMediaItem)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun showSongDetailsUI(mediaItem: MediaItem?) {
        if(mediaItem == null) return
        lifecycleScope.launch {
            val ctx : Context = this@SongInfoFullBottomSheetDialogFragment.context ?: return@launch

            mDataBinding.textSongFilePath.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.extras?.getString("uriPath"))

            //MP3, 44100Hz, 320kbps
            mDataBinding.textSongDetails.text = ctx.getString(
                R.string._song_details_tags,
                mediaItem.mediaMetadata.extras?.getString("fileExtension") ?: "",
                (mediaItem.mediaMetadata.extras?.getLong("sampleRate") ?: 0) / 1000f,
                (mediaItem.mediaMetadata.extras?.getLong("bitrate") ?: 0) / 1000f
            )
            //3:27min = 207sec
            mDataBinding.textSongDuration.text = ctx.getString(
                R.string._song_duration_tags,
                FormattersAndParsers.formatSongDurationToString(mediaItem.mediaMetadata.extras?.getLong("duration") ?: 0),
                (mediaItem.mediaMetadata.extras?.getLong("duration") ?: 0) / 1000f
            )
            //3.5mb = 3730kb
            val fileSizeInKB: Long = (mediaItem.mediaMetadata.extras?.getLong("size") ?: 0) / 1024
            val fileSizeInMB = fileSizeInKB / 1024f
            mDataBinding.textSongSize.text = ctx.getString(
                R.string._song_size_tags,
                fileSizeInMB,
                fileSizeInKB
            )

            mDataBinding.textSongTrack.text = mediaItem.mediaMetadata.extras?.getInt("cdTrackNumber").toString()
            mDataBinding.textSongDisc.text = mediaItem.mediaMetadata.extras?.getInt("diskNumber").toString()
            mDataBinding.textSongYear.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.recordingYear.toString())

            mDataBinding.textSongFileName.text = mediaItem.mediaMetadata.extras?.getString("fileName")
            mDataBinding.textSongTitle.text = mediaItem.mediaMetadata.title
            mDataBinding.textSongArtist.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.artist)
            mDataBinding.textSongAlbum.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.albumTitle)
            mDataBinding.textSongAlbumArtist.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.albumArtist)
            mDataBinding.textSongGenre.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.genre)
            mDataBinding.textSongComposer.text = FormattersAndParsers.getUnderLinedWord(mediaItem.mediaMetadata.composer)
        }
    }

    private fun initViews() {
        //
    }
    companion object {
        const val TAG = "SongInfoFullBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance(mediaItem: MediaItem?) =
            SongInfoFullBottomSheetDialogFragment().apply {
                mMediaItem = mediaItem
            }
    }
}