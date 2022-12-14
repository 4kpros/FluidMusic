package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.ArtistItem
import com.prosabdev.fluidmusic.models.view.YearItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.YearItemRepository

class YearItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: YearItemRepository? = YearItemRepository(app)

    suspend fun getAtName(name : String) : YearItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String) : LiveData<List<YearItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String) : List<YearItem>? {
        return repository?.getAllDirectly(order_by)
    }
}