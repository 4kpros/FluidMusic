package com.prosabdev.fluidmusic.utils.adapters

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.core.util.size
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.utils.ConstantValues
import java.util.*
import kotlin.collections.ArrayList

abstract class SelectableRecycleViewAdapter<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<VH>() {
    private val TAG: String = SelectableRecycleViewAdapter::class.java.simpleName
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onTotalSelectedItemChange(totalSelected : Int)
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
        value: Boolean
    ) {
        if(value == mSelectionMode)
            return
        if(mSelectedItems.size > 0){
            for (i in 0 until itemCount) {
                mSelectedItems.delete(i)
                notifyItemChanged(i)
            }
        }
        mSelectedItems.clear()
        mMinSelected = -1
        mMaxSelected = -1
        mSelectionMode = value
    }

    protected fun selectableItemToggleSelection(position: Int) {
        if (mSelectedItems.contains(position)) {
            mSelectedItems.delete(position)
            findNewMinMaxSelected(position)
        } else {
            mSelectedItems.put(position, true)
            compareAndSetMinMax(position)
        }
        notifyItemChanged(position)
    }

    protected fun selectableItemUpdateSelection(position: Int, value : Boolean) {
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
    }
    protected fun selectableItemSelectAll() {
        mMinSelected = 0
        mMaxSelected = itemCount
        for (i in 0 until itemCount) {
            if (!mSelectedItems.contains(i)) {
                mSelectedItems.put(i, true)
                notifyItemChanged(i)
            }
        }
    }
    protected fun selectableItemClearAllSelection() {
        if(mSelectedItems.size > 0){
            for (i in 0 until itemCount) {
                if (mSelectedItems.contains(i)) {
                    mSelectedItems.delete(i)
                    notifyItemChanged(i)
                }
            }
        }
        mMinSelected = -1
        mMaxSelected = -1
        mSelectedItems.clear()
    }

    protected fun selectableItemToggleSelectRange(mOnSelectSelectableItemListener: OnSelectSelectableItemListener) {
        val oldSize = mSelectedItems.size()
        if(mMinSelected >= 0 && mMaxSelected >= 0) {
            if (mMinSelected in 0 until mMaxSelected) {
                selectableItemSelectRange()
            }
        }
        if(mSelectedItems.size() != oldSize)
            mOnSelectSelectableItemListener.onTotalSelectedItemChange(mSelectedItems.size)
    }
    private fun selectableItemSelectRange() {
        if(mMinSelected >= 0 && mMaxSelected >= 0){
            if(mMinSelected in 0 until mMaxSelected)
            {
                for (i in mMinSelected .. mMaxSelected){
                    if(!mSelectedItems.contains(i)){
                        mSelectedItems.put(i, true)
                        notifyItemChanged(i)
                    }
                }
            }
        }
    }
//    private fun selectableItemClearRange() {
//        if(mMinSelected >= 0 && mMaxSelected >= 0){
//            if(mSelectedItems.size  > 0) {
//                for (i in mMinSelected..mMaxSelected) {
//                    mSelectedItems.delete(i)
//                    notifyItemChanged(i)
//                }
//            }
//        }
//        mMinSelected = -1
//        mMaxSelected = -1
//        mSelectedItems.clear()
//    }

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
}