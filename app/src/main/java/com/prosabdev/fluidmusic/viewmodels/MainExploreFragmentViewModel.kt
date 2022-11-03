package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainExploreFragmentViewModel : ViewModel() {
    private val mMutableActionBarState = MutableLiveData<Boolean>()

    val mActionBarState: LiveData<Boolean> get() = mMutableActionBarState

    fun setOnActionBarClickListened(item: Boolean) {
        mMutableActionBarState.value = item
    }
}