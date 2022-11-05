package com.prosabdev.fluidmusic.utils.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class SelectablePlayingItemAdapter<VH : RecyclerView.ViewHolder>() : SelectableRecycleViewAdapter<VH>() {
    private val TAG: String = SelectablePlayingItemAdapter::class.java.simpleName
    private var playingItem: Int = -1

    protected fun selectablePlayingIsPlaying(position: Int): Boolean {
        return position == playingItem && playingItem >= 0
    }
    protected fun selectablePlayingGetCurrentPlayingSong(): Int {
        return playingItem
    }
    protected fun selectablePlayingSetCurrentPlayingSong(position: Int) {
        playingItem = if(position >= 0) position else -1
        notifyItemChanged(position)
    }
}