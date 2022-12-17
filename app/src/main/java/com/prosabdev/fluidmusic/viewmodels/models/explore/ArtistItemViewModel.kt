package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.ArtistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.ArtistItemRepository

class ArtistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: ArtistItemRepository? = ArtistItemRepository(app)

    suspend fun getAtName(name : String) : ArtistItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<ArtistItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String) : List<ArtistItem>? {
        return repository?.getAllDirectly(order_by)
    }
}