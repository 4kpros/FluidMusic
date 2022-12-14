package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.fluidmusic.models.view.ComposerItem
import com.prosabdev.fluidmusic.models.view.FolderItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ComposerItemViewModel

class ComposersFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: ComposerItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value?.ifEmpty { FolderItem.DEFAULT_INDEX } ?: ComposerItem.DEFAULT_INDEX)
    }
}
