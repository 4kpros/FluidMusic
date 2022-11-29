package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.view.ArtistItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.ArtistItemRepository

class ArtistItemViewModel(ctx : Context) : ViewModel() {

    private var repository: ArtistItemRepository? = ArtistItemRepository(ctx)

    suspend fun getAtName(name : String) : ArtistItem? {
        return repository?.getAtName(name)
    }
    suspend fun getAll(order_by: String = "name") : LiveData<List<ArtistItem>>? {
        return repository?.getAll(order_by)
    }
}