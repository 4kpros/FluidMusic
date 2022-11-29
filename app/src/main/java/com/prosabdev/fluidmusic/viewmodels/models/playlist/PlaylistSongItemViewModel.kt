package com.prosabdev.fluidmusic.viewmodels.models.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.playlist.PlaylistSongItemRepository

class PlaylistSongItemViewModel(ctx : Context) : ViewModel() {

    private var repository: PlaylistSongItemRepository? = PlaylistSongItemRepository(ctx)

    suspend fun insert(playlistItem: PlaylistSongItem?) : Long? {
        return repository?.insert(playlistItem)
    }
    suspend fun insertMultiple(playlistSongItems: ArrayList<PlaylistSongItem>?) : List<Long>? {
        return repository?.insertMultiple(playlistSongItems)
    }
    suspend fun update(playlistItem: PlaylistSongItem?) {
        repository?.update(playlistItem)
    }
    suspend fun delete(playlistItem: PlaylistSongItem?) {
        repository?.delete(playlistItem)
    }
    suspend fun deleteAll() {
        repository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : PlaylistSongItem? {
        return repository?.getAtId(id)
    }
    suspend fun getAll(order_by: String = "id") : LiveData<List<PlaylistSongItem>>? {
        return repository?.getAll(order_by)
    }
}