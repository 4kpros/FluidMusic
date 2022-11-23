package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.AlbumArtistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumArtistItemRepository


class SongItemViewModel(ctx : Context) : ViewModel() {

    private var repository: AlbumArtistItemRepository? = AlbumArtistItemRepository(ctx)

    suspend fun getAtName(name : String) : AlbumArtistItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<AlbumArtistItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}