package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class GenericListenDataViewModel (app: Application) : AndroidViewModel(app) {
    protected val mMutableDataList = MutableLiveData<List<Any>>(null)
    private val mMutableSortBy = MutableLiveData<String>("id")
    private val mMutableOrganizeListGrid = MutableLiveData<Int>(ConstantValues.ORGANIZE_LIST_MEDIUM)
    private val mMutableIsInverted = MutableLiveData<Boolean>(false)

    private val mDataList: LiveData<List<Any>> get() = mMutableDataList
    private val mSortBy: LiveData<String> get() = mMutableSortBy
    private val mOrganizeListGrid: LiveData<Int> get() = mMutableOrganizeListGrid
    private val mIsInverted: LiveData<Boolean> get() = mMutableIsInverted

    fun getAll(): LiveData<List<Any>> {
        return mDataList
    }
    fun getAllDirectly(): List<Any>? {
        return mDataList.value
    }
    fun setSortBy(sortBy : String) {
        if(sortBy == mSortBy.value) return
        MainScope().launch {
            mMutableSortBy.value = sortBy
        }
    }
    fun getSortBy(): LiveData<String> {
        return mSortBy
    }
    fun setOrganizeListGrid(organizeListGrid : Int) {
        if(organizeListGrid == mOrganizeListGrid.value) return
        MainScope().launch {
            mMutableOrganizeListGrid.value = organizeListGrid
        }
    }
    fun getOrganizeListGrid(): LiveData<Int> {
        return mOrganizeListGrid
    }
    fun setIsInverted(isInverted : Boolean) {
        if(isInverted == mIsInverted.value) return
        MainScope().launch {
            mMutableIsInverted.value = isInverted
        }
    }
    fun getIsInverted(): LiveData<Boolean> {
        return mIsInverted
    }
}
