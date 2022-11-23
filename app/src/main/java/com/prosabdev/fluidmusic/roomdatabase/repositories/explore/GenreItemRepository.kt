package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.GenreItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.GenreItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenreItemRepository(ctx : Context) {

    private var mDao: GenreItemDao? = AppDatabase.getDatabase(ctx).genreItemDao()

    suspend fun getAtName(name : String) : GenreItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<GenreItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}