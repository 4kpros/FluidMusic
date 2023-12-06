package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.playlist.PlaylistItemAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetAddToPlaylistBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogEditTextBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogTitleBinding
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import kotlinx.coroutines.*


class PlaylistAddFullBottomSheetDialogFragment : GenericFullBottomSheetDialogFragment() {

    private lateinit var mDataBiding: BottomSheetAddToPlaylistBinding

    private var mPlaylistItemViewModel: PlaylistItemViewModel? = null

    private var mPlaylistItemAdapter: PlaylistItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mSongsToAddOnPlaylist : ArrayList<com.prosabdev.common.models.playlist.PlaylistSongItem>? = null
    private var mPlaylists : ArrayList<com.prosabdev.common.models.playlist.PlaylistItem>? = null
    private var mDefaultPlaylistCount : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_add_to_playlist, container, false)
        val view = mDataBiding.root

        //Load your UI content
        initViews()
        setupRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
    }

    private fun setupRecyclerView() {
        context?.let { ctx ->
            //Setup playlist item adapter adapter
            mPlaylistItemAdapter = PlaylistItemAdapter(object : PlaylistItemAdapter.OnItemClickListener{
                override fun onClickListener(position: Int) {
                    val playlistId : Long = mPlaylistItemAdapter?.currentList?.get(position)?.id ?: -1
                    val playlistName : String = mPlaylistItemAdapter?.currentList?.get(position)?.name ?: ""
                    if(playlistId > 0){
                        MainScope().launch {
                            insertMultiplesSongsToPlaylist(ctx, playlistId, playlistName)
                            dismiss()
                        }
                    }
                }

            })
            mPlaylistItemAdapter?.submitList(mPlaylists)
            mDataBiding.recyclerView.scrollToPosition(0)

            mLayoutManager = GridLayoutManager(ctx, 1, GridLayoutManager.VERTICAL, false)
            mDataBiding.recyclerView.adapter = mPlaylistItemAdapter
            mDataBiding.recyclerView.layoutManager = mLayoutManager
        }
    }

    private fun checkInteractions() {
        mDataBiding.buttonNewPlaylist.setOnClickListener{
            MainScope().launch {
                showAddNewPlaylistDialog()
            }
        }
    }

    private suspend fun showAddNewPlaylistDialog() {
        context?.let { ctx->
            val dialogDataBidingView : ComponentDialogEditTextBinding = DataBindingUtil.inflate(layoutInflater, R.layout._component_dialog_edit_text, null, false)
            val dialogTitleDataBidingView : ComponentDialogTitleBinding = DataBindingUtil.inflate(layoutInflater, R.layout._component_dialog_title, null, false)
            MaterialAlertDialogBuilder(ctx)
                .setCustomTitle(dialogTitleDataBidingView.root)
                .setIcon(R.drawable.playlist_add)
                .setView(dialogDataBidingView.root)
                .show().apply {
                    //Apply title
                    dialogTitleDataBidingView.imageViewIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            ctx.resources,
                            R.drawable.playlist_add,
                            null
                        )
                    )
                    dialogTitleDataBidingView.textTitle.text = ctx.resources.getString(R.string.add_new_playlist)
                    //Listen clicks events
                    dialogDataBidingView.buttonCancel.setOnClickListener {
                        dismiss()
                    }
                    dialogDataBidingView.buttonSave.setOnClickListener {
                        MainScope().launch {
                            savePlaylistAndAddSong(ctx, dialogDataBidingView.textInputEditText.text.toString())
                        }
                        dismiss()
                    }
                    //Listen clicks events
                    if(dialogDataBidingView.textInputEditText.requestFocus()){
                        dialogDataBidingView.textInputEditText.setText("$DEFAULT_PLAYLIST_NAME ${mDefaultPlaylistCount+1}")
                        dialogDataBidingView.textInputEditText.hint = ctx.resources.getString(R.string.playlist_name)
                        dialogDataBidingView.textInputEditText.selectAll()
                        com.prosabdev.common.utils.SystemSettings.SoftInputService(context, dialogDataBidingView.textInputEditText).show(5)
                    }
                    dialogDataBidingView.textInputEditText.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }
                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            handleOnInputChanges(dialogDataBidingView, s)
                        }
                        override fun afterTextChanged(s: Editable?) {
                        }
                    })
                }
        }
        dismiss()
    }

    private suspend fun savePlaylistAndAddSong(ctx : Context, playlistName: String): Boolean {
        return withContext(Dispatchers.Default){
            val pI = com.prosabdev.common.models.playlist.PlaylistItem()
            pI.name = playlistName
            pI.lastAddedDateToLibrary = com.prosabdev.common.utils.SystemSettings.getCurrentDateInMillis()
            val insertedPlaylistResult : Long = mPlaylistItemViewModel?.insert(pI) ?: -1
            if(insertedPlaylistResult > 0){
                insertMultiplesSongsToPlaylist(ctx, insertedPlaylistResult, playlistName)
            }
            return@withContext false
        }
    }
    private suspend fun insertMultiplesSongsToPlaylist(ctx : Context, playlistId : Long, playlistName : String = "") {
        withContext(Dispatchers.Default){
            if(mSongsToAddOnPlaylist == null || (mSongsToAddOnPlaylist?.size ?: 0) <= 0)
                return@withContext
            //
        }
    }

    private var mCheckerJob: Job? =null
    private fun handleOnInputChanges(
        dialogDataBidingView: ComponentDialogEditTextBinding,
        sequence: CharSequence?
    ) {
        if(mCheckerJob != null && mCheckerJob?.isActive == true)
            mCheckerJob?.cancel()
        mCheckerJob = CoroutineScope(Dispatchers.Default).launch {
            if(sequence == null || sequence.isEmpty()){
                updateInputUI(dialogDataBidingView, true)
            }else{
                val playlistExist = checkIfPlaylistExist(sequence.toString())
                if(playlistExist){
                    updateInputUI(dialogDataBidingView, true)
                }else{
                    updateInputUI(dialogDataBidingView, false)
                }
            }
        }
    }
    private fun updateInputUI(
        dialogDataBidingView: ComponentDialogEditTextBinding,
        haveErrors: Boolean,
        nameLength : Int = 0
    ) {
        MainScope().launch {
            context?.let { ctx ->
                if(!haveErrors){
                    dialogDataBidingView.textInputLayout.error = null
                }else{
                    if(nameLength <= 0){
                        dialogDataBidingView.textInputLayout.error = ctx.resources.getString(R.string.invalid_name)
                    }else{
                        dialogDataBidingView.textInputLayout.error = ctx.resources.getString(R.string.name_already_exist)
                    }
                }
            }
        }
    }
    private suspend fun checkIfPlaylistExist(name : String): Boolean {
        val result = mPlaylistItemViewModel?.getWithName(name)
        return result != null && result.name == name
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "PlaylistAddFullBottomSheetDialogFragment"
        const val DEFAULT_PLAYLIST_NAME : String = "Playlist"

        @JvmStatic
        fun newInstance(songsToAddOnPlaylist : ArrayList<com.prosabdev.common.models.playlist.PlaylistSongItem>?, playlists : ArrayList<com.prosabdev.common.models.playlist.PlaylistItem>?, defaultPlaylistCount : Long = 0) =
            PlaylistAddFullBottomSheetDialogFragment().apply {
                mSongsToAddOnPlaylist = songsToAddOnPlaylist
                mPlaylists = playlists
                mDefaultPlaylistCount = defaultPlaylistCount//Last default playlist name + index(counter)
            }
    }
}