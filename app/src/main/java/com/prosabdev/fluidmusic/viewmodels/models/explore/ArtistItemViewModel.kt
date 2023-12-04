package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.ArtistItem
import com.prosabdev.common.roomdatabase.repositories.explore.ArtistItemRepository

class ArtistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: ArtistItemRepository? = ArtistItemRepository(app)

    suspend fun getAtName(name : String) : ArtistItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String = "name") : LiveData<List<ArtistItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<ArtistItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}