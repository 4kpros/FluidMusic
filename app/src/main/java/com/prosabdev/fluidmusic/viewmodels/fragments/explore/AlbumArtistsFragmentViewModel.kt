package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.AlbumArtistItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumArtistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumArtistsFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: AlbumArtistItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "name")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
    suspend fun requestDataDirectlyFromDatabase(viewModel: AlbumArtistItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value ?: AlbumArtistItem.DEFAULT_INDEX)
    }
}
