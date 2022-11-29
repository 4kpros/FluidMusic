package com.prosabdev.fluidmusic.viewmodels.models.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.playlist.PlaylistItemRepository

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
    suspend fun getMaxIdLikeName(playlistName: String) : Long? = repository?.getMaxIdLikeName(playlistName)

    suspend fun getAtId(id: Long) : PlaylistItem? = repository?.getAtId(id)

    suspend fun getWithName(name: String) : PlaylistItem? = repository?.getWithName(name)

    suspend fun getAll(order_by: String = "id") : LiveData<List<PlaylistItem>>? = repository?.getAll(order_by)
}