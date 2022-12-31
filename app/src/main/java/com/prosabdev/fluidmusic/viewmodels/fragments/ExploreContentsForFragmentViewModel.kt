package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.common.media.MusicServiceConnection
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
        mMutableDataList.value = viewModel.getAllDirectlyWhereEqual(
            whereColumnIndex,
            columnValue,
            getSortBy().value?.ifEmpty { SongItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX
        )
    }

    companion object {
        private const val TAG = "ExploreContentsForFVM"
    }
}
