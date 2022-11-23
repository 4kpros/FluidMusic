package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.YearItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.YearItemRepository

class YearItemViewModel(ctx : Context) : ViewModel() {

    private var repository: YearItemRepository? = YearItemRepository(ctx)

    suspend fun getAtName(name : String) : YearItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<YearItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}