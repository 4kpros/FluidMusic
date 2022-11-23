package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.SongItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongItemRepository(ctx : Context) {

    private var mDao: SongItemDao? = AppDatabase.getDatabase(ctx).songItemDao()

    suspend fun insert(songItem: SongItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(songItem)
        }
    }
    suspend fun update(songItem: SongItem?) {
        withContext(Dispatchers.IO){
            mDao?.update(songItem)
        }
    }
    suspend fun delete(songItem: SongItem?) {
        withContext(Dispatchers.IO){
            mDao?.delete(songItem)
        }
    }
    suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    //Getters
    suspend fun getFirstSong() : SongItem? {
        return withContext(Dispatchers.IO){
            mDao?.getFirstSong()
        }
    }
    suspend fun getAtId(id: Long) : SongItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAtUri(uri: String) : SongItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtUri(uri)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
    suspend fun getAllWithWhereClause(whereColumn: String?, columnValue: String?, order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllWithWhereClause(whereColumn, columnValue, order_name, asc_desc_mode)
        }
    }
}