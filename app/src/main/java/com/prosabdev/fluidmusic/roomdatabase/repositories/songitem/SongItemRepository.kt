package com.prosabdev.fluidmusic.roomdatabase.repositories.songitem

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.SongItemDao
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
    suspend fun getAllLimit(orderBy: String, limit: Int) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllLimit(orderBy, limit)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<SongItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
    suspend fun getAllWhereEqual(whereColumn: String, columnValue: String?, orderBy: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllWhereEqual(whereColumn, columnValue, orderBy)
        }
    }
    suspend fun getAllWhereLike(whereColumn: String, columnValue: String?, orderBy: String) : LiveData<List<SongItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllWhereLike(whereColumn, columnValue, orderBy)
        }
    }
}