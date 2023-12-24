package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.fluidmusic.ui.fragments.explore.AlbumsFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class ExploreContentsForFragmentViewModel(
    app: Application
) : GenericListenDataViewModel(app) {

    private var mPrefsKey: String? = null
    fun loadPreferencesManually(
        preferencesKey: String?
    ){
        mPrefsKey = preferencesKey
        loadPreferences(
            mPrefsKey.toString(),
            SORT_DEFAULT_VALUE,
            ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

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

    override fun onCleared() {
        super.onCleared()
        savePreferences(
            mPrefsKey.toString(),
            SORT_DEFAULT_VALUE,
            ORGANIZE_LIST_GRID_DEFAULT_VALUE,
            IS_INVERTED_DEFAULT_VALUE
        )
    }

    companion object {

        const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = Constants.ORGANIZE_LIST_MEDIUM
        const val SORT_DEFAULT_VALUE: String = SongItem.DEFAULT_INDEX
        const val IS_INVERTED_DEFAULT_VALUE: Boolean = false

    }
}
