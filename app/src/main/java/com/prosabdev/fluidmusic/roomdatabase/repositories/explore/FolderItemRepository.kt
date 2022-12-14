package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.ArtistItem
import com.prosabdev.fluidmusic.models.view.FolderItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.FolderItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderItemRepository(ctx : Context) {

    private var mDao: FolderItemDao? = AppDatabase.getDatabase(ctx).folderItemDao()

    suspend fun getAtName(name : String) : FolderItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<FolderItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<FolderItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}