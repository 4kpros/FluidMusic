package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.FolderItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.FolderItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderItemRepository(ctx : Context) {

    private var mDao: FolderItemDao? = AppDatabase.getDatabase(ctx).folderItemDao()

    suspend fun getAtName(name : String?) : FolderItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String?) : LiveData<List<FolderItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String?) : List<FolderItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}