package com.prosabdev.fluidmusic.adapters.generic

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class SelectablePlayingItemListAdapter<VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Any>
) : SelectableItemListAdapter<VH>(diffCallback)
{
    private val mTAG: String = SelectablePlayingItemListAdapter::class.java.simpleName
    private var mPlayingPosition: Int = -1
    private var mIsPlaying: Boolean = false

    protected fun setSelectableIsPlaying(isPlaying: Boolean) {
        mIsPlaying = isPlaying
        notifyItemChanged(mPlayingPosition, PAYLOAD_PLAYBACK_STATE)
    }
    protected fun getSelectableIsPlaying(): Boolean {
        return mIsPlaying
    }
    protected fun setSelectablePlayingPosition(position: Int) {
        val oldPlaying = mPlayingPosition
        mPlayingPosition = if(position >= 0) position else -1
        if(oldPlaying != position){
            if(oldPlaying >= 0)
                notifyItemChanged(oldPlaying, PAYLOAD_PLAYBACK_STATE)
            if(mPlayingPosition >= 0)
                notifyItemChanged(position, PAYLOAD_PLAYBACK_STATE)
        }else{
            notifyItemChanged(position, PAYLOAD_PLAYBACK_STATE)
        }
    }
    protected fun getSelectablePlayingPosition(): Int {
        return mPlayingPosition
    }

    companion object {
        const val PAYLOAD_PLAYBACK_STATE = "PAYLOAD_PLAYBACK_STATE"
    }
}