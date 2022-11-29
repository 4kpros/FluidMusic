package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlayerCardViewBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerPageAdapter(
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : ListAdapter<SongItem, PlayerPageAdapter.PlayerPageHolder>(SongItem.diffCallback) {

    interface OnItemClickListener {
        fun onButtonLyricsClicked(position: Int)
        fun onButtonFullscreenClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPageHolder {
        val tempItemPlayerCardViewBinding: ItemPlayerCardViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_player_card_view, parent, false
        )
        return PlayerPageHolder(
            tempItemPlayerCardViewBinding,
            mListener
        )
    }

    override fun onBindViewHolder(
        holder: PlayerPageHolder,
        position: Int
    ) {
        holder.loadCovertArt(mContext, getItem(position))
    }

    override fun onViewRecycled(holder: PlayerPageHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem(mContext)
    }

    class PlayerPageHolder(
        private val mItemPlayerCardViewBinding: ItemPlayerCardViewBinding,
        listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mItemPlayerCardViewBinding.root) {

        var mAnimateButtonsJob : Job? = null
        init {
            mItemPlayerCardViewBinding.playerViewpagerContainer.setOnClickListener(View.OnClickListener {
                if(mAnimateButtonsJob != null)
                    mAnimateButtonsJob?.cancel()
                mAnimateButtonsJob = MainScope().launch {
                    animateButtons()
                }
            })
            mItemPlayerCardViewBinding.buttonLyrics.setOnClickListener(View.OnClickListener {
                listener.onButtonLyricsClicked(
                    bindingAdapterPosition
                )
            })
            mItemPlayerCardViewBinding.buttonFullscreen.setOnClickListener(View.OnClickListener {
                listener.onButtonFullscreenClicked(
                    bindingAdapterPosition
                )
            })
        }

        fun recycleItem(ctx : Context){
            Glide.with(ctx).clear(mItemPlayerCardViewBinding.playerViewpagerImageview)
        }

        private suspend fun animateButtons() {
            ViewAnimatorsUtils.crossFadeUp(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            ViewAnimatorsUtils.crossFadeUp(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
            delay(2000)
            ViewAnimatorsUtils.crossFadeDown(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            ViewAnimatorsUtils.crossFadeDown(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
        }

        fun loadCovertArt(context: Context, songItem: SongItem) {
            mItemPlayerCardViewBinding.playerViewpagerImageview.layout(0,0,0,0)
            val tempUri : Uri? = Uri.parse(songItem.uri ?: "")
            MainScope().launch {
                ImageLoadersUtils.loadCovertArtFromSongUri(
                    context,
                    mItemPlayerCardViewBinding.playerViewpagerImageview,
                    tempUri,
                    300,
                    200,
                    false
                )
            }
        }
    }
}