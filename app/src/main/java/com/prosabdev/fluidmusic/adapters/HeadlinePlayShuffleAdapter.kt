package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.common.utils.Animators
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemHeadlinePlayShuffleBinding

class HeadlinePlayShuffleAdapter(
    private val mHeadLines: ArrayList<Int>,
    private val mListener: OnItemClickListener
    ) : RecyclerView.Adapter<HeadlinePlayShuffleAdapter.HeadlinePlayShuffleHolder>() {

    private var mIsSelectMode: Boolean = false

    interface OnItemClickListener {
        fun onPlayButtonClicked()
        fun onShuffleButtonClicked()
        fun onSortButtonClicked()
        fun onOrganizeButtonClicked()
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
        return HeadlinePlayShuffleHolder(
            tempItemHeadlinePlayShuffleBinding,
            mListener
        )
    }

    override fun onBindViewHolder(holder: HeadlinePlayShuffleHolder, position: Int) {

        holder.updateOnSelectModeEnabledUI(mIsSelectMode)
    }

    override fun getItemCount(): Int {
        return mHeadLines.size
    }

    class HeadlinePlayShuffleHolder(
        private val mItemHeadlinePlayShuffleBinding: ItemHeadlinePlayShuffleBinding,
        private val mListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mItemHeadlinePlayShuffleBinding.root) {
        init {
            mItemHeadlinePlayShuffleBinding.buttonPlay.setOnClickListener {
                mListener.onPlayButtonClicked()
            }
            mItemHeadlinePlayShuffleBinding.buttonShuffle.setOnClickListener {
                mListener.onShuffleButtonClicked()
            }
            mItemHeadlinePlayShuffleBinding.buttonSort.setOnClickListener {
                mListener.onSortButtonClicked()
            }
            mItemHeadlinePlayShuffleBinding.buttonOrganize.setOnClickListener {
                mListener.onOrganizeButtonClicked()
            }
        }

        fun updateOnSelectModeEnabledUI(isSelectMode: Boolean) {
            if(isSelectMode){
                Animators.crossFadeDownClickable(
                    mItemHeadlinePlayShuffleBinding.buttonPlay,
                    true,
                    200
                )
                Animators.crossFadeDownClickable(
                    mItemHeadlinePlayShuffleBinding.buttonShuffle,
                    true,
                    200
                )
                Animators.crossFadeDownClickable(
                    mItemHeadlinePlayShuffleBinding.buttonSort,
                    true,
                    200
                )
                Animators.crossFadeDownClickable(
                    mItemHeadlinePlayShuffleBinding.buttonOrganize,
                    true,
                    200
                )
            }
            else{
                Animators.crossFadeUpClickable(
                    mItemHeadlinePlayShuffleBinding.buttonPlay,
                    true,
                    200
                )
                Animators.crossFadeUpClickable(
                    mItemHeadlinePlayShuffleBinding.buttonShuffle,
                    true,
                    200
                )
                Animators.crossFadeUpClickable(
                    mItemHeadlinePlayShuffleBinding.buttonSort,
                    true,
                    200
                )
                Animators.crossFadeUpClickable(
                    mItemHeadlinePlayShuffleBinding.buttonOrganize,
                    true,
                    200
                )
            }
        }
    }
}