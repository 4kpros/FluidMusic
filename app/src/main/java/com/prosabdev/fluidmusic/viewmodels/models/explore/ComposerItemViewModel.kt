package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.ComposerItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.ComposerItemRepository

class ComposerItemViewModel(ctx : Context) : ViewModel() {

    private var repository: ComposerItemRepository? = ComposerItemRepository(ctx)

    suspend fun getAtName(name : String) : ComposerItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<ComposerItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}