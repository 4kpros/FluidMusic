package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumItemRepository

class AlbumItemViewModel(ctx : Context) : ViewModel() {

    private var repository: AlbumItemRepository? = AlbumItemRepository(ctx)

    suspend fun getAtName(name : String) : AlbumItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<AlbumItem>>? {
        return repository?.getAll(order_by)
    }
}