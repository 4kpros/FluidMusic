package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.AlbumItemView
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumItemRepository

class AlbumItemViewModel(ctx : Context) : ViewModel() {

    private var repository: AlbumItemRepository? = AlbumItemRepository(ctx)

    suspend fun getAtName(name : String) : AlbumItemView? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<AlbumItemView>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}