package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.QueueMusicItemAdapter
import com.prosabdev.fluidmusic.adapters.callbacks.QueueMusicItemCallback
import com.prosabdev.fluidmusic.adapters.explore.SongItemAdapter
import com.prosabdev.fluidmusic.databinding.DialogQueueMusicBinding
import com.prosabdev.fluidmusic.databinding.FragmentGenresBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.adapters.SelectableItemListAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class QueueMusicDialog : BottomSheetDialogFragment() {

    private lateinit var mDialogQueueMusicBinding: DialogQueueMusicBinding

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mQueueMusicItemAdapter: QueueMusicItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        mActivity = requireActivity()

        mDialogQueueMusicBinding = DataBindingUtil.inflate(inflater,R.layout.dialog_queue_music, container,false)
        val view = mDialogQueueMusicBinding.root

        initViews()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        MainScope().launch {
            setupRecyclerViewAdapter()
            checkInteractions()
            observeLiveData()
        }
    }

    private fun observeLiveData() {
    }

    private fun updateCurrentPlayingSong(currentSong: Int?) {
    }

    private fun setupRecyclerViewAdapter() {
        val spanCount = 1
        var touchHelper : ItemTouchHelper? = null

        mQueueMusicItemAdapter = QueueMusicItemAdapter(
            mContext!!,
            object : QueueMusicItemAdapter.OnItemClickListener{
                override fun onSongItemClicked(position: Int) {
                    if(mQueueMusicItemAdapter?.selectableGetSelectionMode() == true){
                        mQueueMusicItemAdapter?.selectableToggleSelection(position, mLayoutManager)
//                        mMainFragmentViewModel.setTotalSelected(mQueueMusicItemAdapter?.selectableGetSelectedItemCount() ?: 0)
                    }else{
                        onPlayButton(position)
                    }
                }
                override fun onSongItemPlayClicked(position: Int) {
                    updateCurrentPlayingSong(position)
                }
                override fun onSongItemLongClicked(position: Int) {
                    onLongPressedToItemSong(position)
                }
                override fun onItemMovedTo(position: Int) {
                    scrollDrag(position)
                }

            },
            object : SelectableItemListAdapter.OnSelectSelectableItemListener {
                override fun onTotalSelectedItemChange(totalSelected: Int) {
//                    mMainFragmentViewModel.setTotalSelected(totalSelected)
                }
            },
            object : QueueMusicItemAdapter.OnTouchListener{
                override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
                    if (viewHolder != null) {
                        touchHelper?.startDrag(viewHolder)
                    }
                }

            }
        )

        mDialogQueueMusicBinding.queueMusicRecyclerView.adapter = mQueueMusicItemAdapter
        mLayoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mDialogQueueMusicBinding.queueMusicRecyclerView.layoutManager = mLayoutManager

        //Setup Item touch helper callback for drag feature
        val callback : ItemTouchHelper.Callback = QueueMusicItemCallback(mQueueMusicItemAdapter!!)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mDialogQueueMusicBinding.queueMusicRecyclerView)
    }
    fun scrollDrag(position: Int) {
        Log.i(ConstantValues.TAG, "scrollDrag To $position")
    }
    private fun onLongPressedToItemSong(position: Int) {

    }
    private fun onPlayButton(position: Int) {
    }

    private fun checkInteractions() {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun initViews() {

    }
    companion object {
        const val TAG = "PlayerQueueMusicDialog"
    }
}