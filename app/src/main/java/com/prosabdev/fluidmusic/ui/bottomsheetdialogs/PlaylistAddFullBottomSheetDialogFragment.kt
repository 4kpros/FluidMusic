package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.playlist.PlaylistItemAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetAddToPlaylistBinding
import com.prosabdev.fluidmusic.databinding.DialogNewPlaylistBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.utils.SystemSettingsUtils
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistSongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaylistAddFullBottomSheetDialogFragment : GenericFullBottomSheetDialogFragment() {

    private lateinit var mBottomSheetAddToPlaylistBinding: BottomSheetAddToPlaylistBinding

    private lateinit var mPlaylistItemViewModel: PlaylistItemViewModel
    private lateinit var mPlaylistSongItemViewModel: PlaylistSongItemViewModel

    private var mPlaylistItemAdapter: PlaylistItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mSongsToAddOnPlaylist : ArrayList<PlaylistSongItem>? = null
    private var mPlaylists : ArrayList<PlaylistItem>? = null
    private var mDefaultPlaylistCount : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetAddToPlaylistBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_add_to_playlist, container, false)
        val view = mBottomSheetAddToPlaylistBinding.root

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
                            val insertResult = insertMultiplesSongsToPlaylist(ctx, playlistId, playlistName)
                            if(insertResult)
                                dismiss()
                        }
                    }
                }

            })
            mPlaylistItemAdapter?.submitList(mPlaylists)
            mBottomSheetAddToPlaylistBinding.recyclerView.scrollToPosition(0)

            mLayoutManager = GridLayoutManager(ctx, 1, GridLayoutManager.VERTICAL, false)
            mBottomSheetAddToPlaylistBinding.recyclerView.adapter = mPlaylistItemAdapter
            mBottomSheetAddToPlaylistBinding.recyclerView.layoutManager = mLayoutManager
        }
    }

    private fun checkInteractions() {
        mBottomSheetAddToPlaylistBinding.buttonNewPlaylist.setOnClickListener{
            MainScope().launch {
                showAddNewPlaylistDialog()
            }
        }
    }

    private suspend fun showAddNewPlaylistDialog() {
        context?.let { ctx->
            val dialogNewPlaylistBinding : DialogNewPlaylistBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_new_playlist, null, false)
            if(dialogNewPlaylistBinding.textInputEditText.requestFocus()){
                dialogNewPlaylistBinding.textInputEditText.setText("$DEFAULT_PLAYLIST_NAME ${mDefaultPlaylistCount+1}")
                dialogNewPlaylistBinding.textInputEditText.selectAll()
                SystemSettingsUtils.SoftInputService(ctx, dialogNewPlaylistBinding.textInputEditText).show(5)
            }
            dialogNewPlaylistBinding.textInputEditText.addTextChangedListener(object : TextWatcher{
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
                    MainScope().launch {
                        handlePlaylistInputChanges(dialogNewPlaylistBinding, s)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    //
                }

            })

            MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.add_new_playlist))
                .setIcon(R.drawable.playlist_add)
                .setView(dialogNewPlaylistBinding.root)
                .setNegativeButton(ctx.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(ctx.getString(R.string.save)) { dialog, _ ->
                    val errorText = dialogNewPlaylistBinding.textInputLayout.error
                    val playlistNameText = dialogNewPlaylistBinding.textInputEditText.text
                    if((errorText == null || errorText.isEmpty()) && playlistNameText != null && playlistNameText.isNotEmpty()){
                        var hasSaved = false
                        MainScope().launch {
                            hasSaved = savePlaylistAndAddSong(ctx, dialogNewPlaylistBinding.textInputEditText.text.toString())
                        }
                        if(hasSaved){
                            dialog.dismiss()
                        }
                    }else{
                        MainScope().launch {
                            Toast.makeText(ctx, "Please enter valid playlist name !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        }
        dismiss()
    }

    private suspend fun savePlaylistAndAddSong(ctx : Context, playlistName: String): Boolean {
        return withContext(Dispatchers.Default){
            val pI = PlaylistItem()
            pI.name = playlistName
            pI.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
            val insertedPlaylistResult : Long = mPlaylistItemViewModel.insert(pI) ?: -1
            if(insertedPlaylistResult > 0){
                insertMultiplesSongsToPlaylist(ctx, insertedPlaylistResult, playlistName)
            }
            return@withContext false
        }
    }
    private suspend fun insertMultiplesSongsToPlaylist(ctx : Context, playlistId : Long, playlistName : String = ""): Boolean {
        return withContext(Dispatchers.Default){
            if(mSongsToAddOnPlaylist == null || (mSongsToAddOnPlaylist?.size ?: 0) <= 0)
                return@withContext false

            for (i in 0 until (mSongsToAddOnPlaylist?.size ?: 0)){
                mSongsToAddOnPlaylist!![i].playlistId = playlistId
            }
            val insertedSongsResult : List<Long> = mPlaylistSongItemViewModel.insertMultiple(mSongsToAddOnPlaylist) ?: ArrayList()
            var insertedCount = 0
            for (i in insertedSongsResult.indices){
                if(insertedSongsResult[i] > 0)
                    insertedCount++
            }
            if(insertedSongsResult.isNotEmpty()  && insertedCount > 0){
                MainScope().launch {
                    Toast.makeText(ctx, "$insertedCount Song(s) added to playlist $playlistName", Toast.LENGTH_SHORT).show()
                }
                return@withContext true
            }
            return@withContext false
        }
    }

    private suspend fun handlePlaylistInputChanges(
        dialogNewPlaylistBinding: DialogNewPlaylistBinding,
        sequence: CharSequence?
    ) {
        withContext(Dispatchers.Default){
            if(sequence == null || sequence.isEmpty()){
                updateInputUI(dialogNewPlaylistBinding, false)
            }else{
                val playlistExist = checkIfPlaylistExist(sequence.toString())
                if(playlistExist){
                    updateInputUI(dialogNewPlaylistBinding, false)
                }else{
                    updateInputUI(dialogNewPlaylistBinding, true)
                }
            }
        }
    }
    private fun updateInputUI(
        dialogNewPlaylistBinding: DialogNewPlaylistBinding,
        nameOk: Boolean,
        nameLength : Int = 0
    ) {
        MainScope().launch {
            if(nameOk){
                dialogNewPlaylistBinding.textInputLayout.error = null
            }else{
                if(nameLength <= 0){
                    dialogNewPlaylistBinding.textInputLayout.error = "Invalid playlist name !"
                }else{
                    dialogNewPlaylistBinding.textInputLayout.error = "Name already exists !"
                }
            }
        }
    }
    private suspend fun checkIfPlaylistExist(name : String): Boolean {
        val result = mPlaylistItemViewModel.getWithName(name)
        return result != null && result.name == name
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "PlaylistAddFullBottomSheetDialogFragment"
        const val DEFAULT_PLAYLIST_NAME : String = "Playlist"

        @JvmStatic
        fun newInstance(songsToAddOnPlaylist : ArrayList<PlaylistSongItem>?, playlists : ArrayList<PlaylistItem>?, defaultPlaylistCount : Long = 0) =
            PlaylistAddFullBottomSheetDialogFragment().apply {
                mSongsToAddOnPlaylist = songsToAddOnPlaylist
                mPlaylists = playlists
                mDefaultPlaylistCount = defaultPlaylistCount//Last default playlist name + index(counter)
            }
    }
}