package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class ExploreContentsForFragmentViewModel(
    app: Application
) : GenericListenDataViewModel(app) {

    suspend fun requestDataDirectlyWhereColumnEqualFromDatabase(
        viewModel: SongItemViewModel,
        whereColumnIndex: String,
        columnValue: String?
    ){
        itemsList.value = viewModel.getAllDirectlyWhereEqual(
            whereColumnIndex,
            columnValue,
            sortBy.value?.ifEmpty { SongItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX
        )
    }

    companion object {
        private const val TAG = "ExploreContentsForFVM"
    }
}
