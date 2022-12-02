package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
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
}