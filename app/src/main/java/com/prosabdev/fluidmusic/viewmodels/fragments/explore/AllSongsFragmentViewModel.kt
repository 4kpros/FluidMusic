package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AllSongsFragmentViewModel(app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: SongItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "title")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
}
