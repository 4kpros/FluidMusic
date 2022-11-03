package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import org.jaudiotagger.tag.images.Artwork


class PlayerPageAdapter(
    private val mSongList: List<SongItem>?,
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : Adapter<PlayerPageAdapter.PlayerPageHolder>() {

    interface OnItemClickListener {
        fun onViewPagerClicked(position: Int)
        fun onButtonLyricsClicked(position: Int)
        fun onButtonFullscreenClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_viewpager_item_image, parent, false)

        return PlayerPageHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlayerPageHolder,
        position: Int
    ) {
        //Bind listener for to capture click events
        holder.bindListener(position, mListener);

        //Set covert art
        loadCovertArt(holder.mCovertArt, position);
    }

    private fun loadCovertArt(covertArtView: ImageView?, position: Int) {
        val artwork: Artwork? = mSongList!![position].covertArt
        val tempBinaryData: ByteArray? = artwork?.binaryData
        if (covertArtView != null) {
            if(tempBinaryData != null && tempBinaryData.isNotEmpty()){
                Glide.with(mContext)
                    .load(tempBinaryData)
                    .useAnimationPool(true)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(SIZE_ORIGINAL)
                    )
                    .centerCrop()
                    .into(covertArtView)
            }else{
                Glide.with(mContext)
                    .load(ContextCompat.getDrawable(mContext.applicationContext, R.drawable.fashion))
                    .useAnimationPool(true)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(SIZE_ORIGINAL)
                    )
                    .centerCrop()
                    .into(covertArtView)
            }
        }
    }

    override fun getItemCount(): Int {
        return mSongList?.size ?: 0
    }

    class PlayerPageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mContainer: MaterialCardView? = itemView.findViewById(R.id.player_viewpager_container)
        var mCovertArt: ImageView? = itemView.findViewById(R.id.player_viewpager_imageview)
        var mLyricsButton: MaterialButton? = itemView.findViewById(R.id.button_lyrics)
        var mFullscreenButton: MaterialButton? = itemView.findViewById(R.id.button_fullscreen)

        fun bindListener(position: Int, listener: OnItemClickListener) {
            mContainer?.setOnClickListener(View.OnClickListener {
                listener.onViewPagerClicked(
                    position
                )
            })
            mLyricsButton?.setOnClickListener(View.OnClickListener {
                listener.onButtonLyricsClicked(
                    position
                )
            })
            mFullscreenButton?.setOnClickListener(View.OnClickListener {
                listener.onButtonFullscreenClicked(
                    position
                )
            })
        }
    }
}