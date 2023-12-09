package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.GenreItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.GenreItemViewModel

class GenresFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: GenreItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { GenreItem.DEFAULT_INDEX } ?: GenreItem.DEFAULT_INDEX)
    }
}
