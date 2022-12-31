package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.ComposerItem
import com.prosabdev.common.roomdatabase.repositories.explore.ComposerItemRepository

class ComposerItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: ComposerItemRepository? = ComposerItemRepository(app)

    suspend fun getAtName(name : String) : ComposerItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String) : LiveData<List<ComposerItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String) : List<ComposerItem>? {
        return repository?.getAllDirectly(order_by)
    }
}