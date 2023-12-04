package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.ComposerItem
import com.prosabdev.common.roomdatabase.repositories.explore.ComposerItemRepository

class ComposerItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: ComposerItemRepository? = ComposerItemRepository(app)

    suspend fun getAtName(name : String) : ComposerItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<ComposerItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<ComposerItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}