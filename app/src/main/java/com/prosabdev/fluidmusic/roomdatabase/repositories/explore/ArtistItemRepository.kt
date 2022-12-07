package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.ArtistItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.ArtistItemDao
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
}