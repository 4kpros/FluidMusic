package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.FolderItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.FolderItemViewModel

class FoldersFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: FolderItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { FolderItem.DEFAULT_INDEX } ?: FolderItem.DEFAULT_INDEX)
    }
}
