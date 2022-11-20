package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemEmptyBottomSpaceBinding
import com.prosabdev.fluidmusic.databinding.ItemStorageAccessFolderBinding
import com.prosabdev.fluidmusic.utils.CustomAnimators

class EmptyBottomAdapter(
    private val mEmptyList: ArrayList<String>
) : RecyclerView.Adapter<EmptyBottomAdapter.EmptyBottomHolder>() {

    private var mScrollState: Int = -1

    fun onSetScrollState(value : Int){
        mScrollState = value
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyBottomHolder {
        val tempItemEmptyBottomSpaceBinding: ItemEmptyBottomSpaceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_empty_bottom_space, parent, false
        )
        return EmptyBottomHolder(tempItemEmptyBottomSpaceBinding)
    }

    override fun onBindViewHolder(holder: EmptyBottomHolder, position: Int) {
        holder.updateBottomSpaceUI(mScrollState)
    }

    override fun getItemCount(): Int {
        return mEmptyList.size
    }


    class EmptyBottomHolder(private val mItemEmptyBottomSpaceBinding : ItemEmptyBottomSpaceBinding) : RecyclerView.ViewHolder(mItemEmptyBottomSpaceBinding.root) {

        fun updateBottomSpaceUI(scrollState : Int) {
            if(scrollState == 2) {
                CustomAnimators.crossFadeUp(mItemEmptyBottomSpaceBinding.textEnd, true, 500)
            }
        }
    }
}