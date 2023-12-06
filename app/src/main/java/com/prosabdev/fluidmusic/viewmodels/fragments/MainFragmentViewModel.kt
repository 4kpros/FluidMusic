package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import androidx.lifecycle.*
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.launch

class MainFragmentViewModel(
    app: Application
) : AndroidViewModel(app) {
    val currentSelectablePage = MutableLiveData<String>(null)
    val selectMode = MutableLiveData<Boolean>(false)
    val requestToggleSelectRange = MutableLiveData<Int>(0)
    val requestToggleSelectAll = MutableLiveData<Int>(0)
    val totalCount = MutableLiveData<Int>(0)
    val showDrawerMenuCounter = MutableLiveData<Int>()
    val slidingUpPanelState = MutableLiveData<PanelState>(PanelState.COLLAPSED)
    val isFastScrolling = MutableLiveData<Boolean>(false)
    val scrollingState = MutableLiveData<Int>(-2)
    val showSlidingUpPanelCounter = MutableLiveData<Int>(0)
    val hideSlidingUpPanelCounter = MutableLiveData<Int>(0)
    val selectedDataList = MutableLiveData<HashMap<Int, String>>(HashMap())

    fun setShowSlidingPanelCounter() {
        viewModelScope.launch {
            if(slidingUpPanelState.value != PanelState.EXPANDED) {
                val tempValue: Int = (showSlidingUpPanelCounter.value ?: 0) + 1
                showSlidingUpPanelCounter.value = tempValue
            }
        }
    }
    fun setHideSlidingPanelCounter() {
        viewModelScope.launch {
            if(slidingUpPanelState.value != PanelState.COLLAPSED) {
                val tempValue: Int = (showSlidingUpPanelCounter.value ?: 0) + 1
                showSlidingUpPanelCounter.value = tempValue
            }
        }
    }
    fun setReQuestToggleSelectRange() {
        viewModelScope.launch {
            requestToggleSelectRange.value =
                if((requestToggleSelectRange.value ?: 0) >= 100)
                    0
                else
                    (requestToggleSelectRange.value ?: 0) + 1
        }
    }
    fun setReQuestToggleSelectAll(){
        viewModelScope.launch {
            requestToggleSelectAll.value =
                if((requestToggleSelectAll.value ?: 0) >= 100)
                    0
                else
                    (requestToggleSelectAll.value ?: 0) + 1
        }
    }
    fun setShowDrawerMenuCounter() {
        viewModelScope.launch {
            showDrawerMenuCounter.value =
                if((showDrawerMenuCounter.value ?: 0) >= 100)
                    0
                else
                    (showDrawerMenuCounter.value ?: 0) + 1
        }
    }

    companion object {
        private const val TAG = "MainFVM"
    }
}
