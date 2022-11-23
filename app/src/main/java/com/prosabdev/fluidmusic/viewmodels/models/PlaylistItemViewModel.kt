package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.PlaylistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.PlaylistItemRepository

class PlaylistItemViewModel(ctx : Context) : ViewModel() {

    private var repository: PlaylistItemRepository? = PlaylistItemRepository(ctx)

    suspend fun insert(playlistItem: PlaylistItem?) : Long? {
        return repository?.insert(playlistItem)
    }
    suspend fun update(playlistItem: PlaylistItem?) {
        repository?.update(playlistItem)
    }
    suspend fun delete(playlistItem: PlaylistItem?) {
        repository?.delete(playlistItem)
    }
    suspend fun deleteAll() {
        repository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : PlaylistItem? {
        return repository?.getAtId(id)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<PlaylistItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}