package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.ComposerItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.ComposerItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComposerItemRepository(ctx : Context) {

    private var mDao: ComposerItemDao? = AppDatabase.getDatabase(ctx).composerItemDao()

    suspend fun getAtName(name : String) : ComposerItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<ComposerItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<ComposerItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}