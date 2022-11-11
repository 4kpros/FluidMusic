package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomViewModifiers

class HeadlinePlayShuffleAdapter(
    private val mHeadLines: ArrayList<Long>,
    private val mResourceId : Int,
    private val mListener: OnItemClickListener
    ) : RecyclerView.Adapter<HeadlinePlayShuffleAdapter.HeadlinePlayShuffleHolder>() {

    private var mIsSelectMode: Boolean = false
    private var mScrollState: Int = -1

    interface OnItemClickListener {
        fun onPlayButtonClicked()
        fun onShuffleButtonClicked()
        fun onFilterButtonClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinePlayShuffleHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(mResourceId, parent, false)

        return HeadlinePlayShuffleHolder(view)
    }

    override fun onViewAttachedToWindow(holder: HeadlinePlayShuffleHolder) {
        super.onViewAttachedToWindow(holder)
        holder.updateBottomSpaceUI(mScrollState)
    }
    override fun onBindViewHolder(holder: HeadlinePlayShuffleHolder, position: Int) {

        //Bind listener for to capture click events
        holder.updateBottomSpaceUI(mScrollState)
        holder.bindListener(mListener)
        holder.updateOnSelectModeEnabledUI(mIsSelectMode)
    }

    override fun getItemCount(): Int {
        return mHeadLines.size
    }

    fun onSetScrollState(value : Int){
        mScrollState = value
        notifyItemChanged(0)
    }

    fun onSelectModeValue(value : Boolean){
        mIsSelectMode = value
        notifyItemChanged(0)
    }

    class HeadlinePlayShuffleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mPlayButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_play)
        private var mShuffleButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_shuffle)
        private var mFilterButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_filter)
        private var mHoverView: View? =
            itemView.findViewById<View>(R.id.hover_view)
//
        private var mTextEnd: MaterialTextView? =
            itemView.findViewById<MaterialTextView>(R.id.text_end)
        private var mCustomEmptyBottomSpace: ConstraintLayout? =
            itemView.findViewById<ConstraintLayout>(R.id.custom_empty_bottom_space)

        private var mRequestApplyInsetsCounter :Int  = 0;
        fun bindListener(listener: OnItemClickListener) {
            mPlayButton?.setOnClickListener {
                listener.onPlayButtonClicked()
            }
            mShuffleButton?.setOnClickListener {
                listener.onShuffleButtonClicked()
            }
            mFilterButton?.setOnClickListener {
                listener.onFilterButtonClicked()
            }
        }
        fun updateBottomSpaceUI(scrollState : Int) {
            if(mCustomEmptyBottomSpace == null)
                return

            if (scrollState == 1) {
                mCustomEmptyBottomSpace?.visibility = GONE
            }else {
                if(mRequestApplyInsetsCounter == 0){
                    mRequestApplyInsetsCounter++
//                    CustomViewModifiers.updateBottomViewInsets(mCustomEmptyBottomSpace!!)
                }
//                mCustomEmptyBottomSpace?.requestApplyInsets()
                mCustomEmptyBottomSpace?.visibility = VISIBLE
            }
            if(scrollState == 2) {
                if (mTextEnd != null)
                    CustomAnimators.crossFadeUp(mTextEnd!!, true, 500)
            }
        }
        fun updateOnSelectModeEnabledUI(isSelectMode: Boolean) {
            if(mHoverView == null)
                return
            if(isSelectMode){
                if(mHoverView?.visibility != VISIBLE)
                    CustomAnimators.crossFadeUp(mHoverView!!, true, 200, 0.8f)
            }else{
                if (mHoverView?.visibility != GONE)
                    CustomAnimators.crossFadeDown(mHoverView!!, true, 200)
            }
        }
    }
}