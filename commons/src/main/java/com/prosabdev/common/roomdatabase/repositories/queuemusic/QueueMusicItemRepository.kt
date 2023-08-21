package com.prosabdev.common.roomdatabase.repositories.queuemusic

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.queuemusic.QueueMusicItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.queuemusic.QueueMusicItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueMusicItemRepository(ctx : Context) {

    private var mDao: QueueMusicItemDao? = AppDatabase.getDatabase(ctx).queueMusicItemDao()

    suspend fun insert(queueMusicItem: QueueMusicItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(queueMusicItem)
        }
    }
    suspend fun update(queueMusicItem: QueueMusicItem?): Int? {
        return withContext(Dispatchers.IO){
            mDao?.update(queueMusicItem)
        }
    }
    suspend fun delete(queueMusicItem: QueueMusicItem?) {
        return withContext(Dispatchers.IO){
            mDao?.delete(queueMusicItem)
        }
    }
    suspend fun deleteAtId(id: Long): Int? {
        return withContext(Dispatchers.IO){
            mDao?.deleteAtId(id)
        }
    }
    suspend fun deleteMultiple(queueMusicItem: List<QueueMusicItem>?): Int? {
        return withContext(Dispatchers.IO){
            mDao?.deleteMultiple(queueMusicItem)
        }
    }
    suspend fun deleteAll(): Int? {
        return withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    //Getters
    suspend fun getAtId(id: Long) : QueueMusicItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll() : LiveData<List<QueueMusicItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll()
        }
    }
}