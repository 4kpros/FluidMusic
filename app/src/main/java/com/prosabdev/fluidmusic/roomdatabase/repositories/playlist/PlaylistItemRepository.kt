package com.prosabdev.fluidmusic.roomdatabase.repositories.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.playlist.PlaylistItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistItemRepository(ctx : Context) {

    private var mDao: PlaylistItemDao? = AppDatabase.getDatabase(ctx).playlistItemDao()

    suspend fun insert(playlistItem: PlaylistItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(playlistItem)
        }
    }
    suspend fun update(playlistItem: PlaylistItem?) {
        withContext(Dispatchers.IO){
            mDao?.update(playlistItem)
        }
    }
    suspend fun delete(playlistItem: PlaylistItem?) {
        withContext(Dispatchers.IO){
            mDao?.delete(playlistItem)
        }
    }
    suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    //Getters
    suspend fun getMaxIdLikeName(playlistName: String) : Long? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getMaxIdLikeName(playlistName)
        }
    }
    suspend fun getWithName(name: String) : PlaylistItem? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getWithName(name)
        }
    }
    suspend fun getAtId(id: Long) : PlaylistItem? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getAtId(id)
        }
    }
    suspend fun getAll(order_by: String) : LiveData<List<PlaylistItem>>? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getAll(order_by)
        }
    }
}