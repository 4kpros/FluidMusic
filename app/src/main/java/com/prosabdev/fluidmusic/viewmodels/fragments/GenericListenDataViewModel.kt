package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.common.constants.MainConst
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class GenericListenDataViewModel (app: Application) : AndroidViewModel(app) {
    val dataList = MutableLiveData<List<Any>>(null)
    val sortBy = MutableLiveData<String>("")
    val organizeListGrid = MutableLiveData<Int>(MainConst.ORGANIZE_LIST_MEDIUM)
    val isInverted = MutableLiveData<Boolean>(false)
}
