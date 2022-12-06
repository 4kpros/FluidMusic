package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.AlbumItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumItemRepository(ctx : Context) {

    private var mDao: AlbumItemDao? = AppDatabase.getDatabase(ctx).albumItemDao()

    suspend fun getAtName(name : String) : AlbumItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_by: String) : LiveData<List<AlbumItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_by)
        }
    }
    suspend fun getAllDirectly(order_by: String) : List<AlbumItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(order_by)
        }
    }
}