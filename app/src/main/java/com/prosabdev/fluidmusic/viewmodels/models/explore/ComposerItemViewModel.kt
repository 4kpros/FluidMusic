package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.ComposerItemView
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.ComposerItemRepository

class ComposerItemViewModel(ctx : Context) : ViewModel() {

    private var repository: ComposerItemRepository? = ComposerItemRepository(ctx)

    suspend fun getAtName(name : String) : ComposerItemView? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<ComposerItemView>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}