package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.AlbumItemView
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.AlbumItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumItemRepository(ctx : Context) {

    private var mDao: AlbumItemDao? = AppDatabase.getDatabase(ctx).albumItemDao()

    suspend fun getAtName(name : String) : AlbumItemView? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<AlbumItemView>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}