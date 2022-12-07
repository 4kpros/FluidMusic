package com.prosabdev.fluidmusic.roomdatabase.repositories.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.playlist.PlaylistSongItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistSongItemRepository(ctx : Context) {

    private var mDao: PlaylistSongItemDao? = AppDatabase.getDatabase(ctx).playlistSongItemDao()

    suspend fun insert(playlistSongItem: PlaylistSongItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(playlistSongItem)
        }
    }
    suspend fun insertMultiple(playlistSongItems: ArrayList<PlaylistSongItem>?) : List<Long>? {
        return withContext(Dispatchers.IO){
            mDao?.insertMultiple(playlistSongItems)
        }
    }
    suspend fun update(playlistSongItem: PlaylistSongItem?) {
        withContext(Dispatchers.IO){
            mDao?.update(playlistSongItem)
        }
    }
    suspend fun delete(playlistSongItem: PlaylistSongItem?) {
        withContext(Dispatchers.IO){
            mDao?.delete(playlistSongItem)
        }
    }
    suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    //Getters
    suspend fun getAtId(id: Long) : PlaylistSongItem? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getAtId(id)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<PlaylistSongItem>?>? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<PlaylistSongItem>? {
        return withContext(Dispatchers.IO){
            return@withContext mDao?.getAllDirectly(orderBy)
        }
    }
}