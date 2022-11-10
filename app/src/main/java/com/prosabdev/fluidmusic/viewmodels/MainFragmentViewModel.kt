package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainFragmentViewModel : ViewModel() {
    private val mMutableSelectMode = MutableLiveData<Boolean>(false)
    private val mMutableIsAllSelected = MutableLiveData<Boolean>(false)
    private val mMutableIsRangeSelected = MutableLiveData<Boolean>(false)
    private val mMutableTotalSelected = MutableLiveData<Int>(0)
    private val mMutableTotalCount = MutableLiveData<Int>(0)
    private val mMutableMinimumSelectedIndex = MutableLiveData<Int>(-1)
    private val mMutableMaximumSelectedIndex = MutableLiveData<Int>(-1)
    private val mMutableActivePage = MutableLiveData<Int>(0)
    private val mMutableActionBarState = MutableLiveData<Boolean>()

    private val mSelectMode: LiveData<Boolean> get() = mMutableSelectMode
    private val mutableIsAllSelected: LiveData<Boolean> get() = mMutableIsAllSelected
    private val mutableIsRangeSelected: LiveData<Boolean> get() = mMutableIsRangeSelected
    private val mTotalSelected: LiveData<Int> get() = mMutableTotalSelected
    private val mTotalCount: LiveData<Int> get() = mMutableTotalCount
    private val mMinimumSelectedIndex: LiveData<Int> get() = mMutableMinimumSelectedIndex
    private val mMaximumSelectedIndex: LiveData<Int> get() = mMutableMaximumSelectedIndex
    private val mActivePage: LiveData<Int> get() = mMutableActivePage
    val mActionBarState: LiveData<Boolean> get() = mMutableActionBarState

    fun setSelectMode(value : Boolean) {
        mMutableSelectMode.value = value
    }
    fun getSelectMode(): LiveData<Boolean> {
        return mSelectMode
    }
    fun setIsAllSelected(value : Boolean) {
        mMutableIsAllSelected.value = value
    }
    fun getIsAllSelected(): LiveData<Boolean> {
        return mutableIsAllSelected
    }
    fun setIsRangeSelected(value : Boolean) {
        mMutableIsRangeSelected.value = value
    }
    fun getIsRangeSelected(): LiveData<Boolean> {
        return mutableIsRangeSelected
    }
    fun setMinimumSelectedIndex(value : Int) {
        mMutableMinimumSelectedIndex.value = value
    }
    fun getMinimumSelectedIndex(): LiveData<Int> {
        return mMinimumSelectedIndex
    }
    fun setMaximumSelectedIndex(value : Int) {
        mMutableMaximumSelectedIndex.value = value
    }
    fun getMaximumSelectedIndex(): LiveData<Int> {
        return mMaximumSelectedIndex
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
}