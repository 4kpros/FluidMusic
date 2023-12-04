package com.prosabdev.fluidmusic.viewmodels.models.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.roomdatabase.repositories.playlist.PlaylistItemRepository

class PlaylistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: PlaylistItemRepository? = PlaylistItemRepository(app)

    suspend fun insert(playlistItem: PlaylistItem?) : Long? {
        return mRepository?.insert(playlistItem)
    }
    suspend fun update(playlistItem: PlaylistItem?) {
        mRepository?.update(playlistItem)
    }
    suspend fun delete(playlistItem: PlaylistItem?) {
        mRepository?.delete(playlistItem)
    }
    suspend fun deleteAll() {
        mRepository?.deleteAll()
    }

    //Getters
    suspend fun getMaxIdLikeName(playlistName: String) : Long? = mRepository?.getMaxIdLikeName(playlistName)

    suspend fun getAtId(id: Long) : PlaylistItem? = mRepository?.getAtId(id)

    suspend fun getWithName(name: String) : PlaylistItem? = mRepository?.getWithName(name)

    suspend fun getAll(order_by: String = "id") : LiveData<List<PlaylistItem>>? = mRepository?.getAll(order_by)
}