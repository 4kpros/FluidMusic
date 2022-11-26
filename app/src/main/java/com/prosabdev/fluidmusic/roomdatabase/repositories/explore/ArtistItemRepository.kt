package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.ArtistItemView
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.ArtistItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtistItemRepository(ctx : Context) {

    private var mDao: ArtistItemDao? = AppDatabase.getDatabase(ctx).artistItemDao()

    suspend fun getAtName(name : String) : ArtistItemView? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<ArtistItemView>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}