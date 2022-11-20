package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemHeadlinePlayShuffleBinding
import com.prosabdev.fluidmusic.utils.CustomAnimators

class HeadlinePlayShuffleAdapter(
    private val mHeadLines: ArrayList<Int>,
    private val mListener: OnItemClickListener
    ) : RecyclerView.Adapter<HeadlinePlayShuffleAdapter.HeadlinePlayShuffleHolder>() {

    private var mIsSelectMode: Boolean = false

    interface OnItemClickListener {
        fun onPlayButtonClicked()
        fun onShuffleButtonClicked()
        fun onFilterButtonClicked()
    }

    fun onSelectModeValue(value : Boolean){
        mIsSelectMode = value
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinePlayShuffleHolder {
        val tempItemHeadlinePlayShuffleBinding: ItemHeadlinePlayShuffleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_headline_play_shuffle, parent, false
        )
        return HeadlinePlayShuffleHolder(tempItemHeadlinePlayShuffleBinding)
    }

    override fun onBindViewHolder(holder: HeadlinePlayShuffleHolder, position: Int) {

        holder.bindListener(mListener)
        holder.updateOnSelectModeEnabledUI(mIsSelectMode)
    }

    override fun getItemCount(): Int {
        return mHeadLines.size
    }

    class HeadlinePlayShuffleHolder(private val mItemHeadlinePlayShuffleBinding: ItemHeadlinePlayShuffleBinding) : RecyclerView.ViewHolder(mItemHeadlinePlayShuffleBinding.root) {
        fun bindListener(listener: OnItemClickListener) {
            mItemHeadlinePlayShuffleBinding.buttonPlay.setOnClickListener {
                listener.onPlayButtonClicked()
            }
            mItemHeadlinePlayShuffleBinding.buttonShuffle.setOnClickListener {
                listener.onShuffleButtonClicked()
            }
            mItemHeadlinePlayShuffleBinding.buttonFilter.setOnClickListener {
                listener.onFilterButtonClicked()
            }
        }

        fun updateOnSelectModeEnabledUI(isSelectMode: Boolean) {
            if(isSelectMode){
                if(mItemHeadlinePlayShuffleBinding.hoverView.visibility != VISIBLE)
                    CustomAnimators.crossFadeUp(mItemHeadlinePlayShuffleBinding.hoverView, true, 200, 0.8f)
            }else{
                if (mItemHeadlinePlayShuffleBinding.hoverView.visibility != GONE)
                    CustomAnimators.crossFadeDown(mItemHeadlinePlayShuffleBinding.hoverView, true, 200)
            }
        }
    }
}