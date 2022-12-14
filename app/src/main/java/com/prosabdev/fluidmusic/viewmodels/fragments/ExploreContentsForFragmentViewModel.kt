package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class ExploreContentsForFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    suspend fun requestDataDirectlyWhereColumnEqualFromDatabase(
        viewModel: SongItemViewModel,
        whereColumnIndex: String,
        columnValue: String?
    ){
        mMutableDataList.value = viewModel.getAllDirectlyWhereEqual(
            whereColumnIndex,
            columnValue,
            getSortBy().value?.ifEmpty { SongItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX
        )
    }
}
