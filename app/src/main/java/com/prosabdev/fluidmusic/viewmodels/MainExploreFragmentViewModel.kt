package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainExploreFragmentViewModel : ViewModel() {
    private val mMutableActivePage = MutableLiveData<Int>()
    private val mMutableActionBarState = MutableLiveData<Boolean>()

    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    val mActionBarState: LiveData<Boolean> get() = mMutableActionBarState

    fun setActivePage(page : Int) {
        mMutableActivePage.value = page
    }
    fun getActivePage(): LiveData<Int> {
        return mActivePage
    }
    fun setOnActionBarClickListened(item: Boolean) {
        mMutableActionBarState.value = item
    }
}