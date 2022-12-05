package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemHeadlinePlayShuffleBinding
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils

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
                if(mItemHeadlinePlayShuffleBinding.buttonPlay.isClickable) {
                    ViewAnimatorsUtils.crossFadeDownClickable(
                        mItemHeadlinePlayShuffleBinding.buttonPlay,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeDownClickable(
                        mItemHeadlinePlayShuffleBinding.buttonShuffle,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeDownClickable(
                        mItemHeadlinePlayShuffleBinding.buttonSort,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeDownClickable(
                        mItemHeadlinePlayShuffleBinding.buttonOrganize,
                        true,
                        250
                    )
                }
            }
            else{
                if(!mItemHeadlinePlayShuffleBinding.buttonPlay.isClickable) {
                    ViewAnimatorsUtils.crossFadeUpClickable(
                        mItemHeadlinePlayShuffleBinding.buttonPlay,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeUpClickable(
                        mItemHeadlinePlayShuffleBinding.buttonShuffle,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeUpClickable(
                        mItemHeadlinePlayShuffleBinding.buttonSort,
                        true,
                        250
                    )
                    ViewAnimatorsUtils.crossFadeUpClickable(
                        mItemHeadlinePlayShuffleBinding.buttonOrganize,
                        true,
                        250
                    )
                }
            }
        }
    }
}