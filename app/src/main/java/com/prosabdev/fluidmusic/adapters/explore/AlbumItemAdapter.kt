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
import com.prosabdev.fluidmusic.models.collections.AlbumItem
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.adapters.SelectablePlayingItemAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
            .inflate(R.layout.item_album, parent, false)

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
        private var mContainer: MaterialCardView? = itemView.findViewById<MaterialCardView>(R.id.album_item_container)
        private var mCovertArt: ImageView? = itemView.findViewById<ImageView>(R.id.album_item_imageview)
        private var mTitle: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.album_item_title)
        private var mArtist: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.album_item_artist)
        private var mCountSongs: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.album_item_count_songs)
        private var mTotalDuration: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.album_item_total_duration)
        private var mIsSelectedCheckbox: LinearLayoutCompat? = itemView.findViewById<LinearLayoutCompat>(R.id.album_item_is_selected_checkbox)

        //Update album item UI
        fun updateUI(context: Context, albumItem: AlbumItem, selected: Boolean){
            mTitle?.text = if(albumItem.album != null && albumItem.album!!.isNotEmpty()) albumItem.album else albumItem.albumArtist //Set song title
            if(albumItem.artist != null && albumItem.artist!!.isNotEmpty()){
                mArtist?.text = albumItem.artist
                mArtist?.visibility = View.VISIBLE
            }else{
                mArtist?.text = ""
                mArtist?.visibility = View.GONE
            }
            mCountSongs?.text = albumItem.countSongs.toString()
            mTotalDuration?.text = CustomFormatters.formatSongDurationToString(albumItem.totalDuration) //Set song duration
            //Set is is playing or is checked(for multiple item selection)
            mIsSelectedCheckbox?.visibility = if(selected) View.VISIBLE else View.GONE
            //Set song covert art
            val tempBinary: ByteArray? = albumItem.covertArt?.binaryData
            MainScope().launch {
                CustomUILoaders.loadCovertArtFromBinaryData(context, mCovertArt, tempBinary, 100)
            }
        }

        //Method used to bind one listener with items events click
        fun bindListener(position: Int, listener: OnItemClickListener) {
            mContainer?.setOnClickListener {
                listener.onAlbumItemClicked(position)
            }
            mContainer?.setOnLongClickListener {
                listener.onAlbumItemLongClicked(position)
                true
            }
        }
    }
}