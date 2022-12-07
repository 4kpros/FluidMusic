package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.SongItemViewModel

class AllSongsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    suspend fun listenAllData(viewModel: SongItemViewModel, lifecycleOwner: LifecycleOwner){
        viewModel.getAll(getSortBy().value ?: "title")?.observe(lifecycleOwner){
            mMutableDataList.value = it
        }
    }
    suspend fun requestDataDirectlyFromDatabase(viewModel: SongItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value ?: "title")
    }
}
