package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.adapters.SongItemAdapter

class SongItemAdapter(
    private val mSongList: List<SongItem>?,
    private val mContext: Context,
    private val mListener: OnItemClickListener
    ) : SongItemAdapter<com.prosabdev.fluidmusic.adapters.SongItemAdapter.SongItemHolder>() {

    interface OnItemClickListener {
        fun onSongItemClicked(position: Int)
        fun onSongItemPlayClicked(position: Int)
        fun onSongItemLongClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)

        return SongItemHolder(view)
    }

    override fun onBindViewHolder(holder: SongItemHolder, position: Int) {
        //Bind listener for to capture click events
        holder.bindListener(position, mListener)

        //Update UI
        if(position < mSongList?.size!!){
            holder.updateUI(mContext, mSongList?.get(position)!!, isPlaying(position), isSelected(position))
        }
    }

    override fun getItemCount(): Int {
        return mSongList?.size ?: 0
    }

    class SongItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mContainer: MaterialCardView? = itemView.findViewById<MaterialCardView>(R.id.song_item_container)
        private var mCovertArt: ImageView? = itemView.findViewById<ImageView>(R.id.song_item_imageview)
        private var mTitle: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_title)
        private var mArtist: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_artist)
        private var mDuration: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_duration)
        private var mTypeMime: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_type_mime)
        private var mIsSelectedCheckbox: LinearLayoutCompat? = itemView.findViewById<LinearLayoutCompat>(R.id.song_item_is_selected_checkbox)
//        private var mIsPlayingBackground: LinearLayoutCompat? = itemView.findViewById<LinearLayoutCompat>(R.id.song_item_is_playing_background)

        //Update song item UI
        fun updateUI(context: Context, songItem: SongItem, isPlaying: Boolean, selected: Boolean){
            mTitle?.text = if(songItem.title != null && songItem.title!!.isNotEmpty()) songItem.title else songItem.fileName //Set song title
            if(songItem.artist != null && songItem.artist!!.isNotEmpty()){
                mArtist?.text = songItem.artist
                mArtist?.visibility = VISIBLE
            }else{
                mArtist?.text = ""
                mArtist?.visibility = GONE
            }
            mDuration?.text = CustomFormatters.formatSongDurationToString(songItem.duration) //Set song duration
            mTypeMime?.text = songItem.typeMime //Set song type mime
            //Set is is playing or is checked(for multiple item selection)
            mIsSelectedCheckbox?.visibility = if(selected) VISIBLE else GONE
            //Set song covert art
            if(songItem.covertArt != null && songItem.covertArt!!.binaryData != null && songItem.covertArt!!.binaryData.isNotEmpty()){
                Glide.with(context)
                    .load(songItem.covertArt?.binaryData)
                    .useAnimationPool(false)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .override(100)
                    )
                    .centerCrop()
                    .into(mCovertArt!!)
            }else{
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.ic_fluid_music_icon_with_padding))
                    .useAnimationPool(true)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .override(100)
                    )
                    .centerCrop()
                    .into(mCovertArt!!)
            }
        }

        //Method used to bind one listener with items events click
        fun bindListener(position: Int, listener: OnItemClickListener) {
            mContainer?.setOnClickListener {
                listener.onSongItemClicked(position)
            }
            mContainer?.setOnLongClickListener {
                listener.onSongItemLongClicked(position)
                true
            }
            mCovertArt?.setOnClickListener {
                listener.onSongItemPlayClicked(position)
            }
        }
    }
}