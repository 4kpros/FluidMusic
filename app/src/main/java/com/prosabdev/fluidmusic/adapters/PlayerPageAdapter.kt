package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerPageAdapter(
    private val mSongList: List<SongItem>?,
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : Adapter<PlayerPageAdapter.PlayerPageHolder>() {

    interface OnItemClickListener {
        fun onButtonLyricsClicked(position: Int)
        fun onButtonFullscreenClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_player_carview, parent, false)

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
        val tempBinary: ByteArray? = mSongList!![position].covertArt?.binaryData
        CustomUILoaders.loadCovertArtFromBinaryData(mContext, covertArtView, tempBinary, 450)
    }

    override fun getItemCount(): Int {
        return mSongList?.size ?: 0
    }

    class PlayerPageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mContainer: MaterialCardView? = itemView.findViewById(R.id.player_viewpager_container)
        var mCovertArt: ImageView? = itemView.findViewById(R.id.player_viewpager_imageview)
        var mLyricsButton: MaterialButton? = itemView.findViewById(R.id.button_lyrics)
        var mFullscreenButton: MaterialButton? = itemView.findViewById(R.id.button_fullscreen)

        var job : Job? = null

        fun bindListener(position: Int, listener: OnItemClickListener) {
            mContainer?.setOnClickListener(View.OnClickListener {
                if(job != null)
                    job?.cancel()
                job = MainScope().launch {
                    animateButtons()
                }
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

        private suspend fun animateButtons() {
            CustomAnimators.crossFadeUp(this.mLyricsButton as View, true)
            CustomAnimators.crossFadeUp(mFullscreenButton as View, true)
            delay(2000)
            CustomAnimators.crossFadeDown(this.mLyricsButton as View, true)
            CustomAnimators.crossFadeDown(mFullscreenButton as View, true)
        }
    }
}