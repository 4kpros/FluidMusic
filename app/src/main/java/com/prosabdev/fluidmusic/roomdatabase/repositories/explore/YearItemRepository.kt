package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.YearItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.YearItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YearItemRepository(ctx : Context) {

    private var mDao: YearItemDao? = AppDatabase.getDatabase(ctx).yearItemDao()

    suspend fun getAtName(name : String) : YearItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<YearItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}