package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.GenreItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.GenreItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenreItemRepository(ctx : Context) {

    private var mDao: GenreItemDao? = AppDatabase.getDatabase(ctx).genreItemDao()

    suspend fun getAtName(name : String?) : GenreItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String?) : LiveData<List<GenreItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String?) : List<GenreItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}