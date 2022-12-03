package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ExploreContentsForFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: SongItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "title")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
}
