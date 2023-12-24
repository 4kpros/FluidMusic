package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.media3.common.MediaItem
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class AllSongsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    init {
        loadPreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_ALL_SONGS,
            SORT_LIST_GRID_DEFAULT_VALUE,
            ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            IS_INVERTED_LIST_GRID_DEFAULT_VALUE
        )
    }

    suspend fun requestDataDirectlyFromDatabase(viewModel: SongItemViewModel){
        itemsList.value = viewModel.getAllDirectly(sortBy.value?.ifEmpty { SongItem.DEFAULT_INDEX } ?: SongItem.DEFAULT_INDEX)
    }

    override fun onCleared() {
        super.onCleared()
        savePreferences(
            PersistentStorage.SortAndOrganize.SORT_ORGANIZE_ALL_SONGS,
            SORT_LIST_GRID_DEFAULT_VALUE,
            ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            IS_INVERTED_LIST_GRID_DEFAULT_VALUE
        )
    }

    companion object {

        const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = Constants.ORGANIZE_LIST_MEDIUM
        const val SORT_LIST_GRID_DEFAULT_VALUE: String = SongItem.DEFAULT_INDEX
        const val IS_INVERTED_LIST_GRID_DEFAULT_VALUE: Boolean = false

    }
}
