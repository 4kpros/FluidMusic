package com.prosabdev.fluidmusic.adapters.generic

import androidx.core.util.contains
import androidx.core.util.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableItemListAdapter<VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<Any>) : ListAdapter<Any, VH>(
    diffCallback
) {
    private val mTAG: String = SelectableItemListAdapter::class.java.simpleName
    private var mSelectedStringItems: HashMap<Int, String> = HashMap()
    private var mMinSelected: Int = -1
    private var mMaxSelected: Int = -1
    private var mSelectionMode: Boolean = false

    interface OnSelectSelectableItemListener{
        fun onSelectModeChange(selectMode : Boolean)
        fun onRequestGetStringIndex(position : Int) : String
        fun onSelectedListChange(selectedList : HashMap<Int, String>)
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

    protected fun selectableItemGetSelectedItemList(): HashMap<Int, String> {
        return mSelectedStringItems
    }
    protected fun selectableItemGetSelectedItemCount(): Int {
        return mSelectedStringItems.size
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
        return mSelectedStringItems.contains(position)
    }

    protected fun selectableItemSetSelectionMode(
        value: Boolean,
        layoutManager : GridLayoutManager? = null
    ) {
        if(value == mSelectionMode)
            return
        if(!value){
            mSelectedStringItems.clear()
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
            if(mSelectedStringItems.contains(position)){
                mSelectedStringItems.remove(position)

                onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
                onNotifyItemChanged(position, layoutManager, false)
                findNewMinMaxSelected()
            }else{
                val stringIndex = onSelectSelectableItemListener.onRequestGetStringIndex(position)
                mSelectedStringItems[position] = stringIndex

                onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
                onNotifyItemChanged(position, layoutManager, false)
                compareAndSetMinMax(position)
            }
        }else{
            mSelectionMode = true
            mMinSelected = position
            mMaxSelected = position

            mSelectedStringItems.clear()
            val stringIndex = onSelectSelectableItemListener.onRequestGetStringIndex(position)
            mSelectedStringItems[position] = stringIndex

            onSelectSelectableItemListener.onSelectModeChange(mSelectionMode)
            onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
            onNotifyItemChanged(position, layoutManager, false)
        }
    }

    protected fun selectableItemSelectAll(onSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        mMinSelected = 0
        mMaxSelected = itemCount
        for (i in 0 until itemCount) {
            if(!mSelectedStringItems.contains(i)){
                val stringIndex = onSelectSelectableItemListener.onRequestGetStringIndex(i)
                mSelectedStringItems[i] = stringIndex
            }
        }
        onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
        onNotifyVisibleItemChanged(layoutManager)
    }
    protected fun selectableItemClearAllSelection(onSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        mSelectedStringItems.clear()
        mMinSelected = -1
        mMaxSelected = -1
        onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
        onNotifyVisibleItemChanged(layoutManager)
    }

    protected fun selectableItemSelectRange(onSelectSelectableItemListener: OnSelectSelectableItemListener, layoutManager : GridLayoutManager? = null) {
        val oldSize = mSelectedStringItems.size
        if(mMinSelected >= 0 && mMaxSelected >= 0) {
            for (i in mMinSelected .. mMaxSelected){
                if(!mSelectedStringItems.contains(i)){
                    val stringIndex = onSelectSelectableItemListener.onRequestGetStringIndex(i)
                    mSelectedStringItems[i] = stringIndex
                }
            }
        }
        if(mSelectedStringItems.size != oldSize)
        {
            onSelectSelectableItemListener.onSelectedListChange(mSelectedStringItems)
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
        if(mSelectedStringItems.size > 0){
            mSelectedStringItems.keys.forEach { key ->
                if(mMaxSelected < key){
                    mMaxSelected = key
                }
                if(mMinSelected > key || mMinSelected < 0){
                    mMinSelected = key
                }
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_SELECTED = "PAYLOAD_IS_SELECTED"
    }
}