package com.prosabdev.fluidmusic.adapters.generic

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class SelectablePlayingItemListAdapter<VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Any>
) : SelectableItemListAdapter<VH>(diffCallback)
{
    private val mTAG: String = SelectablePlayingItemListAdapter::class.java.simpleName
    private var mPlayingItem: Int = -1

    protected fun selectablePlayingIsPlaying(position: Int): Boolean {
        return (position == mPlayingItem && mPlayingItem >= 0)
    }
    protected fun selectablePlayingGetCurrentPlayingSong(): Int {
        return mPlayingItem
    }
    protected fun selectablePlayingSetCurrentPlayingSong(position: Int) {
        val oldPlaying = mPlayingItem
        mPlayingItem = if(position >= 0) position else -1
        if(oldPlaying >= 0)
            notifyItemChanged(oldPlaying, PAYLOAD_IS_PLAYING)
        if(mPlayingItem >= 0)
            notifyItemChanged(position, PAYLOAD_IS_PLAYING)
    }

    companion object {
        const val PAYLOAD_IS_PLAYING = "PAYLOAD_IS_PLAYING"
    }
}