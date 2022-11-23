package com.prosabdev.fluidmusic.roomdatabase.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.PlaylistItem
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.PlaylistItemDao
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
    suspend fun getAtId(id: Long) : PlaylistItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<PlaylistItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}