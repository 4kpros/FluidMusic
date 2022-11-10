package com.prosabdev.fluidmusic.utils.adapters

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.util.*

abstract class SelectableRecycleViewAdapter<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<VH>() {
    private val TAG: String = SelectableRecycleViewAdapter::class.java.simpleName
    private var mSelectedItems: SparseBooleanArray? = SparseBooleanArray()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onSelectionModeChange(selectMode : Boolean, totalSelected : Int)
        fun onTotalSelectedItemChange(totalSelected : Int)
        fun onTotalSelectedItemChangeFromRange(totalSelected : Int)
        fun onTotalSelectedItemChangeFromToggle(totalSelected : Int)
        fun onMinSelectedItemChange(minSelected : Int)
        fun onMaxSelectedItemChange(maxSelected : Int)
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
        return mSelectedItems?.contains(position) ?: false
    }

    protected fun selectableItemGetSelectedItemCount(): Int {
        return mSelectedItems?.size() ?: 0
    }

    protected fun selectableItemGetSelectedItems(): List<Int?> {
        val items: ArrayList<Int> = ArrayList()
        val maxLoop = mSelectedItems?.size() ?: 0
        for (i in 0 until maxLoop) {
            if(mSelectedItems?.keyAt(i) != null)
                items.add(mSelectedItems?.keyAt(i) ?: -1)
        }
        return items
    }

    protected fun selectableItemSetSelectionMode(
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
        value: Boolean,
        notifyListener: Boolean
    ) {
        mSelectionMode = value
        if(notifyListener)
            mOnSelectSelectableItemListener.onSelectionModeChange(mSelectionMode, selectableItemGetSelectedItemCount())
    }

    protected fun selectableItemToggleSelection(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, position: Int) {
        if (mSelectedItems?.get(position, false) == true) {
            mSelectedItems?.delete(position)
            findNewMinMaxSelected(position, mOnSelectSelectableItemListener)
        } else {
            mSelectedItems?.put(position, true)
            compareAndSetMinMax(position, mOnSelectSelectableItemListener)
        }
        mOnSelectSelectableItemListener.onTotalSelectedItemChangeFromToggle(selectableItemGetSelectedItemCount())
        notifyItemChanged(position)
    }

    protected fun selectableItemUpdateSelection(position: Int, value : Boolean, mOnSelectSelectableItemListener: OnSelectSelectableItemListener) {
        if(mSelectedItems?.contains(position) == true){
            if(!value){
                mSelectedItems?.delete(position)
                findNewMinMaxSelected(position, mOnSelectSelectableItemListener)
            }
        }else{
            if(value){
                mSelectedItems?.put(position, true)
                compareAndSetMinMax(position, mOnSelectSelectableItemListener)
            }
        }
    }
    protected fun selectableItemSelectAll(mOnSelectSelectableItemListener: OnSelectSelectableItemListener) {
        for (i in 0 until itemCount) {
            if (mSelectedItems?.get(i, false) == false) {
                mSelectedItems?.delete(i)
            }
            mSelectedItems?.put(i, true)
            notifyItemChanged(i)
        }
        mMinSelected = 0
        mMaxSelected = itemCount
        mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
        mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)
        mOnSelectSelectableItemListener.onTotalSelectedItemChange(selectableItemGetSelectedItemCount())
    }

    protected fun selectableItemClearAllSelection(
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
        notifyListener: Boolean
    ) {
        mSelectedItems?.clear()
        for (i in 0 until itemCount) {
            notifyItemChanged(i)
        }
        mMinSelected = -1
        mMaxSelected = -1
        if(notifyListener){
            mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
            mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)
            mOnSelectSelectableItemListener.onTotalSelectedItemChange(0)
        }
    }

    protected fun selectableItemSelectRange(
        startRange: Int,
        endRange: Int,
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener
    ) {
        Log.i(ConstantValues.TAG, "MIN : $startRange, MAX : $endRange")
        if(startRange in 0 until endRange)
        {
            for (i in startRange .. endRange){
                if(mSelectedItems?.contains(i) == false){
                    mSelectedItems?.put(i, true)
                    notifyItemChanged(i)
                }
            }
            mMinSelected = startRange
            mMaxSelected = endRange
            mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
            mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)
            mOnSelectSelectableItemListener.onTotalSelectedItemChangeFromRange(selectableItemGetSelectedItemCount())
        }
    }

    protected fun selectableItemClearRange(
        startRange: Int,
        endRange: Int,
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener
    ) {
        if(startRange in 0 until endRange)
        {
            for (i in startRange .. endRange){
                mSelectedItems?.delete(i)
                notifyItemChanged(i)
            }
            mMinSelected = -1
            mMaxSelected = -1
            mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
            mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)
            mOnSelectSelectableItemListener.onTotalSelectedItemChangeFromRange(selectableItemGetSelectedItemCount())
        }
    }

    private fun compareAndSetMinMax(
        position: Int,
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
        notifyListener: Boolean = true
    ) {
        if(mMaxSelected < position){
            mMaxSelected = position
            if(notifyListener)
                mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)
        }
        //
        if(mMinSelected > position || mMinSelected < 0){
            mMinSelected = position
            if(notifyListener)
                mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
        }
    }

    private fun findNewMinMaxSelected(
        position: Int,
        mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
        notifyListener: Boolean = true
    ) {
        if((mMaxSelected <= position) || (mMinSelected >= position)){
            val oldMax = mMaxSelected
            val oldMin = mMinSelected
            mMaxSelected = -1
            mMinSelected = -1
            mSelectedItems?.forEach { key, value ->
                if(value){
                    if(key > mMaxSelected || mMaxSelected < 0){
                        mMaxSelected = key
                    }
                    if(key < mMinSelected || mMinSelected < 0){
                        mMinSelected = key
                    }
                }
            }
            if (notifyListener){
                if(oldMax != mMaxSelected)
                    mOnSelectSelectableItemListener.onMaxSelectedItemChange(mMaxSelected)

                if(oldMin != mMinSelected)
                    mOnSelectSelectableItemListener.onMinSelectedItemChange(mMinSelected)
            }

        }
    }
}