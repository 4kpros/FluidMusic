package com.prosabdev.fluidmusic.adapters.generic

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemGenericListGridBinding
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GenericListGridItemAdapter (
    private val mContext: Context,
    private val mOnItemRequestDataInfo: OnItemRequestDataInfo,
    private val mOnItemClickListener: OnItemClickListener,
    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
    diffCallback: DiffUtil.ItemCallback<Any>,
    private var mOrganizeListGrid: Int = ConstantValues.ORGANIZE_GRID_MEDIUM,
    private var mIsSelectable: Boolean = true,
    private var mHavePlaybackState: Boolean = false,
) : SelectablePlayingItemListAdapter<GenericListGridItemAdapter.GenericListGridItemHolder>(diffCallback),
    FastScroller.SectionIndexer
{

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
        fun onItemLongPressed(position: Int)
    }
    interface OnItemRequestDataInfo {
        fun onRequestDataInfo(dataItem: Any, position: Int): GenericItemListGrid?
        fun onRequestTextIndexForFastScroller(dataItem: Any, position: Int): String?
    }

    //Methods for organize list grid
    fun getOrganizeListGrid(): Int {
        return mOrganizeListGrid
    }
    fun setOrganizeListGrid(organizeListGrid: Int) {
        mOrganizeListGrid = organizeListGrid
//        notifyItemRangeChanged(0, itemCount, PAYLOAD_IS_ORGANIZE_LIST_GRID)
        //Not good but we have chance now
        notifyDataSetChanged()
    }

    //Methods for selectable playing
    fun getIsPlaying(): Boolean {
        if(!mHavePlaybackState) return false
        return getSelectableIsPlaying()
    }
    fun setIsPlaying(isPlaying: Boolean) {
        if(!mHavePlaybackState) return
        return setSelectableIsPlaying(isPlaying)
    }
    fun getPlayingPosition(): Int {
        if(!mHavePlaybackState) return -1
        return getSelectablePlayingPosition()
    }
    fun setPlayingPosition(position: Int) {
        if(!mHavePlaybackState) return
        setSelectablePlayingPosition(position)
    }

    //Methods for selectable items
    fun selectableGetSelectionMode(): Boolean {
        if(!mIsSelectable) return false
        return selectableItemGetSelectionMode()
    }
    fun selectableSetSelectionMode(value : Boolean, layoutManager : GridLayoutManager) {
        if(!mIsSelectable) return
        return selectableItemSetSelectionMode(value, layoutManager)
    }
    private fun selectableIsSelected(position: Int): Boolean {
        if(!mIsSelectable) return false
        return selectableItemIsSelected(position)
    }
    fun selectableSelectFromPosition(position: Int, layoutManager : GridLayoutManager? = null) {
        if(!mIsSelectable) return
        selectableItemSelectFromPosition(position, mOnSelectSelectableItemListener, layoutManager)
    }
    fun selectableSelectRange(layoutManager : GridLayoutManager? = null) {
        if(!mIsSelectable) return
        selectableItemSelectRange(mOnSelectSelectableItemListener, layoutManager)
    }
    fun selectableGetSelectedItemCount(): Int {
        if(!mIsSelectable) return -1
        return selectableItemGetSelectedItemCount()
    }
    fun selectableSelectAll(layoutManager : GridLayoutManager? = null) {
        if(!mIsSelectable) return
        selectableItemSelectAll(layoutManager)
    }
    fun selectableClearSelection(layoutManager : GridLayoutManager? = null) {
        if(!mIsSelectable) return
        selectableItemClearAllSelection(layoutManager)
    }

    //Fast scroll text section
    override fun getSectionText(position: Int): CharSequence {
        val tempPosition = if(position >= currentList.size) currentList.size-1 else position
        if(tempPosition < 0) return "#"
        val tempString: String = mOnItemRequestDataInfo.onRequestTextIndexForFastScroller(getItem(tempPosition), tempPosition) ?: "#"
        return if(tempString.isEmpty()) "3" else tempString.substring(0, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericListGridItemHolder {
        val mDataBinding: ItemGenericListGridBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_generic_list_grid, parent, false
        )
        Log.i(ConstantValues.TAG, "ON RECREADTE ITEM VIEW HOLDER")
        return GenericListGridItemHolder(
            mDataBinding,
            mOnItemRequestDataInfo,
            mOnItemClickListener
        )
    }


    override fun onBindViewHolder(holder: GenericListGridItemHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
    override fun onBindViewHolder(holder: GenericListGridItemHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            for (payload in payloads) {
                when (payload) {
                    PAYLOAD_IS_COVERT_ART_TEXT -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_COVERT_ART_TEXT")
                        holder.updateTextAndImageViewUI(mContext, getItem(position), mOrganizeListGrid)
                    }
                    PAYLOAD_IS_ORGANIZE_LIST_GRID -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_ORGANIZE_LIST_GRID")
                        holder.updateOrganizeListGridMotion(mContext, mOrganizeListGrid)
                    }
                    PAYLOAD_IS_SELECTED -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
                        holder.updateWithAnimationSelectedStateUI(selectableIsSelected(position), mIsSelectable)
                    }
                    PAYLOAD_PLAYBACK_STATE -> {
                        Log.i(ConstantValues.TAG, "PAYLOAD_PLAYBACK_STATE")
                        holder.updateIsPlayingStateUI(mContext, getPlayingPosition(), getIsPlaying(), mHavePlaybackState, mOrganizeListGrid, true)
                    }
                    else -> {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
            }
        } else {
            //If the is no payload specified on notify adapter, refresh all UI to be safe
            holder.updateTextAndImageViewUI(mContext, getItem(position), mOrganizeListGrid)
            holder.updateOrganizeListGridMotion(mContext, mOrganizeListGrid)
            holder.updateSelectedStateUI(selectableIsSelected(position), mIsSelectable)
            holder.updateIsPlayingStateUI(
                mContext,
                getPlayingPosition(),
                getIsPlaying(),
                mHavePlaybackState,
                mOrganizeListGrid,
                false
            )
        }
    }

    class GenericListGridItemHolder(
        private val mDataBinding: ItemGenericListGridBinding,
        private val mOnItemRequestDataInfo: OnItemRequestDataInfo,
        mOnItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(mDataBinding.root) {
        init {
            mDataBinding.cardViewClickable.setOnClickListener {
                mOnItemClickListener.onItemClicked(bindingAdapterPosition)
            }
            mDataBinding.cardViewClickable.setOnLongClickListener {
                mOnItemClickListener.onItemLongPressed(bindingAdapterPosition)
                true
            }
        }
        fun updateOrganizeListGridMotion(ctx: Context, organizeListGrid: Int) {
            val marginLarge = ctx.resources.getDimensionPixelSize(R.dimen.margin_large_size)
            val marginMedium = ctx.resources.getDimensionPixelSize(R.dimen.margin_medium_size)
            val widthForListSize = FormattersAndParsersUtils.getSpecificWidthSizeForListType(ctx, organizeListGrid)

            val layoutParamsLinearImageviewContainer : LayoutParams = mDataBinding.linearImageviewContainer.layoutParams as LayoutParams
            val layoutParamsLinearTextContainer : LayoutParams = mDataBinding.linearTextContainer.layoutParams as LayoutParams
            val layoutParamsLinearCardViewClickable : LayoutParams = mDataBinding.cardViewClickable.layoutParams as LayoutParams
            if(
                organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_MEDIUM_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_LARGE_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_GRID_SMALL_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_GRID_MEDIUM_NO_IMAGE ||

                organizeListGrid == ConstantValues.ORGANIZE_LIST_EXTRA_SMALL ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_MEDIUM ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_LARGE
            ){
                if(
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_EXTRA_SMALL ||
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL ||
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_MEDIUM ||
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_LARGE
                ){
                    //Add left margin
                    layoutParamsLinearImageviewContainer.setMargins(marginLarge, 0, 0, 0)
                    //Setup margins for linear text container
                    layoutParamsLinearTextContainer.setMargins(marginMedium, 0, marginLarge, 0)
                    //Resize imageview to specific size
                    mDataBinding.linearImageviewContainer.layoutParams.width = widthForListSize
                    //Show image view
                    mDataBinding.linearImageviewContainer.visibility = View.VISIBLE
                    //Align clickable view to cover all view
                    layoutParamsLinearCardViewClickable.removeRule(RelativeLayout.ALIGN_BOTTOM)
                    layoutParamsLinearCardViewClickable.addRule(RelativeLayout.ALIGN_BOTTOM, mDataBinding.linearImageviewContainer.id)
                }else {
                    //Setup margins for linear text container
                    layoutParamsLinearTextContainer.setMargins(marginLarge, 0, marginLarge, 0)
                    //Hide image view
                    mDataBinding.linearImageviewContainer.visibility = View.GONE
                    //Align clickable view to cover all view
                    layoutParamsLinearCardViewClickable.removeRule(RelativeLayout.ALIGN_BOTTOM)
                    layoutParamsLinearCardViewClickable.addRule(RelativeLayout.ALIGN_BOTTOM, mDataBinding.linearTextContainer.id)
                }
                //Align linear text container on top of imageview cover
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_TOP)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_BOTTOM)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.END_OF)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.BELOW)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.ALIGN_TOP, mDataBinding.linearImageviewContainer.id)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.ALIGN_BOTTOM, mDataBinding.linearImageviewContainer.id)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.END_OF, mDataBinding.linearImageviewContainer.id)
            }else{
                //Remove all margins for linear image view container
                layoutParamsLinearImageviewContainer.setMargins(0, 0, 0, 0)
                //Setup margins for linear text container
                layoutParamsLinearTextContainer.setMargins(marginLarge, 0, marginLarge, 0)
                //Resize imageview to match parent
                mDataBinding.linearImageviewContainer.layoutParams.width = MATCH_PARENT
                //Show image view
                mDataBinding.linearImageviewContainer.visibility = View.VISIBLE
                //Align linear text container on bottom of imageview cover
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_TOP)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_BOTTOM)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.END_OF)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.BELOW)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParamsLinearTextContainer.removeRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.BELOW, mDataBinding.linearImageviewContainer.id)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                layoutParamsLinearTextContainer.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                //Align clickable view to cover all view
                layoutParamsLinearCardViewClickable.removeRule(RelativeLayout.ALIGN_BOTTOM)
                layoutParamsLinearCardViewClickable.addRule(RelativeLayout.ALIGN_BOTTOM, mDataBinding.linearTextContainer.id)
            }
            mDataBinding.linearImageviewContainer.layoutParams = layoutParamsLinearImageviewContainer
            mDataBinding.linearTextContainer.layoutParams = layoutParamsLinearTextContainer
            mDataBinding.cardViewClickable.layoutParams = layoutParamsLinearCardViewClickable

            when (organizeListGrid) {
                ConstantValues.ORGANIZE_LIST_SMALL_NO_IMAGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.GONE
                }
                ConstantValues.ORGANIZE_LIST_MEDIUM_NO_IMAGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_LIST_LARGE_NO_IMAGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_GRID_SMALL_NO_IMAGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_GRID_MEDIUM_NO_IMAGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                //
                ConstantValues.ORGANIZE_LIST_EXTRA_SMALL -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_LIST_SMALL -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_LIST_MEDIUM -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_LIST_LARGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                //
                ConstantValues.ORGANIZE_GRID_EXTRA_SMALL -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.GONE
                }
                ConstantValues.ORGANIZE_GRID_SMALL -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_GRID_MEDIUM -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_GRID_LARGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.GONE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
                ConstantValues.ORGANIZE_GRID_EXTRA_LARGE -> {
                    //Update text size and font
                    //Show or hide non necessary text view
                    mDataBinding.textTitle.visibility = View.VISIBLE
                    mDataBinding.textSubtitle.visibility = View.VISIBLE
                    mDataBinding.textDetails.visibility = View.VISIBLE
                }
            }
        }

        fun updateTextAndImageViewUI(ctx: Context, dataItem: Any, organizeListGrid: Int) {
            val genericData : GenericItemListGrid =
                mOnItemRequestDataInfo.onRequestDataInfo(dataItem, bindingAdapterPosition)
                    ?: return

            mDataBinding.textTitle.text = genericData.title
            mDataBinding.textSubtitle.text = genericData.subtitle
            mDataBinding.textDetails.text = genericData.details
            updateCovertArtUI(ctx, genericData.imageUri, genericData.imageHashedSignature, organizeListGrid)
        }
        private fun updateCovertArtUI(ctx: Context, uri: Uri?, imageSignature: Int, organizeListGrid: Int) {
            CoroutineScope(Dispatchers.Default).launch {
                if(
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL_NO_IMAGE ||
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_MEDIUM_NO_IMAGE ||
                    organizeListGrid == ConstantValues.ORGANIZE_LIST_LARGE_NO_IMAGE ||
                    organizeListGrid == ConstantValues.ORGANIZE_GRID_SMALL_NO_IMAGE ||
                    organizeListGrid == ConstantValues.ORGANIZE_GRID_MEDIUM_NO_IMAGE
                ){
                    ImageLoadersUtils.clearImageView(ctx, mDataBinding.imageviewCoverArt)
                    return@launch
                }
                val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
                imageRequest.uri = uri
                imageRequest.imageView = mDataBinding.imageviewCoverArt
                imageRequest.hashedCovertArtSignature = imageSignature
                ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
            }
        }

        fun updateIsPlayingStateUI(
            ctx: Context,
            playingPosition: Int,
            isPlaying: Boolean,
            havePlaybackState: Boolean,
            organizeListGrid: Int,
            animate: Boolean
        ) {
            if(!havePlaybackState){
                mDataBinding.imageviewBackgroundIsPlaying.visibility = View.GONE
                mDataBinding.linearIsPlayingAnimContainer.visibility = View.GONE
                val colorValue = MaterialColors.getColor(mDataBinding.textTitle  as View, com.google.android.material.R.attr.colorOnBackground)
                changeColorAndFaceType(Typeface.NORMAL, colorValue, false)
                return
            }
            if(playingPosition == bindingAdapterPosition){
                val colorValue = MaterialColors.getColor(mDataBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
                changeColorAndFaceType(Typeface.BOLD, colorValue, true)
            }else{
                val colorValue = MaterialColors.getColor(mDataBinding.textTitle as View, com.google.android.material.R.attr.colorOnBackground)
                changeColorAndFaceType(Typeface.NORMAL, colorValue, false)
            }

            if(
                organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_MEDIUM_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_LIST_LARGE_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_GRID_SMALL_NO_IMAGE ||
                organizeListGrid == ConstantValues.ORGANIZE_GRID_MEDIUM_NO_IMAGE
            ){
                mDataBinding.imageviewBackgroundIsPlaying.visibility = View.GONE
                mDataBinding.linearIsPlayingAnimContainer.visibility = View.GONE
            }else{
                if(playingPosition == bindingAdapterPosition){
                    AnimatorsUtils.crossFadeUp(
                        mDataBinding.imageviewBackgroundIsPlaying,
                        animate,
                        150,
                        0.65f
                    )
                    AnimatorsUtils.crossFadeUp(
                        mDataBinding.linearIsPlayingAnimContainer,
                        animate,
                        200,
                        1.0f
                    )
                }else{
                    AnimatorsUtils.crossFadeDown(
                        mDataBinding.imageviewBackgroundIsPlaying,
                        animate,
                        200
                    )
                    AnimatorsUtils.crossFadeDown(
                        mDataBinding.linearIsPlayingAnimContainer,
                        animate,
                        150
                    )
                }
            }
        }
        private fun changeColorAndFaceType(typeface: Int, textColorRes: Int, isUnderlined: Boolean){
            mDataBinding.textTitle.setTypeface(null, typeface)
            mDataBinding.textSubtitle.setTypeface(null, typeface)
            mDataBinding.textDetails.setTypeface(null, typeface)

            mDataBinding.textTitle.setTextColor(textColorRes)
            mDataBinding.textSubtitle.setTextColor(textColorRes)
            mDataBinding.textDetails.setTextColor(textColorRes)

            updateUnderlinedPlaying(isUnderlined)
        }
        private fun updateUnderlinedPlaying(isUnderlined: Boolean){
            if(isUnderlined){
                mDataBinding.textTitle.text = FormattersAndParsersUtils.getUnderLinedWord(mDataBinding.textTitle.text.toString())
                mDataBinding.textSubtitle.text = FormattersAndParsersUtils.getUnderLinedWord(mDataBinding.textSubtitle.text.toString())
                mDataBinding.textDetails.text = FormattersAndParsersUtils.getUnderLinedWord(mDataBinding.textDetails.text.toString())
            }else{
                mDataBinding.textTitle.text = mDataBinding.textTitle.text.toString()
                mDataBinding.textSubtitle.text = mDataBinding.textSubtitle.text.toString()
                mDataBinding.textDetails.text = mDataBinding.textDetails.text.toString()
            }
        }

        fun updateWithAnimationSelectedStateUI(selectableIsSelected: Boolean, isSelectable: Boolean) {
            if(!isSelectable){
                hideDirectlySelectedItemBackground()
                return
            }
            startAnimationForSelectedItemBackground(selectableIsSelected, 200)
        }
        fun updateSelectedStateUI(selectableIsSelected: Boolean, isSelectable: Boolean) {
            if(!isSelectable){
                hideDirectlySelectedItemBackground()
                return
            }
            startAnimationForSelectedItemBackground(selectableIsSelected, 0)
        }
        private fun hideDirectlySelectedItemBackground() {
            mDataBinding.backgroundSelected.visibility = View.GONE
            mDataBinding.backgroundSelected.alpha = 0.0f
        }
        private fun startAnimationForSelectedItemBackground(selectableIsSelected: Boolean, duration: Int) {
            if(selectableIsSelected) {
                AnimatorsUtils.crossFadeUp(
                    mDataBinding.backgroundSelected,
                    true,
                    duration,
                    0.125f
                )
            }
            else {
                AnimatorsUtils.crossFadeDown(
                    mDataBinding.backgroundSelected,
                    true,
                    duration
                )
            }
        }
    }

    companion object {
        const val PAYLOAD_IS_ORGANIZE_LIST_GRID = "PAYLOAD_IS_ORGANIZE_LIST_GRID"
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}
