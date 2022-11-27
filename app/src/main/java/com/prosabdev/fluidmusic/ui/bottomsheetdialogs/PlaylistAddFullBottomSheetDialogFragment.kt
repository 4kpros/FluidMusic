package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.playlist.PlaylistItemAdapter
import com.prosabdev.fluidmusic.databinding.BottomSheetAddToPlaylistBinding
import com.prosabdev.fluidmusic.databinding.DialogNewPlaylistBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.SystemSettingsUtils
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.playlist.PlaylistSongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaylistAddFullBottomSheetDialogFragment(private val mPlaylistAdd : ArrayList<PlaylistSongItem>) : GenericFullBottomSheetDialogFragment() {

    private lateinit var mBottomSheetAddToPlaylistBinding: BottomSheetAddToPlaylistBinding

    private lateinit var mPlaylistItemViewModel: PlaylistItemViewModel
    private lateinit var mPlaylistSongItemViewModel: PlaylistSongItemViewModel

    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    private var mPlaylistItemAdapter: PlaylistItemAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mDefaultPlaylistName : String = "Playlist"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetAddToPlaylistBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_add_to_playlist, container, false)
        val view = mBottomSheetAddToPlaylistBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        checkInteractions()
        observeLiveData()
    }

    private fun setupRecyclerView() {
        val ctx : Context = this.context ?: return
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
        //Setup empty bottom space adapter
        val listEmptyBottomSpace : ArrayList<String> = ArrayList()
        listEmptyBottomSpace.add("")
        mEmptyBottomAdapter = EmptyBottomAdapter(listEmptyBottomSpace)

        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mPlaylistItemAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)

        mLayoutManager = GridLayoutManager(ctx, 1, GridLayoutManager.VERTICAL, false)
        mBottomSheetAddToPlaylistBinding.recyclerView.adapter = concatAdapter
        mBottomSheetAddToPlaylistBinding.recyclerView.layoutManager = mLayoutManager
        MainScope().launch {
        }
    }

    private fun observeLiveData() {
        MainScope().launch {
            mPlaylistItemViewModel.getAll()?.observe(activity as LifecycleOwner){
                mPlaylistItemAdapter?.submitList(it)
                mBottomSheetAddToPlaylistBinding.recyclerView.scrollToPosition(0)
            }
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
        val ctx : Context = this.context ?: return

        val dialogNewPlaylistBinding : DialogNewPlaylistBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_new_playlist, null, false)
        if(dialogNewPlaylistBinding.textInputEditText.requestFocus()){
            val defaultPlaylistCount : Long = mPlaylistItemViewModel.getMaxIdLikeName(mDefaultPlaylistName) ?: 0
            dialogNewPlaylistBinding.textInputEditText.setText("$mDefaultPlaylistName ${defaultPlaylistCount+1}")
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
                        Toast.makeText(ctx, "Please enter correct playlist name !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
        dismiss()
    }

//    private suspend fun insertSongToPlaylist(ctx : Context, songId : Long, playlistId: Long, playlistName: String = ""){
//        if(songId <= 0 || playlistId <= 0)
//            return
//
//        withContext(Dispatchers.Default){
//            val psI = PlaylistSongItem()
//            psI.songId = songId
//            psI.playlistId = playlistId
//            psI.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
//            val insertedSongsResult : Long = mPlaylistSongItemViewModel.insert(psI) ?: -1
//            if(insertedSongsResult > 0){
//                MainScope().launch {
//                    Toast.makeText(ctx, "Song added to playlist $playlistName", Toast.LENGTH_SHORT).show()
//                    dismiss()
//                }
//            }
//        }
//    }

    private suspend fun savePlaylistAndAddSong(ctx : Context, playlistName: String): Boolean {
        return withContext(Dispatchers.Default){
            val pI = PlaylistItem()
            pI.name = playlistName
            pI.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
            val insertedPlaylistResult : Long = mPlaylistItemViewModel.insert(pI) ?: -1
            Log.i(ConstantValues.TAG, "Refuse to insert : ${insertedPlaylistResult}")
            if(insertedPlaylistResult > 0){
                insertMultiplesSongsToPlaylist(ctx, insertedPlaylistResult, playlistName)
            }
            return@withContext false
        }
    }
    private suspend fun insertMultiplesSongsToPlaylist(ctx : Context, playlistId : Long, playlistName : String = ""): Boolean {
        return withContext(Dispatchers.Default){
            if(mPlaylistAdd.size <= 0)
                return@withContext false

            for (i in mPlaylistAdd.indices){
                mPlaylistAdd[i].playlistId = playlistId
            }
            val insertedSongsResult : List<Long> = mPlaylistSongItemViewModel.insertMultiple(mPlaylistAdd) ?: ArrayList()
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
                    dialogNewPlaylistBinding.textInputLayout.error = "Name already exists"
                }
            }
        }
    }
    private suspend fun checkIfPlaylistExist(name : String): Boolean {
        val result = mPlaylistItemViewModel.getWithName(name)
        return result != null && result.name == name
    }

    private fun initViews() {
        val ctx : Context = this.context ?: return

        mPlaylistItemViewModel = ModelsViewModelFactory(ctx).create(
            PlaylistItemViewModel::class.java
        )
        mPlaylistSongItemViewModel = ModelsViewModelFactory(ctx).create(
            PlaylistSongItemViewModel::class.java
        )
    }

    companion object {
        const val TAG = "PlaylistAddBottomSheetDialog"
    }
}