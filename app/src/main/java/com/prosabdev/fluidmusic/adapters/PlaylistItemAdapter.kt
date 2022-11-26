package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlaylistBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItemView
import com.prosabdev.fluidmusic.utils.FormattersUtils

class PlaylistItemViewViewAdapter(
    private val mListener: OnItemClickListener
) : ListAdapter<PlaylistItemView, PlaylistItemViewViewAdapter.PlaylistItemViewHolder>(DiffCallback){

    interface OnItemClickListener {
        fun onClickListener(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistItemViewHolder {
        val tempItemPlaylistBinding: ItemPlaylistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist,
            parent,
            false
        )
        return PlaylistItemViewHolder(tempItemPlaylistBinding)
    }

    override fun onBindViewHolder(
        holder: PlaylistItemViewHolder,
        position: Int
    ) {
        holder.bindData(mListener, position)
        holder.updateUI(getItem(position))
    }

    class PlaylistItemViewHolder(private val mItemPlaylistBinding: ItemPlaylistBinding): RecyclerView.ViewHolder(mItemPlaylistBinding.root) {
        fun bindData(mListener: OnItemClickListener, position: Int) {
            mItemPlaylistBinding.cardViewClickable.setOnClickListener{
                mListener.onClickListener(position)
            }
        }

        fun updateUI(playlistItemView: PlaylistItemView){
            mItemPlaylistBinding.textTitle.text = playlistItemView.playlistName
            mItemPlaylistBinding.textSubtitle.text = "${playlistItemView.numberTracks} songs | ${FormattersUtils.formatSongDurationToString(playlistItemView.totalDuration)} min"

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PlaylistItemView>() {
            override fun areItemsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem.playlistId == newItem.playlistId
            }

            override fun areContentsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem == newItem
            }
        }
    }
}