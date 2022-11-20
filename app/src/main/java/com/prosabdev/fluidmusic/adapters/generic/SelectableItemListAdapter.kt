package com.prosabdev.fluidmusic.adapters.generic

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues

abstract class SelectableItemListAdapter<VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<Any>) : ListAdapter<Any, VH>(
    diffCallback
) {
    private val mTAG: String = SelectableItemListAdapter::class.java.simpleName
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onTotalSelectedItemChange(totalSelected : Int)
    }

    private fun onNotifyItemChanged(position : Int, layoutManager : GridLayoutManager?){
        if(layoutManager != null){
            if(isItemPositionVisible(position, layoutManager))
                notifyItemChanged(position, Companion.PAYLOAD_IS_SELECTED)
        }else{
            notifyItemChanged(position, Companion.PAYLOAD_IS_SELECTED)
        }
    }
    private fun isItemPositionVisible(position : Int, layoutManager : GridLayoutManager?): Boolean {
        var tempResult : Boolean? = false
        val tempFirstVisiblePosition = (layoutManager?.findFirstVisibleItemPosition() ?: 0) - 2
        val tempLastVisiblePosition = (layoutManager?.findLastVisibleItemPosition() ?: 0) + 2
        if(position in tempFirstVisiblePosition ..  tempLastVisiblePosition)
            tempResult = true
        Log.i(ConstantValues.TAG, "View visibility at $position : $tempResult")
        return tempResult ?: true
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
        if(mSelectedItems.size > 0){
            for (i in 0 until itemCount) {
                mSelectedItems.delete(i)
                onNotifyItemChanged(i, layoutManager)
            }
        }
        mSelectedItems.clear()
        mMinSelected = -1
        mMaxSelected = -1
        mSelectionMode = value
    }

    protected fun selectableItemToggleSelection(position: Int, layoutManager : GridLayoutManager? = null) {
        if (mSelectedItems.contains(position)) {
            mSelectedItems.delete(position)
            findNewMinMaxSelected(position)
        } else {
            mSelectedItems.put(position, true)
            compareAndSetMinMax(position)
        }
        onNotifyItemChanged(position, layoutManager)
    }

    protected fun selectableItemUpdateSelection(position: Int, value : Boolean, layoutManager : GridLayoutManager? = null) {
        if(mSelectedItems.contains(position)){
            if(!value){
                mSelectedItems.delete(position)
                findNewMinMaxSelected(position)
            }
        }else{
            if(value){
                mSelectedItems.put(position, true)
                compareAndSetMinMax(position)
            }
        }
        onNotifyItemChanged(position, layoutManager)
    }
    protected fun selectableItemSelectAll(layoutManager : GridLayoutManager? = null) {
        mMinSelected = 0
        mMaxSelected = itemCount
        for (i in 0 until itemCount) {
            if (!mSelectedItems.contains(i)) {
                mSelectedItems.put(i, true)
                onNotifyItemChanged(i, layoutManager)
            }
        }
    }
    protected fun selectableItemClearAllSelection(layoutManager : GridLayoutManager? = null) {
        if(mSelectedItems.size > 0){
            for (i in 0 until itemCount) {
                if (mSelectedItems.contains(i)) {
                    mSelectedItems.delete(i)
                    onNotifyItemChanged(i, layoutManager)
                }
            }
        }
        mMinSelected = -1
        mMaxSelected = -1
        mSelectedItems.clear()
    }

    protected fun selectableItemToggleSelectRange(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        val oldSize = mSelectedItems.size()
        if(mMinSelected >= 0 && mMaxSelected >= 0) {
            if (mMinSelected in 0 until mMaxSelected) {
                selectableItemSelectRange(layoutManager)
            }
        }
        if(mSelectedItems.size() != oldSize)
            mOnSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
    }
    private fun selectableItemSelectRange(layoutManager : GridLayoutManager? = null) {
        if(mMinSelected >= 0 && mMaxSelected >= 0){
            if(mMinSelected in 0 until mMaxSelected)
            {
                for (i in mMinSelected .. mMaxSelected){
                    if(!mSelectedItems.contains(i)){
                        mSelectedItems.put(i, true)
                        onNotifyItemChanged(i, layoutManager)
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