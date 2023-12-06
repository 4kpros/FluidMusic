package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.ArtistItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel

class ArtistsFragmentViewModel(app: Application) : GenericListenDataViewModel(app){
    suspend fun requestDataDirectlyFromDatabase(viewModel: ArtistItemViewModel){
        dataList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { ArtistItem.DEFAULT_INDEX } ?: ArtistItem.DEFAULT_INDEX)
    }
}
