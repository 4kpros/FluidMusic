package com.prosabdev.fluidmusic.adapters.explore

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
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.generic.SelectablePlayingItemListAdapter
import com.prosabdev.fluidmusic.databinding.ItemGenericExploreListBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.FormattersUtils
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SongItemAdapter(
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener
    ) : SelectablePlayingItemListAdapter<SongItemAdapter.SongItemViewHolder>(SongItem.diffCallback as DiffUtil.ItemCallback<Any>),
    FastScroller.SectionIndexer {
    interface OnItemClickListener {
        fun onSongItemClicked(position: Int)
        fun onSongItemLongClicked(position: Int)
    }

    //Methods for selectable playing
    fun getIsPlaying(): Boolean {
        return getSelectableIsPlaying()
    }
    fun setIsPlaying(isPlaying: Boolean) {
        return setSelectableIsPlaying(isPlaying)
    }
    fun getPlayingPosition(): Int {
        return getSelectablePlayingPosition()
    }
    fun setPlayingPosition(position: Int) {
        setSelectablePlayingPosition(position)
    }

    //Methods for selectable items
    fun selectableGetSelectionMode(): Boolean {
        return selectableItemGetSelectionMode()
    }
    fun selectableSetSelectionMode(value : Boolean, layoutManager : GridLayoutManager) {
        return selectableItemSetSelectionMode(value, layoutManager)
    }
    private fun selectableIsSelected(position: Int): Boolean {
        return selectableItemIsSelected(position)
    }
    fun selectableOnSelectFromPosition(position: Int, layoutManager : GridLayoutManager? = null) {
        selectableItemOnSelectFromPosition(position, mOnSelectSelectableItemListener, layoutManager)
    }
    fun selectableOnSelectRange(layoutManager : GridLayoutManager? = null) {
        selectableItemOnSelectRange(mOnSelectSelectableItemListener, layoutManager)
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

    override fun getSectionText(position: Int): CharSequence {
        val tempText: String =
            if(position >= 0 && position < currentList.size)
                    (currentList[position] as SongItem?)?.title ?:
                    (currentList[position] as SongItem?)?.fileName ?:
                    "#"
            else
                "#"
        return tempText.substring(0, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        val dataBinding: ItemGenericExploreListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_generic_explore_list, parent, false
        )
        return SongItemViewHolder(
            dataBinding,
            mOnItemClickListener
        )
    }
    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_IS_SELECTED -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
                        holder.updateSelectedStateUI(selectableIsSelected(position))
                    }
                    PAYLOAD_PLAYBACK_STATE -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_PLAYBACK_STATE")
                        holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
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
            //If the is no payload specified on notify adapter, refresh all UI to be safe
            holder.updateSelectedStateUI(selectableIsSelected(position))
            holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
            holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
        }
    }

    override fun onViewRecycled(holder: SongItemViewHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem(mContext)
    }

    override fun onViewAttachedToWindow(holder: SongItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.updateSelectedStateUI(selectableIsSelected(holder.bindingAdapterPosition))
    }

    class SongItemViewHolder(
        private val mItemGenericExploreListBinding: ItemGenericExploreListBinding,
        mOnItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mItemGenericExploreListBinding.root) {
        init {
            mItemGenericExploreListBinding.cardViewClickable.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(bindingAdapterPosition)
            }
            mItemGenericExploreListBinding.cardViewClickable.setOnLongClickListener {
                mOnItemClickListener.onSongItemLongClicked(bindingAdapterPosition)
                true
            }
        }

        fun recycleItem(ctx : Context){
            Glide.with(ctx.applicationContext).clear(mItemGenericExploreListBinding.imageviewCoverArt)
        }

        fun updateCovertArtAndTitleUI(context: Context, songItem: SongItem) {
            var tempTitle : String = songItem.title ?: ""
            var tempArtist : String = songItem.artist ?: ""
            if(tempTitle.isEmpty()) tempTitle = songItem.fileName ?: context.getString(R.string.unknown_title)
            if(tempArtist.isEmpty()) tempArtist = context.getString(R.string.unknown_artist)
            mItemGenericExploreListBinding.textTitle.text = tempTitle
            mItemGenericExploreListBinding.textSubtitle.text = tempArtist
            mItemGenericExploreListBinding.textDetails.text =
                context.getString(
                    R.string.item_song_card_text_details,
                    FormattersUtils.formatSongDurationToString(songItem.duration),
                    songItem.fileExtension
                )

            MainScope().launch {
                val tempUri: Uri? = Uri.parse(songItem.uri)
                ImageLoadersUtils.loadCovertArtFromSongUri(context, mItemGenericExploreListBinding.imageviewCoverArt, tempUri, 100, 50, true)
            }
        }

        fun updateIsPlayingStateUI(ctx : Context, isPlaying: Boolean, playingPosition : Int) {
            if(playingPosition == bindingAdapterPosition){
                mItemGenericExploreListBinding.textTitle.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textSubtitle.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textDetails.setTypeface(null, Typeface.BOLD)
                mItemGenericExploreListBinding.textNowPlaying.setTypeface(null, Typeface.BOLD)

                val value = MaterialColors.getColor(mItemGenericExploreListBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                mItemGenericExploreListBinding.textTitle.setTextColor(value)
                mItemGenericExploreListBinding.textSubtitle.setTextColor(value)
                mItemGenericExploreListBinding.textDetails.setTextColor(value)
                if(isPlaying){
                    mItemGenericExploreListBinding.textNowPlaying.text = ctx.getString(R.string.playing)
                }else{
                    mItemGenericExploreListBinding.textNowPlaying.text = ctx.getString(R.string.paused)
                }
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
                ViewAnimatorsUtils.crossFadeUp(mItemGenericExploreListBinding.songItemIsSelected, animated, 150)
            else if(!selectableIsSelected && mItemGenericExploreListBinding.songItemIsSelected.alpha == 1.0f)
                ViewAnimatorsUtils.crossFadeDown(mItemGenericExploreListBinding.songItemIsSelected, animated, 150)
        }
    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}