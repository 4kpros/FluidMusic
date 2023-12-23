package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.prosabdev.common.components.Constants

abstract class GenericListenDataViewModel (app: Application) : AndroidViewModel(app) {
    val itemsList = MutableLiveData<List<Any>>(null)
    val sortBy = MutableLiveData<String>("")
    val organizeListGrid = MutableLiveData<Int>(Constants.ORGANIZE_LIST_MEDIUM)
    val isInverted = MutableLiveData<Boolean>(false)
}
