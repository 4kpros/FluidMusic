package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.roomdatabase.repositories.explore.AlbumItemRepository

class AlbumItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: AlbumItemRepository? = AlbumItemRepository(app)

    suspend fun getAtName(name : String) : AlbumItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<AlbumItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<AlbumItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}