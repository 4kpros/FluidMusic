package com.prosabdev.fluidmusic.viewmodels.models.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.roomdatabase.repositories.playlist.PlaylistItemRepository

class PlaylistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: PlaylistItemRepository? = PlaylistItemRepository(app)

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