package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.YearItem
import com.prosabdev.common.roomdatabase.repositories.explore.YearItemRepository

class YearItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: YearItemRepository? = YearItemRepository(app)

    suspend fun getAtName(name : String) : YearItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<YearItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<YearItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}