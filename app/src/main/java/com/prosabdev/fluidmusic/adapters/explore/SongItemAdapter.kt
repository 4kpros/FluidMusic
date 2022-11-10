package com.prosabdev.fluidmusic.adapters.explore

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.adapters.SelectablePlayingItemAdapter
import com.prosabdev.fluidmusic.utils.adapters.SelectableRecycleViewAdapter
import java.util.*


class SongItemAdapter(
    private val mSongList: List<SongItem>,
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: SelectableRecycleViewAdapter.OnSelectSelectableItemListener,
    private val mOnTouchListener: OnTouchListener,
    ) : SelectablePlayingItemAdapter<SongItemAdapter.SongItemHolder>(),
        SongItemMoveCallback.ItemTouchHelperContract
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
    fun selectableSetSelectionMode(value : Boolean, notifyListener: Boolean = true) {
        return selectableItemSetSelectionMode(mOnSelectSelectableItemListener, value, notifyListener)
    }
    fun selectableIsSelected(position: Int): Boolean {
        return selectableItemIsSelected(position)
    }
    fun selectableToggleSelection(position: Int) {
        selectableItemToggleSelection(mOnSelectSelectableItemListener, position)
    }
    fun selectableUpdateSelection(position: Int, value : Boolean) {
        selectableItemUpdateSelection(position, value, mOnSelectSelectableItemListener)
    }
    fun selectableSelectRange(startRange: Int, endRange: Int) {
        selectableItemSelectRange(startRange, endRange, mOnSelectSelectableItemListener)
    }
    fun selectableClearRange(startRange: Int, endRange: Int) {
        selectableItemClearRange(startRange, endRange, mOnSelectSelectableItemListener)
    }
    fun selectableGetSelectedItemCount(): Int {
        return selectableItemGetSelectedItemCount()
    }
    fun selectableGetSelectedItems(): List<Int?> {
        return selectableItemGetSelectedItems()
    }
    fun selectableSelectAll() {
        selectableItemSelectAll(mOnSelectSelectableItemListener)
    }
    fun selectableClearSelection(notifyListener: Boolean = true) {
        selectableItemClearAllSelection(mOnSelectSelectableItemListener, notifyListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)

        return SongItemHolder(view)
    }
    override fun onBindViewHolder(holder: SongItemHolder, position: Int) {
        holder.bindListener(holder, position, mOnItemClickListener, mOnTouchListener)
        holder.updateUI(mContext, mSongList[position], isPlaying(position), selectableIsSelected(position))
    }

    override fun onViewRecycled(holder: SongItemHolder) {
        super.onViewRecycled(holder)
        holder.recycleItem()
    }
    override fun getItemCount(): Int {
        return mSongList.size
    }
    override fun onRowMoved(mFromPosition: Int, mToPosition: Int) {
        mOnItemClickListener.onItemMovedTo(mToPosition)
        if (mFromPosition < mToPosition) {
            for (i in mFromPosition until mToPosition) {
                selectableItemUpdateSelection(i, selectableIsSelected(i + 1), mOnSelectSelectableItemListener)
                selectableItemUpdateSelection(i + 1, selectableIsSelected(i), mOnSelectSelectableItemListener)
                Collections.swap(mSongList, i, i + 1)
            }
        } else {
            for (i in mFromPosition downTo mToPosition + 1) {
                selectableItemUpdateSelection(i, selectableIsSelected(i - 1), mOnSelectSelectableItemListener)
                selectableItemUpdateSelection(i - 1, selectableIsSelected(i), mOnSelectSelectableItemListener)
                Collections.swap(mSongList, i, i - 1)
            }
        }
        notifyItemMoved(mFromPosition, mToPosition)
    }
    override fun onRowSelected(myViewHolder: SongItemHolder?) {
        myViewHolder?.updateItemTouchHelper(true)
    }
    override fun onRowClear(myViewHolder: SongItemHolder?) {
        myViewHolder?.updateItemTouchHelper(false)
    }

    class SongItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mContainer: MaterialCardView? = itemView.findViewById<MaterialCardView>(R.id.song_item_container)
        private var mCovertArt: ImageView? = itemView.findViewById<ImageView>(R.id.song_item_imageview)
        private var mTitle: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_title)
        private var mArtist: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_artist)
        private var mDuration: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_duration)
        private var mTypeMime: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_item_type_mime)
        private var mVerticalSeparator: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.vertical_separator)
        private var mCurrentlyPlaying: AppCompatTextView? = itemView.findViewById<AppCompatTextView>(R.id.song_currently_playing)

        private var mSelectedItemBackground: View? = itemView.findViewById<View>(R.id.song_item_is_selected)

        private var mDragHand: MaterialButton? = itemView.findViewById<MaterialButton>(R.id.button_drag_hand)

        fun updateUI(context: Context, songItem: SongItem, isPlaying: Boolean, selected: Boolean){
            mCovertArt?.layout(0,0,0,0)
            mTitle?.text = if(songItem.title != null && songItem.title!!.isNotEmpty()) songItem.title else songItem.fileName
            mArtist?.text = if(songItem.artist!!.isNotEmpty()) songItem.artist else context.getString(
                            R.string.unknown_artist)
            mDuration?.text = CustomFormatters.formatSongDurationToString(songItem.duration)
            mTypeMime?.text = songItem.typeMime

            if(selected)
                mSelectedItemBackground?.visibility = VISIBLE
            else
                mSelectedItemBackground?.visibility = INVISIBLE
            if(isPlaying){
                mTitle?.setTypeface(null, Typeface.BOLD)
                mArtist?.setTypeface(null, Typeface.BOLD)
                mDuration?.setTypeface(null, Typeface.BOLD)
                mTypeMime?.setTypeface(null, Typeface.BOLD)
                mVerticalSeparator?.setTypeface(null, Typeface.BOLD)
                mCurrentlyPlaying?.setTypeface(null, Typeface.BOLD)

                val value = MaterialColors.getColor(mTitle as View, com.google.android.material.R.attr.colorPrimary)
                mTitle?.setTextColor(value)
                mArtist?.setTextColor(value)
                mDuration?.setTextColor(value)
                mTypeMime?.setTextColor(value)
                mVerticalSeparator?.setTextColor(value)
                mCurrentlyPlaying?.setTextColor(value)
                mCurrentlyPlaying?.visibility = VISIBLE
            }else{
                mTitle?.setTypeface(null, Typeface.NORMAL)
                mArtist?.setTypeface(null, Typeface.NORMAL)
                mDuration?.setTypeface(null, Typeface.NORMAL)
                mTypeMime?.setTypeface(null, Typeface.NORMAL)
                mVerticalSeparator?.setTypeface(null, Typeface.NORMAL)
                mCurrentlyPlaying?.setTypeface(null, Typeface.NORMAL)

                val value = MaterialColors.getColor(mTitle as View, com.google.android.material.R.attr.colorOnBackground)
                mTitle?.setTextColor(value)
                mArtist?.setTextColor(value)
                mDuration?.setTextColor(value)
                mTypeMime?.setTextColor(value)
                mVerticalSeparator?.setTextColor(value)
                mCurrentlyPlaying?.setTextColor(value)
                mCurrentlyPlaying?.visibility = INVISIBLE
            }

            val tempBinary: ByteArray? = songItem.covertArt?.binaryData
            CustomUILoaders.loadCovertArtFromBinaryData(context, mCovertArt, tempBinary, 100)
        }

        fun recycleItem(){
            mCovertArt?.setImageDrawable(null)
        }

        fun updateItemTouchHelper(selected : Boolean){
//            if(selected) CustomAnimators.crossScaleIn(mContainer as View, true) else CustomAnimators.crossScaleOut(mContainer as View, true)
        }

        fun bindListener(
            holder: SongItemHolder,
            position: Int,
            mOnItemClickListener: OnItemClickListener,
            mOnTouchListener: OnTouchListener
        ) {
            mContainer?.setOnClickListener {
                mOnItemClickListener.onSongItemClicked(position)
            }
            mContainer?.setOnLongClickListener {
                mOnItemClickListener.onSongItemLongClicked(position)
                true
            }
            mCovertArt?.setOnClickListener {
                mOnItemClickListener.onSongItemPlayClicked(position)
            }
            mDragHand?.setOnTouchListener(object : android.view.View.OnTouchListener{
                override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        mOnTouchListener.requestDrag(holder)
                    }else if(event?.action == MotionEvent.ACTION_UP){
                        view?.performClick();
                    }
                    return false
                }
            })
        }
    }
}