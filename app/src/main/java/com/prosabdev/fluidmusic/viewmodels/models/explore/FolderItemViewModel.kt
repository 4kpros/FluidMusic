package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.FolderItem
import com.prosabdev.common.roomdatabase.repositories.explore.FolderItemRepository

class FolderItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: FolderItemRepository? = FolderItemRepository(app)

    suspend fun getAtName(name : String) : FolderItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<FolderItem>>? {
        return repository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<FolderItem>? {
        return repository?.getAllDirectly(orderBy)
    }
}