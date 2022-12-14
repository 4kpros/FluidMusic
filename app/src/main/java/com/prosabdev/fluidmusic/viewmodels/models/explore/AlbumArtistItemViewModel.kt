package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.view.AlbumArtistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumArtistItemRepository

class AlbumArtistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: AlbumArtistItemRepository? = AlbumArtistItemRepository(app)

    suspend fun getAtName(name : String) : AlbumArtistItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String) : LiveData<List<AlbumArtistItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllDirectly(order_by: String) : List<AlbumArtistItem>? {
        return repository?.getAllDirectly(order_by)
    }
}