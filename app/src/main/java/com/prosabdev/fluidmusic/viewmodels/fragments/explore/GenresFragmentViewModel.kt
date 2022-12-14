package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.GenreItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.GenreItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GenresFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: GenreItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "name")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
    suspend fun requestDataDirectlyFromDatabase(viewModel: GenreItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value ?: GenreItem.DEFAULT_INDEX)
    }
}
