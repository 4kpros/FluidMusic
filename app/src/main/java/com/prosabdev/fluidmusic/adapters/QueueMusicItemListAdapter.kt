package com.prosabdev.fluidmusic.adapters

import android.content.Context
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
import com.prosabdev.fluidmusic.adapters.generic.SelectablePlayingItemListAdapter
import com.prosabdev.fluidmusic.databinding.ItemQueueMusicBinding
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
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
                        holder.updateIsPlayingStateUI(getPlayingPosition(), true)
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
            holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
            holder.updateIsPlayingStateUI(getPlayingPosition(), true)
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
            mItemQueueMusicBinding.cardViewClickable.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(bindingAdapterPosition)
            }
            mItemQueueMusicBinding.buttonDrag.setOnTouchListener { view, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    mOnTouchListener.requestDrag(this)
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    view?.performClick()
                }
                false
            }
        }

        fun recycleItem(ctx : Context){
            Glide.with(ctx.applicationContext).clear(mItemQueueMusicBinding.imageviewCoverArt)
        }
        fun updateCovertArtAndTitleUI(ctx: Context, songItem: SongItem) {
            val tempTitle : String = songItem.title.ifEmpty { songItem.fileName.ifEmpty { ctx.getString(R.string.unknown_title) } }
            val tempArtist : String = songItem.artist.ifEmpty { ctx.getString(R.string.unknown_artist) }
            mItemQueueMusicBinding.textTitle.text = tempTitle
            mItemQueueMusicBinding.textSubtitle.text = tempArtist
            mItemQueueMusicBinding.textDetails.text =
                ctx.getString(
                    R.string.item_song_card_text_details,
                    FormattersAndParsersUtils.formatSongDurationToString(songItem.duration),
                    songItem.fileExtension
                )

            val tempUri: Uri? = Uri.parse(songItem.uri)
            val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
            imageRequest.uri = tempUri
            imageRequest.imageView = mItemQueueMusicBinding.imageviewCoverArt
            imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
            ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
        }
        fun updateIsPlayingStateUI(
            playingPosition: Int,
            animate: Boolean
        ) {
            if(playingPosition == bindingAdapterPosition){
                mItemQueueMusicBinding.imageviewBackgroundIsPlaying.visibility = GONE
                mItemQueueMusicBinding.linearIsPlayingAnimContainer.visibility = GONE
                val colorValue = MaterialColors.getColor(mItemQueueMusicBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                changeColorAndFaceType(colorValue, true)
                showPlayingPositionImageHover(animate)
            }else{
                mItemQueueMusicBinding.imageviewBackgroundIsPlaying.visibility = GONE
                mItemQueueMusicBinding.linearIsPlayingAnimContainer.visibility = GONE
                val colorValue = MaterialColors.getColor(mItemQueueMusicBinding.textTitle as View, com.google.android.material.R.attr.colorOnBackground)
                changeColorAndFaceType(colorValue, false)
                hidePlayingPositionImageHover(animate)
            }
        }
        private fun changeColorAndFaceType(textColorRes: Int, isUnderlined: Boolean){
            mItemQueueMusicBinding.textTitle.setTextColor(textColorRes)
            mItemQueueMusicBinding.textSubtitle.setTextColor(textColorRes)
            mItemQueueMusicBinding.textDetails.setTextColor(textColorRes)
            if(isUnderlined){
                mItemQueueMusicBinding.textTitle.text = FormattersAndParsersUtils.getUnderLinedWord(mItemQueueMusicBinding.textTitle.text.toString())
                mItemQueueMusicBinding.textSubtitle.text = FormattersAndParsersUtils.getUnderLinedWord(mItemQueueMusicBinding.textSubtitle.text.toString())
                mItemQueueMusicBinding.textDetails.text = FormattersAndParsersUtils.getUnderLinedWord(mItemQueueMusicBinding.textDetails.text.toString())
            }else{
                mItemQueueMusicBinding.textTitle.text = mItemQueueMusicBinding.textTitle.text.toString()
                mItemQueueMusicBinding.textSubtitle.text = mItemQueueMusicBinding.textSubtitle.text.toString()
                mItemQueueMusicBinding.textDetails.text = mItemQueueMusicBinding.textDetails.text.toString()
            }
        }
        private fun showPlayingPositionImageHover(animate: Boolean){
            if(animate){
                AnimatorsUtils.crossFadeUp(
                    mItemQueueMusicBinding.imageviewBackgroundIsPlaying,
                    true,
                    150,
                    0.65f
                )
                AnimatorsUtils.crossFadeUp(
                    mItemQueueMusicBinding.linearIsPlayingAnimContainer,
                    true,
                    200,
                    1.0f
                )
            }else{
                mItemQueueMusicBinding.imageviewBackgroundIsPlaying.visibility = VISIBLE
                mItemQueueMusicBinding.imageviewBackgroundIsPlaying.alpha = 0.65f
                mItemQueueMusicBinding.linearIsPlayingAnimContainer.visibility = VISIBLE
                mItemQueueMusicBinding.linearIsPlayingAnimContainer.alpha = 1.0f
            }
        }
        private fun hidePlayingPositionImageHover(animate: Boolean){
            if(animate){
                AnimatorsUtils.crossFadeDown(
                    mItemQueueMusicBinding.imageviewBackgroundIsPlaying,
                    true,
                    200
                )
                AnimatorsUtils.crossFadeDown(
                    mItemQueueMusicBinding.linearIsPlayingAnimContainer,
                    true,
                    150
                )
            }else{
                mItemQueueMusicBinding.imageviewBackgroundIsPlaying.alpha = 0.0f
                mItemQueueMusicBinding.linearIsPlayingAnimContainer.alpha = 0.0f
            }
        }
        fun updateItemTouchHelper(isDragging : Boolean, animated: Boolean = true){
//            if(isDragging) {
//                AnimatorsUtils.crossFadeUp(
//                    mItemQueueMusicBinding.songItemIsSelected,
//                    animated,
//                    150,
//                    0.15f
//                )
//            }
//            else {
//                AnimatorsUtils.crossFadeDown(
//                    mItemQueueMusicBinding.songItemIsSelected,
//                    animated,
//                    150
//                )
//            }
        }
    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}
