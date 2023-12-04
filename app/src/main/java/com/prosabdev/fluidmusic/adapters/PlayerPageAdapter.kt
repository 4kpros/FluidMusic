package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.utils.Animators
import com.prosabdev.common.utils.ImageLoaders
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlayerCardViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayingNowPageAdapter(
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : ListAdapter<SongItem, PlayingNowPageAdapter.PlayingNowPageHolder>(
    SongItem.diffCallbackViewPager) {

    interface OnItemClickListener {
        fun onButtonLyricsClicked(position: Int)
        fun onButtonFullscreenClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayingNowPageHolder {
        val itemBinding: ItemPlayerCardViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_player_card_view, parent, false
        )
        return PlayingNowPageHolder(
            itemBinding,
            mListener
        )
    }

    override fun onBindViewHolder(
        holder: PlayingNowPageHolder,
        position: Int
    ) {
        holder.loadCovertArt(mContext, getItem(position))
    }

    class PlayingNowPageHolder(
        private val mItemPlayerCardViewBinding: ItemPlayerCardViewBinding,
        listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mItemPlayerCardViewBinding.root) {

        private var mAnimateButtonsJob : Job? = null

        init {
            mItemPlayerCardViewBinding.playerViewpagerContainer.setOnClickListener {
                if (mAnimateButtonsJob != null)
                    mAnimateButtonsJob?.cancel()
                mAnimateButtonsJob = MainScope().launch {
                    animateButtons()
                }
            }
            mItemPlayerCardViewBinding.buttonLyrics.setOnClickListener {
                listener.onButtonLyricsClicked(
                    bindingAdapterPosition
                )
            }
            mItemPlayerCardViewBinding.buttonFullscreen.setOnClickListener {
                listener.onButtonFullscreenClicked(
                    bindingAdapterPosition
                )
            }
        }

        private suspend fun animateButtons() {
            Animators.crossFadeUp(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            Animators.crossFadeUp(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
            delay(2000)
            Animators.crossFadeDown(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            Animators.crossFadeDown(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
        }

        fun loadCovertArt(ctx: Context, songItem: SongItem) {
            val tempUri : Uri? = Uri.parse(songItem.uri ?: "")
            val imageRequest: ImageLoaders.ImageRequestItem = ImageLoaders.ImageRequestItem.newOriginalLargeCardInstance()
            imageRequest.uri = tempUri
            imageRequest.imageView = mItemPlayerCardViewBinding.playerViewpagerImageview
            imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
            ImageLoaders.startExploreContentImageLoaderJob(ctx, imageRequest)
        }
    }
}