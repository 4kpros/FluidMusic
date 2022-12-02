package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.view.YearItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel
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
