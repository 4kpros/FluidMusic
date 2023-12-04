package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.view.AlbumArtistItem
import com.prosabdev.common.roomdatabase.repositories.explore.AlbumArtistItemRepository

class AlbumArtistItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: AlbumArtistItemRepository? = AlbumArtistItemRepository(app)

    suspend fun getAtName(name : String) : AlbumArtistItem? {
        return mRepository?.getAtName(name)
    }
    suspend fun getAll(orderBy: String) : LiveData<List<AlbumArtistItem>>? {
        return mRepository?.getAll(orderBy)
    }
    suspend fun getAllDirectly(orderBy: String) : List<AlbumArtistItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
}