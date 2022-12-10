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
    private var mSelectedItems: ArrayList<Int> = ArrayList()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onSelectModeChange(selectMode : Boolean)
        fun onTotalSelectedItemChange(totalSelected : Int)
    }

    private fun onNotifyVisibleItemChanged(layoutManager : GridLayoutManager?){
        val tempItemCount : Int = layoutManager?.itemCount ?: -1
        var tempFirstPos = (layoutManager?.findFirstVisibleItemPosition() ?: 0) - 5
        var tempLastPos = (layoutManager?.findLastVisibleItemPosition() ?: 0) + 5
        if(tempFirstPos < 0) tempFirstPos = 0
        if(tempLastPos > tempItemCount) tempLastPos = tempItemCount

        notifyItemRangeChanged(tempFirstPos, tempLastPos-tempFirstPos, PAYLOAD_IS_SELECTED)
    }
    private fun onNotifyItemChanged(position : Int, layoutManager : GridLayoutManager?, checkVisibility: Boolean = false){
        if(checkVisibility){
            if(layoutManager != null){
                if(isItemPositionVisible(position, layoutManager))
                    notifyItemChanged(position, PAYLOAD_IS_SELECTED)
            }else{
                notifyItemChanged(position, PAYLOAD_IS_SELECTED)
            }
        }else{
            notifyItemChanged(position, PAYLOAD_IS_SELECTED)
        }
    }
    private fun isItemPositionVisible(position : Int, layoutManager : GridLayoutManager?): Boolean {
        val tempFirstVisiblePosition = (layoutManager?.findFirstVisibleItemPosition() ?: 0) - 5
        val tempLastVisiblePosition = (layoutManager?.findLastVisibleItemPosition() ?: 0) + 5
        if(position in tempFirstVisiblePosition ..  tempLastVisiblePosition)
            return true
        return false
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
        if(!value){
            mSelectedItems.clear()
            mMinSelected = -1
            mMaxSelected = -1
            mSelectionMode = false
            onNotifyVisibleItemChanged(layoutManager)
        }
    }

    protected fun selectableItemSelectFromPosition(
        position: Int,
        onSelectSelectableItemListener: OnSelectSelectableItemListener,
        layoutManager: GridLayoutManager? = null
    ) {
        if(mSelectionMode){
            if(mSelectedItems.contains(position)){
                mSelectedItems.remove(position)
                onSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
                onNotifyItemChanged(position, layoutManager, false)
                findNewMinMaxSelected()
            }else{
                mSelectedItems.add(position)
                onSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
                onNotifyItemChanged(position, layoutManager, false)
                compareAndSetMinMax(position)
            }
        }else{
            mSelectionMode = true
            mMinSelected = position
            mMaxSelected = position
            mSelectedItems.clear()
            mSelectedItems.add(position)
            onSelectSelectableItemListener.onSelectModeChange(mSelectionMode)
            onSelectSelectableItemListener.onTotalSelectedItemChange(1)
            onNotifyItemChanged(position, layoutManager, false)
        }
    }

    protected fun selectableItemSelectAll(layoutManager : GridLayoutManager? = null) {
        mMinSelected = 0
        mMaxSelected = itemCount
        for (i in 0 until itemCount) {
            if (!mSelectedItems.contains(i)) {
                mSelectedItems.add(i)
            }
        }
        onNotifyVisibleItemChanged(layoutManager)
    }
    protected fun selectableItemClearAllSelection(layoutManager : GridLayoutManager? = null) {
        mSelectedItems.clear()
        mMinSelected = -1
        mMaxSelected = -1
        onNotifyVisibleItemChanged(layoutManager)
    }

    protected fun selectableItemSelectRange(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        val oldSize = mSelectedItems.size
        if(mMinSelected >= 0 && mMaxSelected >= 0) {
            for (i in mMinSelected .. mMaxSelected){
                if(!mSelectedItems.contains(i)){
                    mSelectedItems.add(i)
                }
            }
        }
        if(mSelectedItems.size != oldSize)
        {
            mOnSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
            onNotifyVisibleItemChanged(layoutManager)
        }
    }

    private fun compareAndSetMinMax(position: Int) {
        if(mMaxSelected < position){
            mMaxSelected = position
        }
        if(mMinSelected > position || mMinSelected < 0){
            mMinSelected = position
        }
    }

    private fun findNewMinMaxSelected() {
        mMaxSelected = -1
        mMinSelected = -1
        if(mSelectedItems.size > 0){
            for (i in 0 until mSelectedItems.size) {
                if(mMaxSelected < mSelectedItems[i]){
                    mMaxSelected = mSelectedItems[i]
                }
                if(mMinSelected > mSelectedItems[i] || mMinSelected < 0){
                    mMinSelected = mSelectedItems[i]
                }
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_SELECTED = "PAYLOAD_IS_SELECTED"
    }
}