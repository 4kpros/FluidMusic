package com.prosabdev.fluidmusic.roomdatabase.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.QueueMusicItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueMusicItemRepository(ctx : Context) {

    private var mDao: QueueMusicItemDao? = AppDatabase.getDatabase(ctx).queueMusicItemDao()

    suspend fun insert(queueMusicItem: QueueMusicItem?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(queueMusicItem)
        }
    }
    suspend fun update(queueMusicItem: QueueMusicItem?) {
        withContext(Dispatchers.IO){
            mDao?.update(queueMusicItem)
        }
    }
    suspend fun delete(queueMusicItem: QueueMusicItem?) {
        withContext(Dispatchers.IO){
            mDao?.delete(queueMusicItem)
        }
    }
    suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    //Getters
    suspend fun getAtId(id: Long) : QueueMusicItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll(order_by: String) : LiveData<List<QueueMusicItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_by)
        }
    }
}