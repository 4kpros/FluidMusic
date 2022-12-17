package com.prosabdev.fluidmusic.adapters.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlaylistAddSongBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItemView
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils

class PlaylistItemViewAdapter(
    private val mListener: OnItemClickListener
) : ListAdapter<PlaylistItemView, PlaylistItemViewAdapter.PlaylistItemViewHolder>(PlaylistItemView.diffCallback){

    interface OnItemClickListener {
        fun onClickListener(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistItemViewHolder {
        val tempItemPlaylistBinding: ItemPlaylistAddSongBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist_add_song,
            parent,
            false
        )
        return PlaylistItemViewHolder(tempItemPlaylistBinding, mListener)
    }

    override fun onBindViewHolder(
        holder: PlaylistItemViewHolder,
        position: Int
    ) {
        holder.updateUI(getItem(position))
    }

    class PlaylistItemViewHolder(
        private val mItemPlaylistAddSongBinding: ItemPlaylistAddSongBinding,
        listener: OnItemClickListener
    ): RecyclerView.ViewHolder(mItemPlaylistAddSongBinding.root) {
        init {
            mItemPlaylistAddSongBinding.cardViewClickable.setOnClickListener{
                listener.onClickListener(bindingAdapterPosition)
            }
        }

        fun updateUI(playlistItemView: PlaylistItemView){
            mItemPlaylistAddSongBinding.textTitle.text = playlistItemView.name
            mItemPlaylistAddSongBinding.textSubtitle.text = "${playlistItemView.numberTracks} songs | ${FormattersAndParsersUtils.formatSongDurationToString(playlistItemView.totalDuration)} min"

        }
    }
}