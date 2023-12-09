package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.ComposerItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ComposerItemViewModel

class ComposersFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: ComposerItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { ComposerItem.DEFAULT_INDEX } ?: ComposerItem.DEFAULT_INDEX)
    }
}
