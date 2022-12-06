package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumItemRepository

class AlbumItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: AlbumItemRepository? = AlbumItemRepository(app)

    suspend fun getAtName(name : String) : AlbumItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<AlbumItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String = "name") : List<AlbumItem>? {
        return repository?.getAllDirectly(order_by)
    }
}