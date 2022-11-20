package com.prosabdev.fluidmusic.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemPlayerCardViewBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerPageAdapter(
    private val mContext: Context,
    private val mListener: OnItemClickListener
) : ListAdapter<SongItem,PlayerPageAdapter.PlayerPageHolder>(DiffCallback) {

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
            tempItemPlayerCardViewBinding
        )
    }

    override fun onBindViewHolder(
        holder: PlayerPageHolder,
        position: Int
    ) {
        holder.bindListener(position, mListener)
        holder.loadCovertArt(mContext, getItem(position).covertArt?.binaryData)
    }

    override fun onViewRecycled(holder: PlayerPageHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem()
    }

    class PlayerPageHolder(private val mItemPlayerCardViewBinding: ItemPlayerCardViewBinding) : RecyclerView.ViewHolder(mItemPlayerCardViewBinding.root) {
        fun recycleItem(){
            mItemPlayerCardViewBinding.playerViewpagerImageview.layout(0,0,0,0)
            mItemPlayerCardViewBinding.playerViewpagerImageview.setImageDrawable(null)
        }

        var job : Job? = null
        fun bindListener(position: Int, listener: OnItemClickListener) {
            mItemPlayerCardViewBinding.playerViewpagerContainer.setOnClickListener(View.OnClickListener {
                if(job != null)
                    job?.cancel()
                job = MainScope().launch {
                    animateButtons()
                }
            })
            mItemPlayerCardViewBinding.buttonLyrics.setOnClickListener(View.OnClickListener {
                listener.onButtonLyricsClicked(
                    position
                )
            })
            mItemPlayerCardViewBinding.buttonFullscreen.setOnClickListener(View.OnClickListener {
                listener.onButtonFullscreenClicked(
                    position
                )
            })
        }

        private suspend fun animateButtons() {
            CustomAnimators.crossFadeUp(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            CustomAnimators.crossFadeUp(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
            delay(2000)
            CustomAnimators.crossFadeDown(mItemPlayerCardViewBinding.buttonLyrics as View, true)
            CustomAnimators.crossFadeDown(mItemPlayerCardViewBinding.buttonFullscreen as View, true)
        }

        fun loadCovertArt(context: Context, tempBinary: ByteArray?) {
            mItemPlayerCardViewBinding.playerViewpagerImageview.layout(0,0,0,0)
            MainScope().launch{
                CustomUILoaders.loadCovertArtFromBinaryData(
                    context,
                    mItemPlayerCardViewBinding.playerViewpagerImageview,
                    tempBinary,
                    450)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SongItem>() {
            override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
                return oldItem.absolutePath == newItem.absolutePath
            }
        }
    }
}