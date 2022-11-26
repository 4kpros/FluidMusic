package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetPlayerMoreBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistAddBottomSheetDialog(private val mPlaylistAdd : List<Long>, private val mPlaylistName : String, private val mPlaylistId : Long) : GenericBottomSheetDialogFragment() {

    private lateinit var mBottomSheetPlayerMoreBinding: BottomSheetPlayerMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBottomSheetPlayerMoreBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_player_more, container, false)
        val view = mBottomSheetPlayerMoreBinding.root

        initViews()
        MainScope().launch {
            loadPlaylists()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    private fun observeLiveData() {
        //
    }

    private fun checkInteractions() {
        //
    }

    private suspend fun loadPlaylists() {
        withContext(Dispatchers.IO){
            //
        }
    }

    private fun initViews() {
        //
    }
}