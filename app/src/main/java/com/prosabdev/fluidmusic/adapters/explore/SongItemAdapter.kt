package com.prosabdev.fluidmusic.adapters.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.callbacks.SongItemMoveCallback
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.adapters.SelectablePlayingItemAdapter
import java.util.*


class SongItemAdapter(
    private val mSongList: List<SongItem>,
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnTouchListener: OnTouchListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
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
    fun isPlaying(position: Int): Boolean {
        return selectablePlayingIsPlaying(position)
    }
    fun getCurrentPlayingSong(): Int {
        return selectablePlayingGetCurrentPlayingSong()
    }
    fun setCurrentPlayingSong(position: Int) {
        selectablePlayingIsPlaying(position)
    }

    //Methods for selectable items
    fun selectableGetSelectionMode(): Boolean {
        return selectableItemGetSelectionMode()
    }
    fun selectableSetSelectionMode(value : Boolean) {
        return selectableItemSetSelectionMode(mOnSelectSelectableItemListener, value, itemCount)
    }
    fun selectableIsSelected(position: Int): Boolean {
        return selectableItemIsSelected(position)
    }
    fun selectableToggleSelection(position: Int) {
        selectableItemToggleSelection(mOnSelectSelectableItemListener, position, itemCount)
    }
    fun selectableUpdateSelection(position: Int, value : Boolean) {
        selectableItemUpdateSelection(position, value)
    }
    fun selectableGetSelectedItemCount(): Int {
        return selectableItemGetSelectedItemCount()
    }
    fun selectableGetSelectedItems(): List<Int?> {
        return selectableItemGetSelectedItems()
    }
    fun selectableSelectAll() {
        selectableItemSelectAll(mOnSelectSelectableItemListener, itemCount)
    }
    fun selectableClearSelection() {
        selectableItemClearSelection(mOnSelectSelectableItemListener, itemCount)
    }

    //Override methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)

        return SongItemHolder(view)
    }
    override fun onBindViewHolder(holder: SongItemHolder, position: Int) {
        //Bind listener for to capture click events
        holder.bindListener(holder, position, mOnItemClickListener, mOnTouchListener)

        //Update UI
        if(position < mSongList.size){
            holder.updateUI(mContext, mSongList[position], isPlaying(position), selectableIsSelected(position))
        }
    }
    override fun getItemCount(): Int {
        return mSongList.size
    }
    override fun onRowMoved(mFromPosition: Int, mToPosition: Int) {
        mOnItemClickListener.onItemMovedTo(mToPosition)
        if (mFromPosition < mToPosition) {
            for (i in mFromPosition until mToPosition) {
                selectableItemUpdateSelection(i, selectableIsSelected(i + 1))
                selectableItemUpdateSelection(i + 1, selectableIsSelected(i))
                Collections.swap(mSongList, i, i + 1)
            }
        } else {
            for (i in mFromPosition downTo mToPosition + 1) {
                selectableItemUpdateSelection(i, selectableIsSelected(i - 1))
                selectableItemUpdateSelection(i - 1, selectableIsSelected(i))
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
        private var mSelectedItemBackground: LinearLayoutCompat? = itemView.findViewById<LinearLayoutCompat>(R.id.song_item_is_selected)
        private var mDragHand: MaterialButton? = itemView.findViewById<MaterialButton>(R.id.button_drag_hand)
//        private var mIsPlayingBackground: LinearLayoutCompat? = itemView.findViewById<LinearLayoutCompat>(R.id.song_item_is_playing_background)

        //Update song item UI
        fun updateUI(context: Context, songItem: SongItem, isPlaying: Boolean, selected: Boolean){
            mTitle?.text = if(songItem.title != null && songItem.title!!.isNotEmpty()) songItem.title else songItem.fileName //Set song title
            mArtist?.text = if(songItem.artist!!.isNotEmpty()) songItem.artist else context.getString(
                            R.string.unknown_artist)
            mDuration?.text = CustomFormatters.formatSongDurationToString(songItem.duration) //Set song duration
            mTypeMime?.text = songItem.typeMime //Set song type mime
            //Set is is playing or is checked(for multiple item selection)
            if(selected) CustomAnimators.crossFadeUp(mSelectedItemBackground as View) else CustomAnimators.crossFadeDown(mSelectedItemBackground as View)
            //Set song covert art
            val tempBinaryData : ByteArray? = songItem.covertArt?.binaryData
            CustomUILoaders.loadCovertArtFromBinaryData(context, mCovertArt, tempBinaryData, 100)
        }

        fun updateItemTouchHelper(selected : Boolean){
            if(selected) CustomAnimators.crossScaleIn(mContainer as View, true) else CustomAnimators.crossScaleOut(mContainer as View, true)
        }

        //Method used to bind one listener with items events click
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