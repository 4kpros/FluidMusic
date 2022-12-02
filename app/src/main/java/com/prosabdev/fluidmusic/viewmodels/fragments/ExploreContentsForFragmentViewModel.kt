package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel
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
