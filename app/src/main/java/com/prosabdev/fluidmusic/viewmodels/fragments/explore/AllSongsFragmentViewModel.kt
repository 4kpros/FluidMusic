package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
