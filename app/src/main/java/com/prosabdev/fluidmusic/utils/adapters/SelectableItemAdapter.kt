package com.prosabdev.fluidmusic.utils.adapters

import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.utils.ConstantValues

abstract class SelectableRecycleViewAdapter<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<VH>() {
    private val TAG: String = SelectableRecycleViewAdapter::class.java.simpleName
    private var mSelectedItems: SparseBooleanArray? = SparseBooleanArray()
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onSelectionModeChange(selectMode : Boolean, totalSelected : Int, totalCount : Int)
        fun onTotalSelectedItemChange(totalSelected : Int, totalCount : Int)
    }

    protected fun selectableItemGetSelectionMode(): Boolean {
        return mSelectionMode
    }

    protected fun selectableItemSetSelectionMode(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, value : Boolean, totalItemCount : Int) {
        mSelectionMode = value
        mOnSelectSelectableItemListener.onSelectionModeChange(mSelectionMode, selectableItemGetSelectedItemCount(), totalItemCount)
    }

    protected fun selectableItemIsSelected(position: Int): Boolean {
        return mSelectedItems?.contains(position) ?: false
    }

    protected fun selectableItemToggleSelection(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, position: Int, totalItemCount : Int) {
        if (mSelectedItems?.get(position, false) == true) {
            mSelectedItems?.delete(position)
        } else {
            mSelectedItems?.put(position, true)
        }
        mOnSelectSelectableItemListener.onTotalSelectedItemChange(selectableItemGetSelectedItemCount(), totalItemCount)
        notifyItemChanged(position)
    }
    protected fun selectableItemUpdateSelection(position: Int, value : Boolean) {
        if(mSelectedItems?.contains(position) == true){
            if(!value){
                mSelectedItems?.delete(position)
            }
        }else{
            if(value){
                mSelectedItems?.put(position, true)
            }
        }
    }

    protected fun selectableItemGetSelectedItemCount(): Int {
        return mSelectedItems?.size() ?: 0
    }

    protected fun selectableItemGetSelectedItems(): List<Int?> {
        val items: ArrayList<Int> = ArrayList(selectableItemGetSelectedItemCount())
        val maxLoop = mSelectedItems?.size() ?: 0
        for (i in 0 until maxLoop) {
            if(mSelectedItems?.keyAt(i) != null)
                items.add(mSelectedItems?.keyAt(i)!!)
        }
        return items
    }

    protected fun selectableItemSelectAll(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, totalItemCount : Int) {
        for (i in 0 until itemCount) {
            if (!mSelectedItems?.get(i, false)!!) {
                mSelectedItems?.put(i, true)
            }
            notifyItemChanged(i)
        }
//        notifyDataSetChanged()
        mOnSelectSelectableItemListener.onTotalSelectedItemChange(selectableItemGetSelectedItemCount(), totalItemCount)
    }

    protected fun selectableItemClearSelection(mOnSelectSelectableItemListener: OnSelectSelectableItemListener, totalItemCount : Int) {
        val selection = selectableItemGetSelectedItems()
        mSelectedItems?.clear()
        for (i in selection) {
            if (i != null) {
                notifyItemChanged(i)
            }
        }
        mOnSelectSelectableItemListener.onTotalSelectedItemChange(0, totalItemCount)
    }
}