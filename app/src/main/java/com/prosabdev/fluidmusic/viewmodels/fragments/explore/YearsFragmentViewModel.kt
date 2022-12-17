package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import com.prosabdev.fluidmusic.models.view.YearItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.YearItemViewModel

class YearsFragmentViewModel (app: Application) : GenericListenDataViewModel(app) {
    suspend fun requestDataDirectlyFromDatabase(viewModel: YearItemViewModel){
        mMutableDataList.value = viewModel.getAllDirectly(getSortBy().value?.ifEmpty { YearItem.DEFAULT_INDEX } ?: YearItem.DEFAULT_INDEX)
    }
}
