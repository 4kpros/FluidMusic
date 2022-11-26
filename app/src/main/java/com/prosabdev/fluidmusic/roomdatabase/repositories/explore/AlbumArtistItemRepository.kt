package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.AlbumArtistItemView
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.AlbumArtistItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumArtistItemRepository(ctx : Context) {

    private var mDao: AlbumArtistItemDao? = AppDatabase.getDatabase(ctx).albumArtistItemDao()

    suspend fun getAtName(name : String) : AlbumArtistItemView? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<AlbumArtistItemView>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}