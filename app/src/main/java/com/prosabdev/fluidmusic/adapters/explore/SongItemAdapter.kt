package com.prosabdev.fluidmusic.adapters.explore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.ItemGenericExploreListBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.adapters.generic.SelectablePlayingItemListAdapter
import com.prosabdev.fluidmusic.utils.CustomFormatters
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch



class SongItemAdapter(
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener
    ) : SelectablePlayingItemListAdapter<SongItemAdapter.SongItemViewHolder>(diffCallback as DiffUtil.ItemCallback<Any>)
    {
    interface OnItemClickListener {
        fun onSongItemClicked(position: Int)
        fun onSongItemPlayClicked(position: Int)
        fun onSongItemLongClicked(position: Int)
    }

    //Methods for selectable playing
    private fun isPlaying(position: Int): Boolean {
        return selectablePlayingIsPlaying(position)
    }
    fun getCurrentPlayingSong(): Int {
        return selectablePlayingGetCurrentPlayingSong()
    }
    fun setCurrentPlayingSong(position: Int) {
        selectablePlayingSetCurrentPlayingSong(position)
    }

    //Methods for selectable items
    fun selectableGetSelectionMode(): Boolean {
        return selectableItemGetSelectionMode()
    }
    fun selectableSetSelectionMode(value : Boolean, layoutManager : GridLayoutManager? = null) {
        return selectableItemSetSelectionMode(value, layoutManager)
    }
    fun selectableIsSelected(position: Int): Boolean {
        return selectableItemIsSelected(position)
    }
    fun selectableToggleSelection(position: Int, layoutManager : GridLayoutManager? = null) {
        selectableItemToggleSelection(position, layoutManager)
    }
    fun selectableUpdateSelection(position: Int, value : Boolean) {
        selectableItemUpdateSelection(position, value)
    }
    fun selectableToggleSelectRange(layoutManager : GridLayoutManager? = null) {
        selectableItemToggleSelectRange(mOnSelectSelectableItemListener, layoutManager)
    }
    fun selectableGetSelectedItemCount(): Int {
        return selectableItemGetSelectedItemCount()
    }
    fun selectableSelectAll(layoutManager : GridLayoutManager? = null) {
        selectableItemSelectAll(layoutManager)
    }
    fun selectableClearSelection(layoutManager : GridLayoutManager? = null) {
        selectableItemClearAllSelection(layoutManager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        val tempItemGenericExploreListBinding: ItemGenericExploreListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_generic_explore_list, parent, false
        )
        return SongItemViewHolder(
            tempItemGenericExploreListBinding
        )
    }
    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        holder.bindListener(position, mOnItemClickListener)
        holder.updateAllUI(mContext, getItem(position) as SongItem, isPlaying(position), selectableIsSelected(position))
    }
    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    SelectableItemListAdapter.PAYLOAD_IS_SELECTED -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
                        holder.updateSelectedStateUI(selectableIsSelected(position))
                    }
                    PAYLOAD_IS_PLAYING -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_PLAYING")
                        holder.updateIsPlayingStateUI(isPlaying(position))
                    }
                    PAYLOAD_IS_COVERT_ART_TEXT -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_COVERT_ART_TEXT")
                        holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
                    }
                    else -> {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewRecycled(holder: SongItemViewHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem(mContext)
    }

    class SongItemViewHolder(private val mItemGenericExploreListBinding: ItemGenericExploreListBinding) : RecyclerView.ViewHolder(mItemGenericExploreListBinding.root) {
        fun updateAllUI(context: Context, songItem: SongItem, isPlaying: Boolean, selected: Boolean){
            updateSelectedStateUI(selected, false)
            updateIsPlayingStateUI(isPlaying)
            updateCovertArtAndTitleUI(context, songItem)
        }
        fun recycleItem(ctx : Context){
            Glide.with(ctx).clear(mItemGenericExploreListBinding.imageviewCoverArt)
        }
        fun updateCovertArtAndTitleUI(context: Context, songItem: SongItem) {
            mItemGenericExploreListBinding.textTitle.text =
                if(songItem.title != null && songItem.title!!.isNotEmpty())
                    songItem.title
                else
                    songItem.fileName

            mItemGenericExploreListBinding.textSubtitle.text =
                if(songItem.artist != null && songItem.artist!!.isNotEmpty())
                    songItem.artist
                else
                    context.getString(R.string.unknown_artist)

            mItemGenericExploreListBinding.textDetails.text =
                context.getString(
                    R.string.item_song_card_text_details,
                    CustomFormatters.formatSongDurationToString(songItem.duration),
                    songItem.typeMime
                )

            MainScope().launch {
                val tempUri: Uri? = Uri.parse(songItem.uri)
                CustomUILoaders.loadCovertArtFromSongUri(context, mItemGenericExploreListBinding.imageviewCoverArt, tempUri, 100)
            }
        }
        fun updateIsPlayingStateUI(playing: Boolean) {
            if(playing){
                mItemGenericExploreListBinding.textTitle.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textSubtitle.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textDetails.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textNowPlaying.setTypeface(null, Typeface.BOLD)

                val value = MaterialColors.getColor(mItemGenericExploreListBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                mItemGenericExploreListBinding.textTitle.setTextColor(value)
                mItemGenericExploreListBinding.textSubtitle.setTextColor(value)
                mItemGenericExploreListBinding.textDetails.setTextColor(value)
                mItemGenericExploreListBinding.textNowPlaying.setTextColor(value)
                mItemGenericExploreListBinding.textNowPlaying.visibility = VISIBLE
            }else{
                mItemGenericExploreListBinding.textTitle.setTypeface(null, Typeface.NORMAL)
                mItemGenericExploreListBinding.textSubtitle.setTypeface(null, Typeface.NORMAL)
                mItemGenericExploreListBinding.textDetails.setTypeface(null, Typeface.NORMAL)
                mItemGenericExploreListBinding.textNowPlaying.setTypeface(null, Typeface.NORMAL)

                val value = MaterialColors.getColor(mItemGenericExploreListBinding.textTitle as View, com.google.android.material.R.attr.colorOnBackground)
                mItemGenericExploreListBinding.textTitle.setTextColor(value)
                mItemGenericExploreListBinding.textSubtitle.setTextColor(value)
                mItemGenericExploreListBinding.textDetails.setTextColor(value)
                mItemGenericExploreListBinding.textNowPlaying.setTextColor(value)
                mItemGenericExploreListBinding.textNowPlaying.visibility = INVISIBLE
            }
        }
        fun updateSelectedStateUI(selectableIsSelected: Boolean, animated: Boolean = true) {
            if(selectableIsSelected && mItemGenericExploreListBinding.songItemIsSelected.visibility != VISIBLE)
                CustomAnimators.crossFadeUp(mItemGenericExploreListBinding.songItemIsSelected, animated)
            else if(!selectableIsSelected && mItemGenericExploreListBinding.songItemIsSelected.alpha == 1.0f)
                CustomAnimators.crossFadeDown(mItemGenericExploreListBinding.songItemIsSelected, animated)
        }
        fun bindListener(
            position: Int,
            mOnItemClickListener: OnItemClickListener,
        ) {
            mItemGenericExploreListBinding.linearCoverArtContainer.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(position)
            }
            mItemGenericExploreListBinding.linearCoverArtContainer.setOnLongClickListener {
                mOnItemClickListener.onSongItemLongClicked(position)
                true
            }
            mItemGenericExploreListBinding.imageviewCoverArt.setOnClickListener {
                mOnItemClickListener.onSongItemPlayClicked(position)
            }
        }
    }

        companion object {
            const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"

            val diffCallback = object : DiffUtil.ItemCallback<SongItem>() {
                override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
                    return oldItem == newItem
                }
            }
        }
}