package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModel

class PlayerMoreDialog : GenericBottomSheetDialogFragment() {

    private lateinit var mContext: Context
    private var mActivity: FragmentActivity? = null

    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private var mTextTitle: AppCompatTextView? = null
    private var mTextArtist: AppCompatTextView? = null
    private var mTextMimeType: AppCompatTextView? = null
    private var mTextDuration: AppCompatTextView? = null
    private var mCovertArtImageView: AppCompatImageView? = null

    private var mButtonInfo: MaterialButton? = null
    private var mButtonCovertArt: MaterialButton? = null
    private var mButtonLyrics: MaterialButton? = null
    private var mButtonShare: MaterialButton? = null

    private var mButtonTimer: MaterialButton? = null
    private var mButtonGoTo: MaterialButton? = null
    private var mButtonSetAs: MaterialButton? = null
    private var mButtonDelete: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mContext = requireContext()
        mActivity = requireActivity()

        val view = layoutInflater.inflate(R.layout.bottom_sheet_player_more, container, false)

        initViews(view)
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        observeLiveData()
    }

//    private fun observeLiveData() {
//        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
//        ) { updatePlayerUI(it) }
//        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner
//        ) { updatePlayerUI(mPlayerFragmentViewModel.getCurrentSong().value ?: 0) }
//        mPlayerFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner
//        ) { updatePlayerUI(mPlayerFragmentViewModel.getCurrentSong().value ?: 0) }
//    }
//
//    private fun updatePlayerUI(position: Int) {
//        val tempSongSize : Int = mPlayerFragmentViewModel.getSongList().value?.size ?: 0
//        //Update current song info
//        if(tempSongSize > 0 && position >= 0){
//            if(mTextTitle != null)
//                mTextTitle?.text = if(mPlayerFragmentViewModel.getSongList().value!![position].title != null && mPlayerFragmentViewModel.getSongList().value!![position].title!!.isNotEmpty()) mPlayerFragmentViewModel.getSongList().value!![position].title else mPlayerFragmentViewModel.getSongList().value!![position].fileName
//
//            if(mTextArtist != null)
//                mTextArtist?.text = if(mPlayerFragmentViewModel.getSongList().value!![position].artist!!.isNotEmpty()) mPlayerFragmentViewModel.getSongList().value!![position].artist else mContext.getString(R.string.unknown_artist)
//
//            mTextDuration?.text = CustomFormatters.formatSongDurationToString(mPlayerFragmentViewModel.getSongList().value!![position].duration)
//            mTextMimeType?.text = mPlayerFragmentViewModel.getSongList().value!![position].typeMime
//        }
//        //Update blurred background
//        val tempBinary : ByteArray? = if(tempSongSize > 0) mPlayerFragmentViewModel.getSongList().value!![position].covertArt?.binaryData else null
//        MainScope().launch {
//            CustomUILoaders.loadCovertArtFromBinaryData(mContext, mCovertArtImageView, tempBinary, 100)
//        }
//    }

    private fun checkInteractions() {
        mButtonInfo?.setOnClickListener(){
            onGetSongDetails()
        }
        mButtonCovertArt?.setOnClickListener(){
            onFetchCovertArtSong()
        }
        mButtonLyrics?.setOnClickListener(){
            onFetchLyrics()
        }
        mButtonShare?.setOnClickListener(){
            onShareSong()
        }
        mButtonTimer?.setOnClickListener(){
            onSetTimer()
        }
        mButtonGoTo?.setOnClickListener(){
            onGoToSong()
        }
        mButtonSetAs?.setOnClickListener(){
            onSetSongAsRingtone()
        }
        mButtonDelete?.setOnClickListener(){
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

    private fun initViews(view: View) {

        mTextTitle = view.findViewById<AppCompatTextView>(R.id.text_title)
        mTextArtist = view.findViewById<AppCompatTextView>(R.id.text_artist)
        mTextMimeType = view.findViewById<AppCompatTextView>(R.id.song_item_type_mime)
        mTextDuration = view.findViewById<AppCompatTextView>(R.id.song_item_duration)
        mCovertArtImageView = view.findViewById<AppCompatImageView>(R.id.covert_art)

        mButtonInfo = view.findViewById<MaterialButton>(R.id.button_info)
        mButtonCovertArt = view.findViewById<MaterialButton>(R.id.button_covert_art)
        mButtonLyrics = view.findViewById<MaterialButton>(R.id.button_lyrics)
        mButtonShare = view.findViewById<MaterialButton>(R.id.button_share)

        mButtonTimer = view.findViewById<MaterialButton>(R.id.button_timer)
        mButtonGoTo = view.findViewById<MaterialButton>(R.id.button_goto)
        mButtonSetAs = view.findViewById<MaterialButton>(R.id.button_set_as)
        mButtonDelete = view.findViewById<MaterialButton>(R.id.button_delete)

        mCovertArtImageView?.layout(0,0,0,0)
    }

    companion object {
        const val TAG = "PlayerMoreDialog"
    }
}