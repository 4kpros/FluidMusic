package com.prosabdev.common.roomdatabase.repositories.songitem

import android.content.Context
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.SongItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongItemRepository(ctx : Context) {

    private var mDao: SongItemDao? = AppDatabase.getDatabase(ctx).songItemDao()

    suspend fun insert(songItem: SongItem) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(songItem)
        }
    }
    suspend fun update(songItem: SongItem): Int? {
        return withContext(Dispatchers.IO){
            mDao?.update(songItem)
        }
    }
    suspend fun delete(songItem: SongItem): Int? {
        return withContext(Dispatchers.IO){
            mDao?.delete(songItem)
        }
    }
    suspend fun deleteAll(): Int? {
        return withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }

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
    suspend fun getAllDirectlyLimit(orderBy: String, limit: Int) : List<SongItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectlyLimit(orderBy, limit)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<SongItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
    suspend fun getAllDirectlyWhereEqual(whereColumn: String, columnValue: String, orderBy: String) : List<SongItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectlyWhereEqual(whereColumn, columnValue, orderBy)
        }
    }
    suspend fun getAllDirectlyWhereLike(whereColumn: String, columnValue: String, orderBy: String) : List<SongItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectlyWhereLike(whereColumn, columnValue, orderBy)
        }
    }
}