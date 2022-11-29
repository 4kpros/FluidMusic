package com.prosabdev.fluidmusic.viewmodels.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragmentViewModel : ViewModel() {
    private val mMutableSelectMode = MutableLiveData<Boolean>(false)
    private val mMutableToggleOnRange = MutableLiveData<Int>(0)
    private val mMutableTotalSelected = MutableLiveData<Int>(0)
    private val mMutableTotalCount = MutableLiveData<Int>(0)
    private val mMutableActivePage = MutableLiveData<Int>(0)
    private val mMutableShowDrawerMenuCounter = MutableLiveData<Int>()
    private val mMutableScrollingState = MutableLiveData<Int>(-2)

    private val mMutableShowSlidingUpPanelCounter = MutableLiveData<Int>(0)
    private val mMutableHideSlidingUpPanelCounter = MutableLiveData<Int>(0)

    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mToggleOnRange: LiveData<Int> get() = mMutableToggleOnRange
    private val mTotalSelected: LiveData<Int> get() = mMutableTotalSelected
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount
    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    private val mShowDrawerMenuCounter: LiveData<Int> get() = mMutableShowDrawerMenuCounter
    private val mScrollingState: LiveData<Int> get() = mMutableScrollingState

    private val mShowSlidingUpPanelCounter: LiveData<Int> get() = mMutableShowSlidingUpPanelCounter
    private val mHideSlidingUpPanelCounter: LiveData<Int> get() = mMutableHideSlidingUpPanelCounter

    fun setShowSlidingPanelCounter() {
        MainScope().launch {
            val tempValue : Int = (mShowSlidingUpPanelCounter.value ?: 0)+1
            mMutableShowSlidingUpPanelCounter.value = tempValue
        }
    }
    fun getShowSlidingPanelCounter(): LiveData<Int> {
        return mShowSlidingUpPanelCounter
    }
    fun setHideSlidingPanelCounter() {
        MainScope().launch {
            val tempValue : Int = (mShowSlidingUpPanelCounter.value ?: 0)+1
            mMutableHideSlidingUpPanelCounter.value = tempValue
        }
    }
    fun getHideSlidingPanelCounter(): LiveData<Int> {
        return mHideSlidingUpPanelCounter
    }

    fun setScrollingState(value : Int) {
        MainScope().launch {
            mMutableScrollingState.value = value
        }
    }
    fun getScrollingState(): LiveData<Int> {
        return mScrollingState
    }
    fun setSelectMode(value : Boolean) {
        MainScope().launch {
            mMutableSelectMode.value = value
        }
    }
    fun getSelectMode(): LiveData<Boolean> {
        return mSelectMode
    }
    fun setToggleRange() {
        MainScope().launch {
            var tempToggleRange: Int = mToggleOnRange.value ?: 0
            if(tempToggleRange >= 100)
                tempToggleRange = 0
            tempToggleRange++
            mMutableToggleOnRange.value = tempToggleRange
        }
    }
    fun getToggleRange(): LiveData<Int> {
        return mToggleOnRange
    }
    fun setTotalSelected(value : Int) {
        MainScope().launch {
            mMutableTotalSelected.value = value
        }
    }
    fun getTotalSelected(): LiveData<Int> {
        return mTotalSelected
    }
    fun setTotalCount(value : Int) {
        MainScope().launch {
            mMutableTotalCount.value = value
        }
    }
    fun getTotalCount(): LiveData<Int> {
        return mTotalCount
    }
    fun setActivePage(page : Int) {
        MainScope().launch {
            mMutableActivePage.value = page
        }
    }
    fun getActivePage(): LiveData<Int> {
        return mActivePage
    }
    fun setShowDrawerMenuCounter() {
        MainScope().launch {
            var tempShowDrawer: Int = mShowDrawerMenuCounter.value ?: 0
            if(tempShowDrawer >= 100)
                tempShowDrawer = 0
            tempShowDrawer++
            mMutableShowDrawerMenuCounter.value = tempShowDrawer
        }
    }
    fun getShowDrawerMenuCounter() : LiveData<Int> {
        return mShowDrawerMenuCounter
    }
}
