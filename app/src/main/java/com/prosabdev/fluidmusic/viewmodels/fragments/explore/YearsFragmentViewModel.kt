package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.models.view.YearItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel

class YearsFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {

    init {
        loadPreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_YEARS,
            SORT_DEFAULT_VALUE,
            ORGANIZE_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

    suspend fun requestDataDirectlyFromDatabase(viewModel: YearItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { YearItem.DEFAULT_INDEX } ?: YearItem.DEFAULT_INDEX)
    }

    override fun onCleared() {
        super.onCleared()
        savePreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_YEARS,
            SORT_DEFAULT_VALUE,
            ORGANIZE_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

    companion object {

        const val ORGANIZE_DEFAULT_VALUE: Int = Constants.ORGANIZE_GRID_LARGE
        const val SORT_DEFAULT_VALUE: String = YearItem.DEFAULT_INDEX
        const val IS_INVERTED_DEFAULT_VALUE: Boolean = false

    }
}
