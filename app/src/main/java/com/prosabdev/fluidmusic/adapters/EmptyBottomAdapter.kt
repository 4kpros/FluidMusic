package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemEmptyBottomSpaceBinding
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils

class EmptyBottomAdapter(
    private val mEmptyList: ArrayList<String>
) : RecyclerView.Adapter<EmptyBottomAdapter.EmptyBottomHolder>() {

    private var mTextVisible: Boolean = false
    private var mScrollState: Int = -1

    fun setTextVisible(value : Boolean){
        mTextVisible = value
        notifyItemChanged(0)
    }
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
        holder.updateBottomSpaceUI(mScrollState, mTextVisible)
    }

    override fun getItemCount(): Int {
        return mEmptyList.size
    }


    class EmptyBottomHolder(private val mItemEmptyBottomSpaceBinding : ItemEmptyBottomSpaceBinding) : RecyclerView.ViewHolder(mItemEmptyBottomSpaceBinding.root) {

        fun updateBottomSpaceUI(scrollState : Int, textVisible: Boolean) {
            if(textVisible){
                mItemEmptyBottomSpaceBinding.constraintContainer.visibility = VISIBLE
            }else{
                mItemEmptyBottomSpaceBinding.constraintContainer.visibility = INVISIBLE
            }

            if(scrollState == 2) {
                ViewAnimatorsUtils.crossFadeUp(mItemEmptyBottomSpaceBinding.textEnd, true, 500)
            }
        }
    }
}