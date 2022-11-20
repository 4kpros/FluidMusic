package com.prosabdev.fluidmusic.adapters.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.explore.AlbumItem
import com.prosabdev.fluidmusic.utils.adapters.SelectablePlayingItemAdapter

class AlbumItemAdapter(
    private val mAlbumList: List<AlbumItem>?,
    private val mContext: Context,
    private val mListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
) : SelectablePlayingItemAdapter<AlbumItemAdapter.AlbumItemHolder>() {

    interface OnItemClickListener {
        fun onAlbumItemClicked(position: Int)
        fun onAlbumItemLongClicked(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generic_explore_list, parent, false)

        return AlbumItemHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumItemHolder, position: Int) {
        //Bind listener for to capture click events
        holder.bindListener(position, mListener)

        //Update UI
        holder.updateUI(mContext, mAlbumList?.get(position)!!, selectableItemIsSelected(position))
    }

    override fun getItemCount(): Int {
        return mAlbumList?.size ?: 0
    }

    class AlbumItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateUI(context: Context, albumItem: AlbumItem, selected: Boolean){

        }

        //Method used to bind one listener with items events click
        fun bindListener(position: Int, listener: OnItemClickListener) {

        }
    }
}