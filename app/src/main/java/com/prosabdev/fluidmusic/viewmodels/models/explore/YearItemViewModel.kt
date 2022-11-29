package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.view.YearItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.YearItemRepository

class YearItemViewModel(ctx : Context) : ViewModel() {

    private var repository: YearItemRepository? = YearItemRepository(ctx)

    suspend fun getAtName(name : String) : YearItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<YearItem>>? {
        return repository?.getAll(order_by)
    }
}