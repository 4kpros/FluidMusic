package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.callbacks.QueueMusicItemCallback
import com.prosabdev.fluidmusic.databinding.ItemQueueMusicBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.adapters.SelectablePlayingItemAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class QueueMusicItemAdapter(
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
    private val mOnTouchListener: OnTouchListener,
) : SelectablePlayingItemAdapter<QueueMusicItemAdapter.QueueMusicItemViewHolder>(),
    QueueMusicItemCallback.ItemTouchHelperContract
{

    interface OnItemClickListener {
        fun onSongItemClicked(position: Int)
        fun onSongItemPlayClicked(position: Int)
        fun onSongItemLongClicked(position: Int)
        fun onItemMovedTo(position: Int)
    }
    interface OnTouchListener {
        fun requestDrag(viewHolder: RecyclerView.ViewHolder?)
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

    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueMusicItemViewHolder {
        val tempItemQueueMusicBinding: ItemQueueMusicBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_player_card_view, parent, false
        )
        return QueueMusicItemViewHolder(
            tempItemQueueMusicBinding
        )
    }
    override fun onBindViewHolder(holder: QueueMusicItemViewHolder, position: Int) {
        holder.bindListener(holder, position, mOnItemClickListener, mOnTouchListener)
        holder.updateAllUI(mContext, getItem(position), isPlaying(position), selectableIsSelected(position))
    }
    override fun onBindViewHolder(holder: QueueMusicItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_IS_SELECTED -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
                        holder.updateSelectedStateUI(selectableIsSelected(position))
                    }
                    PAYLOAD_IS_PLAYING -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_PLAYING")
                        holder.updateIsPlayingStateUI(isPlaying(position))
                    }
                    PAYLOAD_IS_COVERT_ART_TEXT -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_COVERT_ART_TEXT")
                        holder.updateCovertArtAndTitleUI(mContext, getItem(position))
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

    override fun onViewRecycled(holder: QueueMusicItemViewHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem()
    }
    override fun onRowMoved(mFromPosition: Int, mToPosition: Int) {
        mOnItemClickListener.onItemMovedTo(mToPosition)
        if (mFromPosition < mToPosition) {
            for (i in mFromPosition until mToPosition) {
                selectableItemUpdateSelection(i, selectableIsSelected(i + 1))
                selectableItemUpdateSelection(i + 1, selectableIsSelected(i))
                Collections.swap(currentList, i, i + 1)
            }
        } else {
            for (i in mFromPosition downTo mToPosition + 1) {
                selectableItemUpdateSelection(i, selectableIsSelected(i - 1))
                selectableItemUpdateSelection(i - 1, selectableIsSelected(i))
                Collections.swap(currentList, i, i - 1)
            }
        }
        notifyItemMoved(mFromPosition, mToPosition)
    }

    override fun onRowSelected(myViewHolder: QueueMusicItemViewHolder?) {
        myViewHolder?.updateItemTouchHelper(true)
    }
    override fun onRowClear(myViewHolder: QueueMusicItemViewHolder?) {
        myViewHolder?.updateItemTouchHelper(false)
    }

    class QueueMusicItemViewHolder(private val mItemQueueMusicBinding: ItemQueueMusicBinding) : RecyclerView.ViewHolder(mItemQueueMusicBinding.root) {
        fun updateAllUI(context: Context, songItem: SongItem, isPlaying: Boolean, selected: Boolean){
            updateSelectedStateUI(selected, false)
            updateIsPlayingStateUI(isPlaying)
            updateCovertArtAndTitleUI(context, songItem)
        }

        fun recycleItem(){
            mItemQueueMusicBinding.imageviewCoverArt.setImageDrawable(null)
        }
        fun updateItemTouchHelper(selected : Boolean){
//            if(selected) CustomAnimators.crossScaleIn(mItemSongBinding.songItemContaineras View, true) else CustomAnimators.crossScaleOut(mItemSongBinding.songItemContaineras View, true)
        }
        fun updateCovertArtAndTitleUI(context: Context, songItem: SongItem) {
            mItemQueueMusicBinding.textTitle.text = if(songItem.title != null && songItem.title!!.isNotEmpty()) songItem.title else songItem.fileName
            mItemQueueMusicBinding.textSubtitle.text = if(songItem.artist!!.isNotEmpty()) songItem.artist else context.getString(
                R.string.unknown_artist)
            mItemQueueMusicBinding.textDetails.text = context.getString(R.string.item_song_card_text_details, songItem.duration, songItem.typeMime)

            val tempBinary: ByteArray? = songItem.covertArt?.binaryData
            MainScope().launch {
                CustomUILoaders.loadCovertArtFromBinaryData(context, mItemQueueMusicBinding.imageviewCoverArt, tempBinary, 100)
            }
        }
        fun updateIsPlayingStateUI(playing: Boolean) {
            if(playing){
                mItemQueueMusicBinding.textTitle.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textSubtitle.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textDetails.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textNowPlaying.setTypeface(null, Typeface.BOLD)

                val value = MaterialColors.getColor(mItemQueueMusicBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                mItemQueueMusicBinding.textTitle.setTextColor(value)
                mItemQueueMusicBinding.textSubtitle.setTextColor(value)
                mItemQueueMusicBinding.textDetails.setTextColor(value)
                mItemQueueMusicBinding.textNowPlaying.setTextColor(value)
                mItemQueueMusicBinding.textNowPlaying.visibility = VISIBLE
            }else{
                mItemQueueMusicBinding.textTitle.setTypeface(null, Typeface.NORMAL)
                mItemQueueMusicBinding.textSubtitle.setTypeface(null, Typeface.NORMAL)
                mItemQueueMusicBinding.textDetails.setTypeface(null, Typeface.NORMAL)
                mItemQueueMusicBinding.textNowPlaying.setTypeface(null, Typeface.NORMAL)

                val value = MaterialColors.getColor(mItemQueueMusicBinding.textTitle as View, com.google.android.material.R.attr.colorOnBackground)
                mItemQueueMusicBinding.textTitle.setTextColor(value)
                mItemQueueMusicBinding.textSubtitle.setTextColor(value)
                mItemQueueMusicBinding.textDetails.setTextColor(value)
                mItemQueueMusicBinding.textNowPlaying.setTextColor(value)
                mItemQueueMusicBinding.textNowPlaying.visibility = INVISIBLE
            }
        }
        fun updateSelectedStateUI(selectableIsSelected: Boolean, animated: Boolean = true) {
            if(selectableIsSelected && mItemQueueMusicBinding.songItemIsSelected.visibility != VISIBLE)
                CustomAnimators.crossFadeUp(mItemQueueMusicBinding.songItemIsSelected, animated)
            else if(!selectableIsSelected && mItemQueueMusicBinding.songItemIsSelected.alpha == 1.0f)
                CustomAnimators.crossFadeDown(mItemQueueMusicBinding.songItemIsSelected, animated)
        }
        fun bindListener(
            holder: QueueMusicItemViewHolder,
            position: Int,
            mOnItemClickListener: OnItemClickListener,
            mOnTouchListener: OnTouchListener
        ) {
            mItemQueueMusicBinding.linearCoverArtContainer.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(position)
            }
            mItemQueueMusicBinding.linearCoverArtContainer.setOnLongClickListener {
                mOnItemClickListener.onSongItemLongClicked(position)
                true
            }
            mItemQueueMusicBinding.imageviewCoverArt.setOnClickListener {
                mOnItemClickListener.onSongItemPlayClicked(position)
            }
            mItemQueueMusicBinding.buttonDrag.setOnTouchListener { view, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    mOnTouchListener.requestDrag(holder)
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    view?.performClick();
                }
                false
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}