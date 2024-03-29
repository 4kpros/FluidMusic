package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.YearItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.YearItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YearItemRepository(ctx : Context) {

    private var mDao: YearItemDao? = AppDatabase.getDatabase(ctx).yearItemDao()

    suspend fun getAtName(name : String) : YearItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<YearItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<YearItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}