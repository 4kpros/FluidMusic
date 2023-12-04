package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.GenreItem
import com.prosabdev.common.roomdatabase.repositories.explore.GenreItemRepository

class GenreItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: GenreItemRepository? = GenreItemRepository(app)

    suspend fun getAtName(name : String) : GenreItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<GenreItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<GenreItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}