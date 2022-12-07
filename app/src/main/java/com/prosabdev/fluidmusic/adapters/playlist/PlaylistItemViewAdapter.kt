package com.prosabdev.fluidmusic.adapters.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlaylistBinding
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
        val tempItemPlaylistBinding: ItemPlaylistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist,
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
        private val mItemPlaylistBinding: ItemPlaylistBinding,
        listener: OnItemClickListener
    ): RecyclerView.ViewHolder(mItemPlaylistBinding.root) {
        init {
            mItemPlaylistBinding.cardViewClickable.setOnClickListener{
                listener.onClickListener(bindingAdapterPosition)
            }
        }

        fun updateUI(playlistItemView: PlaylistItemView){
            mItemPlaylistBinding.textTitle.text = playlistItemView.name
            mItemPlaylistBinding.textSubtitle.text = "${playlistItemView.numberTracks} songs | ${FormattersAndParsersUtils.formatSongDurationToString(playlistItemView.totalDuration)} min"

        }
    }
}