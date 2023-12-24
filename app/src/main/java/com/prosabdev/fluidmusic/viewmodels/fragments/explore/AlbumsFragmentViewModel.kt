package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.models.view.YearItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel

class AlbumsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    init {
        loadPreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_YEARS,
            SORT_DEFAULT_VALUE,
            ORGANIZE_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

    suspend fun requestDataDirectlyFromDatabase(viewModel: AlbumItemViewModel){
        itemsList.value = viewModel.getAllDirectly(
            sortBy.value?.ifEmpty {
                AlbumItem.DEFAULT_INDEX
            } ?: AlbumItem.DEFAULT_INDEX)
    }

    override fun onCleared() {
        super.onCleared()
        savePreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_ALBUMS,
            SORT_DEFAULT_VALUE,
            ORGANIZE_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

    companion object {

        const val ORGANIZE_DEFAULT_VALUE: Int = Constants.ORGANIZE_GRID_LARGE
        const val SORT_DEFAULT_VALUE: String = AlbumItem.DEFAULT_INDEX
        const val IS_INVERTED_DEFAULT_VALUE: Boolean = false

    }
}
