package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.SongItem
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
    suspend fun getAllLimit(order_by: String, limit: Int) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllLimit(order_by, limit)
        }
    }
    suspend fun getAll(order_by: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_by)
        }
    }
    suspend fun getAllWhereEqual(whereColumn: String, columnValue: String?, order_by: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllWhereEqual(whereColumn, columnValue, order_by)
        }
    }
    suspend fun getAllWhereLike(whereColumn: String, columnValue: String?, order_by: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllWhereLike(whereColumn, columnValue, order_by)
        }
    }
}