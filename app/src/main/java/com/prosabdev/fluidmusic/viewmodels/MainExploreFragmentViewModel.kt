package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainExploreFragmentViewModel : ViewModel() {
    private val mMutableSelectMode = MutableLiveData<Boolean>()
    private val mMutableTotalSelected = MutableLiveData<Int>()
    private val mMutableTotalCount = MutableLiveData<Int>()

    private val mMutableActivePage = MutableLiveData<Int>()
    private val mMutableActionBarState = MutableLiveData<Boolean>()

    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mTotalSelected: LiveData<Int> get() = mMutableTotalSelected
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount

    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    val mActionBarState: LiveData<Boolean> get() = mMutableActionBarState

    fun setSelectMode(value : Boolean) {
        mMutableSelectMode.value = value
    }
    fun getSelectMode(): LiveData<Boolean> {
        return mSelectMode
    }
    fun setTotalSelected(value : Int) {
        mMutableTotalSelected.value = value
    }
    fun getTotalSelected(): LiveData<Int> {
        return mTotalSelected
    }
    fun setTotalCount(value : Int) {
        mMutableTotalCount.value = value + (mTotalCount.value ?: 0)
    }
    fun getTotalCount(): LiveData<Int> {
        return mTotalCount
    }
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