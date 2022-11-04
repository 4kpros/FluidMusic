package com.prosabdev.fluidmusic.utils

import android.util.SparseBooleanArray
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableRecycleViewAdapter<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<VH>() {
    private val TAG: String = SelectableRecycleViewAdapter::class.java.simpleName
    private var selectedItems: SparseBooleanArray? = SparseBooleanArray()

    fun isSelected(position: Int): Boolean {
        return selectedItems?.contains(position) ?: false
    }

    fun toggleSelection(position: Int) {
        if (selectedItems?.get(position, false) == true) {
            selectedItems?.delete(position)
        } else {
            selectedItems?.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun selectAll() {
        for (i in 0 until itemCount) {
            if (!selectedItems?.get(i, false)!!) {
                selectedItems?.put(i, true)
            }
            notifyItemChanged(i)
        }
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val selection = getSelectedItems()
        selectedItems?.clear()
        for (i in selection) {
            if (i != null) {
                notifyItemChanged(i)
            }
        }
    }

    fun getSelectedItemCount(): Int {
        return selectedItems?.size() ?: 0
    }

    private fun getSelectedItems(): List<Int?> {
        val items: ArrayList<Int> = ArrayList(selectedItems?.size() ?: 0)
        val maxLoop = selectedItems?.size() ?: 0
        for (i in 0 until maxLoop) {
            if(selectedItems?.keyAt(i) != null)
                items.add(selectedItems?.keyAt(i)!!)
        }
        return items
    }
}