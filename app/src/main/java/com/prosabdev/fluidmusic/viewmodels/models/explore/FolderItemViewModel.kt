package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.FolderItemView
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.FolderItemRepository

class FolderItemViewModel(ctx : Context) : ViewModel() {

    private var repository: FolderItemRepository? = FolderItemRepository(ctx)

    suspend fun getAtName(name : String) : FolderItemView? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<FolderItemView>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}