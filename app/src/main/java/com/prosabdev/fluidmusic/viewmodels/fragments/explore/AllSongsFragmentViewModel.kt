package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class AllSongsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    suspend fun requestDataDirectlyFromDatabase(viewModel: SongItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { SongItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX)
    }
}
