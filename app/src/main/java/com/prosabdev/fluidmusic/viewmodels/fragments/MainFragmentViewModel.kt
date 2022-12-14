package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.util.SparseBooleanArray
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragmentViewModel(app: Application) : AndroidViewModel(app) {
    private val mMutableCurrentSelectablePage = MutableLiveData<String>(null)
    private val mMutableSelectMode = MutableLiveData<Boolean>(false)
    private val mMutableRequestToggleSelectRange = MutableLiveData<Int>(0)
    private val mMutableRequestToggleSelectAll = MutableLiveData<Int>(0)
    private val mMutableTotalCount = MutableLiveData<Int>(0)
    private val mMutableShowDrawerMenuCounter = MutableLiveData<Int>()
    private val mMutableSlidingUpPanelState = MutableLiveData<PanelState>(PanelState.COLLAPSED)
    private val mMutableIsFastScrolling = MutableLiveData<Boolean>(false)
    private val mMutableScrollingState = MutableLiveData<Int>(-2)
    private val mMutableShowSlidingUpPanelCounter = MutableLiveData<Int>(0)
    private val mMutableHideSlidingUpPanelCounter = MutableLiveData<Int>(0)
    private val mMutableSelectedDataList = MutableLiveData<HashMap<Int, String>>(HashMap())

    private val mCurrentSelectablePage: LiveData<String> get() = mMutableCurrentSelectablePage
    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mRequestToggleSelectRange: LiveData<Int> get() = mMutableRequestToggleSelectRange
    private val mRequestToggleSelectAll: LiveData<Int> get() = mMutableRequestToggleSelectAll
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount
    private val mShowDrawerMenuCounter: LiveData<Int> get() = mMutableShowDrawerMenuCounter
    private val mSlidingUpPanelState: LiveData<PanelState> get() = mMutableSlidingUpPanelState
    private val mIsFastScrolling: LiveData<Boolean> get() = mMutableIsFastScrolling
    private val mScrollingState: LiveData<Int> get() = mMutableScrollingState
    private val mShowSlidingUpPanelCounter: LiveData<Int> get() = mMutableShowSlidingUpPanelCounter
    private val mHideSlidingUpPanelCounter: LiveData<Int> get() = mMutableHideSlidingUpPanelCounter
    private val mSelectedDataList: LiveData<HashMap<Int, String>> get() = mMutableSelectedDataList

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
    fun setReQuestToggleSelectRange() {
        var tempValue: Int = mRequestToggleSelectRange.value ?: 0
        if(tempValue >= 100)
            tempValue = 0
        tempValue++
        MainScope().launch {
            mMutableRequestToggleSelectRange.value = tempValue
        }
    }
    fun getReQuestToggleSelectRange(): LiveData<Int> {
        return mRequestToggleSelectRange
    }
    fun setReQuestToggleSelectAll(){
        var tempValue: Int = mRequestToggleSelectAll.value ?: 0
        if(tempValue >= 100)
            tempValue = 0
        tempValue++
        MainScope().launch {
            mMutableRequestToggleSelectAll.value = tempValue
        }
    }
    fun getReQuestToggleSelectAll(): LiveData<Int> {
        return mRequestToggleSelectAll
    }
    fun getSelectedDataList() : LiveData<HashMap<Int, String>> {
        return mSelectedDataList
    }
    fun setSelectedDataList(value : HashMap<Int, String>) {
        MainScope().launch {
            mMutableSelectedDataList.value = value
        }
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
        var tempValue: Int = mShowDrawerMenuCounter.value ?: 0
        if(tempValue >= 100)
            tempValue = 0
        tempValue++
        MainScope().launch {
            mMutableShowDrawerMenuCounter.value = tempValue
        }
    }
    fun getShowDrawerMenuCounter() : LiveData<Int> {
        return mShowDrawerMenuCounter
    }
}
