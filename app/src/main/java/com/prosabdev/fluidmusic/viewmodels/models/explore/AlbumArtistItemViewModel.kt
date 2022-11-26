package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.AlbumArtistItemView
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.AlbumArtistItemRepository

class AlbumArtistItemViewModel(ctx : Context) : ViewModel() {

    private var repository: AlbumArtistItemRepository? = AlbumArtistItemRepository(ctx)

    suspend fun getAtName(name : String) : AlbumArtistItemView? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<AlbumArtistItemView>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}