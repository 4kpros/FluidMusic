package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel

class AlbumsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: AlbumItemViewModel){
        dataList.value = viewModel.getAllDirectly(
            sortBy.value?.ifEmpty {
                AlbumItem.DEFAULT_INDEX
            } ?: AlbumItem.DEFAULT_INDEX)
    }
}
