package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.FolderItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class AllSongsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    suspend fun requestDataDirectlyFromDatabase(viewModel: SongItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value?.ifEmpty { FolderItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX)
    }
}
