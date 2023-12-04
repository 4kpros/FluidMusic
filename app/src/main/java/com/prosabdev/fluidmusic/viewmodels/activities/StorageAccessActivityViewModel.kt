package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StorageAccessActivityViewModel(app: Application) : AndroidViewModel(app) {

    val requestRemoveAllFolderUriTrees = MutableLiveData<Int>(0)
}