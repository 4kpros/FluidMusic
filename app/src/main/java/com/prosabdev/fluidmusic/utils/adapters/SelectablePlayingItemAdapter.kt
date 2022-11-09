package com.prosabdev.fluidmusic.utils.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class SelectablePlayingItemAdapter<VH : RecyclerView.ViewHolder>() : SelectableRecycleViewAdapter<VH>() {
    private val TAG: String = SelectablePlayingItemAdapter::class.java.simpleName
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
            notifyItemChanged(oldPlaying)
        if(mPlayingItem >= 0)
            notifyItemChanged(position)
    }
}