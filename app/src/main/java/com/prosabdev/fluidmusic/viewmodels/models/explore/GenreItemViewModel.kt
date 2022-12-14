package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.GenreItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.GenreItemRepository

class GenreItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: GenreItemRepository? = GenreItemRepository(app)

    suspend fun getAtName(name : String) : GenreItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String) : LiveData<List<GenreItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String) : List<GenreItem>? {
        return repository?.getAllDirectly(order_by)
    }
}