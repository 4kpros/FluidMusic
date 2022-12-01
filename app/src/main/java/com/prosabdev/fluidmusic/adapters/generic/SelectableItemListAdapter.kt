package com.prosabdev.fluidmusic.adapters.generic

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.remove
import androidx.core.util.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class SelectableItemListAdapter<VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<Any>) : ListAdapter<Any, VH>(
    diffCallback
) {
    private val mTAG: String = SelectableItemListAdapter::class.java.simpleName
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onSelectModeChange(selectMode : Boolean)
        fun onTotalSelectedItemChange(totalSelected : Int)
    }

    private suspend fun onNotifyVisibleItemChanged(layoutManager : GridLayoutManager?){
        withContext(Dispatchers.Default){
            val tempItemCount : Int = layoutManager?.itemCount ?: -1
            var tempFirstPos = (layoutManager?.findFirstVisibleItemPosition() ?: 0) - 5
            var tempLastPos = (layoutManager?.findLastVisibleItemPosition() ?: 0) + 5
            if(tempFirstPos < 0) tempFirstPos = 0
            if(tempLastPos > tempItemCount) tempLastPos = tempItemCount

            MainScope().launch {
                notifyItemRangeChanged(tempFirstPos, tempLastPos-tempFirstPos, PAYLOAD_IS_SELECTED)
            }
        }

    }
    private suspend fun onNotifyItemChanged(position : Int, layoutManager : GridLayoutManager?, checkVisibility: Boolean = false){
        if(checkVisibility){
            if(layoutManager != null){
                if(isItemPositionVisible(position, layoutManager))
                    MainScope().launch {
                        notifyItemChanged(position, PAYLOAD_IS_SELECTED)
                    }
            }else{
                MainScope().launch {
                    notifyItemChanged(position, PAYLOAD_IS_SELECTED)
                }
            }
        }else{
            MainScope().launch {
                notifyItemChanged(position, PAYLOAD_IS_SELECTED)
            }
        }
    }
    private suspend fun isItemPositionVisible(position : Int, layoutManager : GridLayoutManager?): Boolean {
        return withContext(Dispatchers.Default){
            val tempFirstVisiblePosition = (layoutManager?.findFirstVisibleItemPosition() ?: 0) - 5
            val tempLastVisiblePosition = (layoutManager?.findLastVisibleItemPosition() ?: 0) + 5
            if(position in tempFirstVisiblePosition ..  tempLastVisiblePosition)
                return@withContext true
            return@withContext false
        }
    }

    protected fun selectableItemGetSelectedItemCount(): Int {
        return mSelectedItems.size
    }
    protected fun selectableItemGetMinSelected(): Int {
        return mMinSelected
    }
    protected fun selectableItemGetMaxSelected(): Int {
        return mMaxSelected
    }
    protected fun selectableItemGetSelectionMode(): Boolean {
        return mSelectionMode
    }
    protected fun selectableItemIsSelected(position: Int): Boolean {
        return mSelectedItems.contains(position)
    }

    protected fun selectableItemSetSelectionMode(
        value: Boolean,
        layoutManager : GridLayoutManager? = null
    ) {
        if(value == mSelectionMode)
            return
        MainScope().launch {
            if(!value){
                mSelectedItems.clear()
                mMinSelected = -1
                mMaxSelected = -1
                mSelectionMode = false
                onNotifyVisibleItemChanged(layoutManager)
            }
        }
    }

    protected fun selectableItemOnSelectFromPosition(
        position: Int,
        onSelectSelectableItemListener: OnSelectSelectableItemListener,
        layoutManager: GridLayoutManager? = null
    ) {
        MainScope().launch {
            withContext(Dispatchers.Default){
                if(mSelectionMode){
                    if(mSelectedItems.contains(position)){
                        mSelectedItems.remove(position, true)
                        onSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size())
                        onNotifyItemChanged(position, layoutManager)
                        findNewMinMaxSelected(position)
                    }else{
                        mSelectedItems.put(position, true)
                        onSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size())
                        onNotifyItemChanged(position, layoutManager)
                        compareAndSetMinMax(position)
                    }
                }else{
                    mSelectionMode = true
                    mMinSelected = position
                    mMaxSelected = position
                    mSelectedItems.clear()
                    mSelectedItems.put(position, true)
                    onSelectSelectableItemListener.onSelectModeChange(mSelectionMode)
                    onSelectSelectableItemListener.onTotalSelectedItemChange(1)
                    onNotifyItemChanged(position, layoutManager)
                }
            }
        }
    }

    protected fun selectableItemUpdateSelection(position: Int, value : Boolean, layoutManager : GridLayoutManager? = null) {
        MainScope().launch {
            withContext(Dispatchers.Default) {
                if (mSelectedItems.contains(position)) {
                    if (!value) {
                        mSelectedItems.delete(position)
                        findNewMinMaxSelected(position)
                    }
                } else {
                    if (value) {
                        mSelectedItems.put(position, true)
                        compareAndSetMinMax(position)
                    }
                }
                onNotifyItemChanged(position, layoutManager)
            }
        }
    }
    protected fun selectableItemSelectAll(layoutManager : GridLayoutManager? = null) {
        MainScope().launch {
            withContext(Dispatchers.Default) {
                mMinSelected = 0
                mMaxSelected = itemCount
                for (i in 0 until itemCount) {
                    if (!mSelectedItems.contains(i)) {
                        mSelectedItems.put(i, true)
                    }
                }
                onNotifyVisibleItemChanged(layoutManager)
            }
        }
    }
    protected fun selectableItemClearAllSelection(layoutManager : GridLayoutManager? = null) {
        MainScope().launch {
            mSelectedItems.clear()
            mMinSelected = -1
            mMaxSelected = -1
            onNotifyVisibleItemChanged(layoutManager)
        }
    }

    protected fun selectableItemOnSelectRange(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        val oldSize = mSelectedItems.size()
        MainScope().launch {
            selectableItemSelectRange(layoutManager)
            if(mSelectedItems.size() != oldSize)
            {
                mOnSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
                onNotifyVisibleItemChanged(layoutManager)
            }
        }
    }
    private suspend fun selectableItemSelectRange(layoutManager : GridLayoutManager? = null) {
        withContext(Dispatchers.Default){
            if(mMinSelected >= 0 && mMaxSelected >= 0) {
                if (mMinSelected in 0 until mMaxSelected) {
                    for (i in mMinSelected .. mMaxSelected){
                        if(!mSelectedItems.contains(i)){
                            mSelectedItems.put(i, true)
                        }
                    }
                }
            }
        }
    }

    private fun compareAndSetMinMax(position: Int) {
        if(mMaxSelected < position || mMaxSelected < 0){
            mMaxSelected = position
        }
        if(mMinSelected > position || mMinSelected < 0){
            mMinSelected = position
        }
    }

    private fun findNewMinMaxSelected(position: Int) {
        if((mMaxSelected <= position) || (mMinSelected >= position)){
            mMaxSelected = -1
            mMinSelected = -1
            if(mSelectedItems.size > 0){
                for (i in 0 until mSelectedItems.size) {
                    if(i > mMaxSelected){
                        mMaxSelected = i
                    }
                    if(i < mMinSelected || mMinSelected < 0){
                        mMinSelected = i
                    }
                }
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_SELECTED = "PAYLOAD_IS_SELECTED"
    }
}