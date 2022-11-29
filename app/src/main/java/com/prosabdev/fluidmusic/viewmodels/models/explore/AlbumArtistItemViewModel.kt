package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.view.AlbumArtistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumArtistItemRepository

class AlbumArtistItemViewModel(ctx : Context) : ViewModel() {

    private var repository: AlbumArtistItemRepository? = AlbumArtistItemRepository(ctx)

    suspend fun getAtName(name : String) : AlbumArtistItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<AlbumArtistItem>>? {
        return repository?.getAll(order_by)
    }
}