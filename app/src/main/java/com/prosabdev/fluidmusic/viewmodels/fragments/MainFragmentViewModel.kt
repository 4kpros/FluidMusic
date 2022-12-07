package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragmentViewModel(app: Application) : AndroidViewModel(app) {
    private val mMutableCurrentSelectablePage = MutableLiveData<String>(null)
    private val mMutableSelectMode = MutableLiveData<Boolean>(false)
    private val mMutableToggleOnRange = MutableLiveData<Int>(0)
    private val mMutableTotalSelected = MutableLiveData<Int>(0)
    private val mMutableTotalCount = MutableLiveData<Int>(0)
    private val mMutableActivePage = MutableLiveData<Int>(0)
    private val mMutableShowDrawerMenuCounter = MutableLiveData<Int>()
    private val mMutableSlidingUpPanelState = MutableLiveData<PanelState>(PanelState.COLLAPSED)

    private val mMutableIsFastScrolling = MutableLiveData<Boolean>(false)
    private val mMutableScrollingState = MutableLiveData<Int>(-2)
    private val mMutableShowSlidingUpPanelCounter = MutableLiveData<Int>(0)
    private val mMutableHideSlidingUpPanelCounter = MutableLiveData<Int>(0)

    private val mCurrentSelectablePage: LiveData<String> get() = mMutableCurrentSelectablePage
    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mToggleOnRange: LiveData<Int> get() = mMutableToggleOnRange
    private val mTotalSelected: LiveData<Int> get() = mMutableTotalSelected
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount
    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    private val mShowDrawerMenuCounter: LiveData<Int> get() = mMutableShowDrawerMenuCounter
    private val mSlidingUpPanelState: LiveData<PanelState> get() = mMutableSlidingUpPanelState

    private val mIsFastScrolling: LiveData<Boolean> get() = mMutableIsFastScrolling
    private val mScrollingState: LiveData<Int> get() = mMutableScrollingState
    private val mShowSlidingUpPanelCounter: LiveData<Int> get() = mMutableShowSlidingUpPanelCounter
    private val mHideSlidingUpPanelCounter: LiveData<Int> get() = mMutableHideSlidingUpPanelCounter

    fun setIsFastScrolling(isFastScrolling : Boolean) {
        MainScope().launch {
            mMutableIsFastScrolling.value = isFastScrolling
        }
    }
    fun getIsFastScrolling(): LiveData<Boolean> {
        return mIsFastScrolling
    }
    fun setCurrentSelectablePage(page : String) {
        MainScope().launch {
            mMutableCurrentSelectablePage.value = page
        }
    }
    fun getCurrentSelectablePage(): LiveData<String> {
        return mCurrentSelectablePage
    }
    fun setSlidingUpPanelState(state : PanelState) {
        MainScope().launch {
            mMutableSlidingUpPanelState.value = state
        }
    }
    fun getSlidingUpPanelState(): LiveData<PanelState> {
        return mSlidingUpPanelState
    }
    fun setShowSlidingPanelCounter() {
        MainScope().launch {
            if(mSlidingUpPanelState.value != PanelState.EXPANDED){
                val tempValue : Int = (mShowSlidingUpPanelCounter.value ?: 0)+1
                mMutableShowSlidingUpPanelCounter.value = tempValue
            }
        }
    }
    fun getShowSlidingPanelCounter(): LiveData<Int> {
        return mShowSlidingUpPanelCounter
    }
    fun setHideSlidingPanelCounter() {
        MainScope().launch {
            if(mSlidingUpPanelState.value != PanelState.COLLAPSED) {
                val tempValue: Int = (mShowSlidingUpPanelCounter.value ?: 0) + 1
                mMutableHideSlidingUpPanelCounter.value = tempValue
            }
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
    fun getSelectMode(): LiveData<Boolean> {
        return mSelectMode
    }
    fun setSelectMode(value : Boolean) {
        MainScope().launch {
            mMutableSelectMode.value = value
        }
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
