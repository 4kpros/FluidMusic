package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class StorageAccessActivityViewModel(app: Application) : AndroidViewModel(app) {

    val requestRemoveAllFolderUriTrees = MutableLiveData<Int>(0)
}