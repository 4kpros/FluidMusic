package com.prosabdev.fluidmusic.roomdatabase.repositories.explore

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.explore.ComposerItemView
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.ComposerItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComposerItemRepository(ctx : Context) {

    private var mDao: ComposerItemDao? = AppDatabase.getDatabase(ctx).composerItemDao()

    suspend fun getAtName(name : String) : ComposerItemView? {
        return withContext(Dispatchers.IO){
            mDao?.getAtName(name)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<ComposerItemView>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}