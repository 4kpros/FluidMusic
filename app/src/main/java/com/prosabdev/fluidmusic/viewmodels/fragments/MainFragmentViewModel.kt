package com.prosabdev.fluidmusic.viewmodels.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainFragmentViewModel : ViewModel() {
    private val mMutableSelectMode = MutableLiveData<Boolean>(false)
    private val mMutableToggleOnRange = MutableLiveData<Int>(0)
    private val mMutableTotalSelected = MutableLiveData<Int>(0)
    private val mMutableTotalCount = MutableLiveData<Int>(0)
    private val mMutableActivePage = MutableLiveData<Int>(0)
    private val mMutableActionBarState = MutableLiveData<Boolean>()
    private val mMutableScrollingState = MutableLiveData<Int>(-2)

    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mToggleOnRange: LiveData<Int> get() = mMutableToggleOnRange
    private val mTotalSelected: LiveData<Int> get() = mMutableTotalSelected
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount
    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    private val mActionBarState: LiveData<Boolean> get() = mMutableActionBarState
    private val mScrollingState: LiveData<Int> get() = mMutableScrollingState

    fun setScrollingState(value : Int) {
        mMutableScrollingState.value = value
    }
    fun getScrollingState(): LiveData<Int> {
        return mScrollingState
    }
    fun setSelectMode(value : Boolean) {
        mMutableSelectMode.value = value
    }
    fun getSelectMode(): LiveData<Boolean> {
        return mSelectMode
    }
    fun setToggleRange() {
        var tempToggleRange: Int = mToggleOnRange.value ?: 0
        if(tempToggleRange >= 100)
            tempToggleRange = 0
        tempToggleRange++
        mMutableToggleOnRange.value = tempToggleRange
    }
    fun getToggleRange(): LiveData<Int> {
        return mToggleOnRange
    }
    fun setTotalSelected(value : Int) {
        mMutableTotalSelected.value = value
    }
    fun getTotalSelected(): LiveData<Int> {
        return mTotalSelected
    }
    fun setTotalCount(value : Int) {
        mMutableTotalCount.value = value
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
    fun getOnActionBarClickListened() : LiveData<Boolean> {
        return mActionBarState
    }
}
