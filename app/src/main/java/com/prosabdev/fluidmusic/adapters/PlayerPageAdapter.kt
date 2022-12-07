package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlayerCardViewBinding
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerPageAdapter(
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : ListAdapter<SongItem, PlayerPageAdapter.PlayerPageHolder>(SongItem.diffCallbackViewPager) {

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

        private suspend fun animateButtons() {
            AnimatorsUtils.crossFadeUp(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            AnimatorsUtils.crossFadeUp(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
            delay(2000)
            AnimatorsUtils.crossFadeDown(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            AnimatorsUtils.crossFadeDown(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
        }

        fun loadCovertArt(ctx: Context, songItem: SongItem) {
            val tempUri : Uri? = Uri.parse(songItem.uri ?: "")
            val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newLargeOriginalCardInstance()
            imageRequest.uri = tempUri
            imageRequest.imageView = mItemPlayerCardViewBinding.playerViewpagerImageview
            imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
            ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
        }
    }
}