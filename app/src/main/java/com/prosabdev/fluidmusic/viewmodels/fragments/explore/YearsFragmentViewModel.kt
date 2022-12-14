package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.YearItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class YearsFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {

    fun listenAllData(viewModel: YearItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            viewModel.getAll(getSortBy().value ?: "name")?.observe(lifecycleOwner){
                mMutableDataList.value = it as ArrayList<Any>?
            }
        }
    }
    suspend fun requestDataDirectlyFromDatabase(viewModel: YearItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value ?: YearItem.DEFAULT_INDEX)
    }
}
