package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.GenreItemView
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.GenreItemRepository

class GenreItemViewModel(ctx : Context) : ViewModel() {

    private var repository: GenreItemRepository? = GenreItemRepository(ctx)

    suspend fun getAtName(name : String) : GenreItemView? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<GenreItemView>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}