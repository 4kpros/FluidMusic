package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.AlbumArtistItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumArtistItemViewModel

class AlbumArtistsFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: AlbumArtistItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty {
            AlbumArtistItem.DEFAULT_INDEX
        } ?: AlbumArtistItem.DEFAULT_INDEX)
    }
}
