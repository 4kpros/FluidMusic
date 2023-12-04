package com.prosabdev.common.roomdatabase.repositories.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.playlist.PlaylistItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistItemRepository(ctx : Context) {

    private var mDao: PlaylistItemDao? = AppDatabase.getDatabase(ctx).playlistItemDao()

    suspend fun insert(playlistItem: PlaylistItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(playlistItem)
        }
    }
    suspend fun update(playlistItem: PlaylistItem?): Int? {
        return withContext(Dispatchers.IO){
            mDao?.update(playlistItem)
        }
    }
    suspend fun delete(playlistItem: PlaylistItem?): Int? {
        return withContext(Dispatchers.IO){
            mDao?.delete(playlistItem)
        }
    }
    suspend fun deleteAll(): Int? {
        return withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }

    suspend fun getMaxIdLikeName(playlistName: String?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.getMaxIdLikeName(playlistName)
        }
    }
    suspend fun getWithName(name: String?) : PlaylistItem? {
        return withContext(Dispatchers.IO){
            mDao?.getWithName(name)
        }
    }
    suspend fun getAtId(id: Long) : PlaylistItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll(orderBy: String?) : LiveData<List<PlaylistItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
}