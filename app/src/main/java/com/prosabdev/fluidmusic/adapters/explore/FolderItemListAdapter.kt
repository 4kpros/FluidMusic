package com.prosabdev.fluidmusic.adapters.explore

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.ItemGenericExploreGridBinding
import com.prosabdev.fluidmusic.models.view.FolderItem
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils

class FolderItemListAdapter(
    private val mContext: Context,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener
) :
    SelectableItemListAdapter<FolderItemListAdapter.FolderItemHolder>(FolderItem.diffCallback as DiffUtil.ItemCallback<Any>),
    FastScroller.SectionIndexer {

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
        fun onItemLongClicked(position: Int)
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
        selectableItemSelectFromPosition(position, mOnSelectSelectableItemListener, layoutManager)
    }
    fun selectableOnSelectRange(layoutManager : GridLayoutManager? = null) {
        selectableItemSelectRange(mOnSelectSelectableItemListener, layoutManager)
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
                (currentList[position] as FolderItem?)?.name ?:
                "#"
            else
                "#"
        return tempText.substring(0, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemHolder {
        val dataBinding: ItemGenericExploreGridBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_generic_explore_grid, parent, false
        )
        return FolderItemHolder(
            dataBinding,
            mOnItemClickListener
        )
    }
    override fun onBindViewHolder(holder: FolderItemHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
    override fun onBindViewHolder(holder: FolderItemHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_IS_SELECTED -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
                        holder.updateAnAnimateSelectedStateUI(selectableIsSelected(position))
                    }
                    PAYLOAD_IS_COVERT_ART_TEXT -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_COVERT_ART_TEXT")
                        holder.updateCovertArtAndTitleUI(mContext, getItem(position) as FolderItem)
                    }
                    else -> {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }
        } else {
            //If the is no payload specified on notify adapter, refresh all UI to be safe
            holder.updateSelectedStateUI(selectableIsSelected(position))
            holder.updateCovertArtAndTitleUI(mContext, getItem(position) as FolderItem)
        }
    }

    override fun onViewAttachedToWindow(holder: FolderItemHolder) {
        super.onViewAttachedToWindow(holder)
        holder.updateSelectedStateUI(selectableIsSelected(holder.bindingAdapterPosition))
    }

    class FolderItemHolder(
        private val mItemGenericExploreGridBinding: ItemGenericExploreGridBinding,
        mOnItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mItemGenericExploreGridBinding.root) {
        init {
            mItemGenericExploreGridBinding.cardViewClickable.setOnClickListener {
                mOnItemClickListener.onItemClicked(bindingAdapterPosition)
            }
            mItemGenericExploreGridBinding.cardViewClickable.setOnLongClickListener {
                mOnItemClickListener.onItemLongClicked(bindingAdapterPosition)
                true
            }
        }

        fun updateCovertArtAndTitleUI(ctx: Context, folderItem: FolderItem) {
            val tempTitle : String = "/${folderItem.name ?: ctx.getString(R.string.unknown_folder)}"
            val tempSubtitle : String = folderItem.parentFolder ?: folderItem.deviceName ?: "/"
            val tempDetails : String = "${folderItem.numberTracks} song(s) | ${FormattersAndParsersUtils.formatSongDurationToString(folderItem.totalDuration)} min"
            mItemGenericExploreGridBinding.textTitle.text = tempTitle
            mItemGenericExploreGridBinding.textSubtitle.visibility = View.GONE
            mItemGenericExploreGridBinding.textDetails.text = tempSubtitle

            val tempUri = Uri.parse(folderItem.uriImage ?: "")
            val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
            imageRequest.uri = tempUri
            imageRequest.imageView = mItemGenericExploreGridBinding.imageviewCoverArt
            imageRequest.hashedCovertArtSignature = folderItem.hashedCovertArtSignature
            ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
        }

        fun updateAnAnimateSelectedStateUI(selectableIsSelected: Boolean) {
            if(selectableIsSelected) {
                if (
                    mItemGenericExploreGridBinding.songItemIsSelected.visibility != View.VISIBLE
                ) {
                    mItemGenericExploreGridBinding.songItemIsSelected.clearAnimation()
                    AnimatorsUtils.crossFadeUp(
                        mItemGenericExploreGridBinding.songItemIsSelected,
                        true,
                        250,
                        0.125f
                    )
                }
            }
            else {
                AnimatorsUtils.crossFadeDown(
                    mItemGenericExploreGridBinding.songItemIsSelected,
                    true,
                    250
                )
            }
        }
        fun updateSelectedStateUI(selectableIsSelected: Boolean) {
            if(selectableIsSelected) {
                AnimatorsUtils.crossFadeUp(
                    mItemGenericExploreGridBinding.songItemIsSelected,
                    false,
                    0,
                    0.15f
                )
            }
            else {
                AnimatorsUtils.crossFadeDown(
                    mItemGenericExploreGridBinding.songItemIsSelected,
                    false,
                    0
                )
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}