package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.ComposerItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ComposerItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ComposersFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: ComposerItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "name")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
    suspend fun requestDataDirectlyFromDatabase(viewModel: ComposerItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value ?: ComposerItem.DEFAULT_INDEX)
    }
}
