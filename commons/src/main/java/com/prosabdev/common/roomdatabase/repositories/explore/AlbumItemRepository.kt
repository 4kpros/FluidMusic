package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.AlbumItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumItemRepository(ctx : Context) {

    private var mDao: AlbumItemDao? = AppDatabase.getDatabase(ctx).albumItemDao()

    suspend fun getAtName(name : String?) : AlbumItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String?) : LiveData<List<AlbumItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String?) : List<AlbumItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}