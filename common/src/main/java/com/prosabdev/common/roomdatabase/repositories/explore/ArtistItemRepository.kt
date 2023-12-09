package com.prosabdev.common.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.ArtistItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.explore.ArtistItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtistItemRepository(ctx : Context) {

    private var mDao: ArtistItemDao? = AppDatabase.getDatabase(ctx).artistItemDao()

    suspend fun getAtName(name : String) : ArtistItem? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(orderBy: String) : LiveData<List<ArtistItem>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
    suspend fun getAllDirectly(orderBy: String) : List<ArtistItem>? {
        return withContext(Dispatchers.IO){
            mDao?.getAllDirectly(orderBy)
        }
    }
}