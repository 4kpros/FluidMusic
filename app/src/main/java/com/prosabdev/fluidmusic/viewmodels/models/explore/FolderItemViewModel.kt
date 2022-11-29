package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.view.FolderItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.FolderItemRepository

class FolderItemViewModel(ctx : Context) : ViewModel() {

    private var repository: FolderItemRepository? = FolderItemRepository(ctx)

    suspend fun getAtName(name : String) : FolderItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<FolderItem>>? {
        return repository?.getAll(order_by)
    }
}