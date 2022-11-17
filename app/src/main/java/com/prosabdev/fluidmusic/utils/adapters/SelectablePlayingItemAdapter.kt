package com.prosabdev.fluidmusic.utils.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class SelectablePlayingItemAdapter<VH : RecyclerView.ViewHolder> : SelectableItemListAdapter<VH>() {
    private val mTAG: String = SelectablePlayingItemAdapter::class.java.simpleName
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