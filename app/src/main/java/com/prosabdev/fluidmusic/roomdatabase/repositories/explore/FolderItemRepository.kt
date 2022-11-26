package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.FolderItemView
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.FolderItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderItemRepository(ctx : Context) {

    private var mDao: FolderItemDao? = AppDatabase.getDatabase(ctx).folderItemDao()

    suspend fun getAtName(name : String) : FolderItemView? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<FolderItemView>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}