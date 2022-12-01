package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.callbacks.QueueMusicItemCallback
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectablePlayingItemListAdapter
import com.prosabdev.fluidmusic.databinding.ItemQueueMusicBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.FormattersUtils
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class QueueMusicItemListAdapter(
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnTouchListener: OnTouchListener,
) : SelectablePlayingItemListAdapter<QueueMusicItemListAdapter.QueueMusicItemHolder>(SongItem.diffCallback as DiffUtil.ItemCallback<Any>),
    QueueMusicItemCallback.ItemTouchHelperContract
{

    interface OnItemClickListener {
        fun onSongItemClicked(position: Int)
    }
    interface OnTouchListener {
        fun onItemMovedTo(position: Int)
        fun requestDrag(viewHolder: RecyclerView.ViewHolder?)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueMusicItemHolder {
        val tempItemQueueMusicBinding: ItemQueueMusicBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_queue_music, parent, false
        )
        return QueueMusicItemHolder(
            tempItemQueueMusicBinding,
            mOnItemClickListener,
            mOnTouchListener
        )
    }
    override fun onBindViewHolder(holder: QueueMusicItemHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
    override fun onBindViewHolder(holder: QueueMusicItemHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_PLAYBACK_STATE -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_PLAYBACK_STATE")
                        holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
                    }
                    SongItemAdapter.PAYLOAD_IS_COVERT_ART_TEXT -> {
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
            holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
            holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
        }
    }

    override fun onViewRecycled(holder: QueueMusicItemHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem(mContext)
    }
    override fun onRowMoved(mFromPosition: Int, mToPosition: Int) {
        mOnTouchListener.onItemMovedTo(mToPosition)
        if (mFromPosition < mToPosition) {
            for (i in mFromPosition until mToPosition) {
                Collections.swap(currentList, i, i + 1)
            }
        } else {
            for (i in mFromPosition downTo mToPosition + 1) {
                Collections.swap(currentList, i, i - 1)
            }
        }
        notifyItemMoved(mFromPosition, mToPosition)
    }

    override fun onRowSelected(myViewHolder: QueueMusicItemHolder?) {
        myViewHolder?.updateItemTouchHelper(true)
    }
    override fun onRowClear(myViewHolder: QueueMusicItemHolder?) {
        myViewHolder?.updateItemTouchHelper(false)
    }

    class QueueMusicItemHolder(
        private val mItemQueueMusicBinding: ItemQueueMusicBinding,
        mOnItemClickListener: OnItemClickListener,
        mOnTouchListener: OnTouchListener
    ) : RecyclerView.ViewHolder(mItemQueueMusicBinding.root) {
        init {
            mItemQueueMusicBinding.linearCoverArtContainer.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(bindingAdapterPosition)
            }
            mItemQueueMusicBinding.buttonDrag.setOnTouchListener { view, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    mOnTouchListener.requestDrag(this)
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    view?.performClick();
                }
                false
            }
        }

        fun recycleItem(ctx : Context){
            Glide.with(ctx.applicationContext).clear(mItemQueueMusicBinding.imageviewCoverArt)
        }
        fun updateCovertArtAndTitleUI(context: Context, songItem: SongItem) {
            var tempTitle : String = songItem.title ?: ""
            var tempArtist : String = songItem.artist ?: ""
            if(tempTitle.isEmpty()) tempTitle = songItem.fileName ?: context.getString(R.string.unknown_title)
            if(tempArtist.isEmpty()) tempArtist = context.getString(R.string.unknown_artist)
            mItemQueueMusicBinding.textTitle.text = tempTitle
            mItemQueueMusicBinding.textSubtitle.text = tempArtist
            mItemQueueMusicBinding.textDetails.text =
                context.getString(
                    R.string.item_song_card_text_details,
                    FormattersUtils.formatSongDurationToString(songItem.duration),
                    songItem.fileExtension
                )

            MainScope().launch {
                val tempUri: Uri? = Uri.parse(songItem.uri)
                ImageLoadersUtils.loadCovertArtFromSongUri(context, mItemQueueMusicBinding.imageviewCoverArt, tempUri, 100, 50, true)
            }
        }
        fun updateIsPlayingStateUI(ctx : Context, isPlaying: Boolean, playingPosition : Int) {
            if(playingPosition == bindingAdapterPosition){
                mItemQueueMusicBinding.textTitle.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textSubtitle.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textDetails.setTypeface(null, Typeface.BOLD)
                mItemQueueMusicBinding.textNowPlaying.setTypeface(null, Typeface.BOLD)

                val value = MaterialColors.getColor(mItemQueueMusicBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                mItemQueueMusicBinding.textTitle.setTextColor(value)
                mItemQueueMusicBinding.textSubtitle.setTextColor(value)
                mItemQueueMusicBinding.textDetails.setTextColor(value)
                if(isPlaying){
                    mItemQueueMusicBinding.textNowPlaying.text = ctx.getString(R.string.playing)
                }else{
                    mItemQueueMusicBinding.textNowPlaying.text = ctx.getString(R.string.paused)
                }
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
        fun updateItemTouchHelper(isDragging : Boolean, animated: Boolean = true){
            if(isDragging && mItemQueueMusicBinding.songItemIsSelected.visibility != VISIBLE)
                ViewAnimatorsUtils.crossFadeUp(mItemQueueMusicBinding.songItemIsSelected, animated, 150)
            else if(!isDragging && mItemQueueMusicBinding.songItemIsSelected.alpha == 1.0f)
                ViewAnimatorsUtils.crossFadeDown(mItemQueueMusicBinding.songItemIsSelected, animated, 150)
        }
    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}
