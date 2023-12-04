package com.prosabdev.fluidmusic.viewmodels.models.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.playlist.PlaylistSongItem
import com.prosabdev.common.roomdatabase.repositories.playlist.PlaylistSongItemRepository

class PlaylistSongItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: PlaylistSongItemRepository? = PlaylistSongItemRepository(app)

    suspend fun insert(playlistItem: PlaylistSongItem?) : Long? {
        return mRepository?.insert(playlistItem)
    }
    suspend fun insertMultiple(playlistSongItems: ArrayList<PlaylistSongItem>?) : List<Long>? {
        return mRepository?.insertMultiple(playlistSongItems)
    }
    suspend fun update(playlistItem: PlaylistSongItem?) {
        mRepository?.update(playlistItem)
    }
    suspend fun delete(playlistItem: PlaylistSongItem?) {
        mRepository?.delete(playlistItem)
    }
    suspend fun deleteAll() {
        mRepository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : PlaylistSongItem? {
        return mRepository?.getAtId(id)
    }
    suspend fun getAll(orderBy: String = "id") : LiveData<List<PlaylistSongItem>?>? {
        return mRepository?.getAll(orderBy)
    }
}