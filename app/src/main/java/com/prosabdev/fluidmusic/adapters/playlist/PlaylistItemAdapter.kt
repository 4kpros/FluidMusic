package com.prosabdev.fluidmusic.adapters.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlaylistBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistItemView
import com.prosabdev.fluidmusic.utils.FormattersUtils

class PlaylistItemAdapter(
    private val mListener: OnItemClickListener
) : ListAdapter<PlaylistItem, PlaylistItemAdapter.PlaylistItemHolder>(
    PlaylistItem.diffCallback
){
    interface OnItemClickListener {
        fun onClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistItemHolder {
        val tempItemPlaylistBinding: ItemPlaylistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist,
            parent,
            false
        )
        return PlaylistItemHolder(tempItemPlaylistBinding, mListener)
    }

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {
        holder.updateUI(getItem(position))
    }

    class PlaylistItemHolder(
        private val mItemPlaylistBinding: ItemPlaylistBinding,
        listener: OnItemClickListener
    ): RecyclerView.ViewHolder(mItemPlaylistBinding.root) {
        init {
            mItemPlaylistBinding.cardViewClickable.setOnClickListener{
                listener.onClickListener(bindingAdapterPosition)
            }
        }

        fun updateUI(playlistItem: PlaylistItem){
            mItemPlaylistBinding.textTitle.text = playlistItem.name
        }
    }
}