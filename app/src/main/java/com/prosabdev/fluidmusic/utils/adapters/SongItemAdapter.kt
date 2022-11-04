package com.prosabdev.fluidmusic.utils.adapters

import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.utils.SelectableRecycleViewAdapter

abstract class SongItemAdapter<VH : RecyclerView.ViewHolder>() : SelectableRecycleViewAdapter<VH>() {
    private val TAG: String = SongItemAdapter::class.java.simpleName
    private var playingItem: Int = -1

    fun isPlaying(position: Int): Boolean {
        return position == playingItem && playingItem >= 0
    }
    fun getCurrentPlayingSong(): Int {
        return playingItem
    }

    fun setCurrentPlayingSong(position: Int) {
        playingItem = if(position >= 0) position else -1
        notifyItemChanged(position)
    }
}