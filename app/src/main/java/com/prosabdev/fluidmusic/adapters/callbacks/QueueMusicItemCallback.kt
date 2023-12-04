package com.prosabdev.fluidmusic.adapters.callbacks

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.common.constants.MainConst
import com.prosabdev.fluidmusic.adapters.QueueMusicItemListAdapter

class QueueMusicItemCallback(adapter : QueueMusicItemListAdapter) : ItemTouchHelper.Callback() {

    private val mAdapter : QueueMusicItemListAdapter = adapter
    private var mFromPosition : Int = -1
    private var mToPosition : Int = -1
    private var mFromValue : Boolean = false
    private var mToValue : Boolean = false

    private var mDragStarted : Boolean = false

    interface ItemTouchHelperContract {
        fun onRowMoved(mFromPosition: Int, mToPosition: Int)
        fun onRowSelected(myViewHolder: QueueMusicItemListAdapter.QueueMusicItemHolder?)
        fun onRowClear(myViewHolder: QueueMusicItemListAdapter.QueueMusicItemHolder?)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }
    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if(!mDragStarted){
            mDragStarted = true
            mFromPosition = viewHolder.bindingAdapterPosition
            Log.i(MainConst.TAG, "From position : $mFromPosition = $mFromValue")
        }
        mToPosition = target.bindingAdapterPosition
        Log.i(MainConst.TAG, "To position : $mToPosition = $mToValue")

        mAdapter.onRowMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is QueueMusicItemListAdapter.QueueMusicItemHolder) {
                val myViewHolder: QueueMusicItemListAdapter.QueueMusicItemHolder =
                    viewHolder
                mAdapter.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is QueueMusicItemListAdapter.QueueMusicItemHolder) {
            val myViewHolder: QueueMusicItemListAdapter.QueueMusicItemHolder =
                viewHolder

            mDragStarted = false
            Log.i(MainConst.TAG, "Finally : from = $mFromPosition = $mToValue and to = $mToPosition = $mFromValue")
            mDragStarted = false
            if (mFromPosition < mToPosition) {
                mAdapter.notifyItemRangeChanged(mFromPosition, mToPosition+1)
            }else  if (mFromPosition > mToPosition) {
                mAdapter.notifyItemRangeChanged(mToPosition, mFromPosition+1)
            }
            mAdapter.onRowClear(myViewHolder)
        }
    }
}