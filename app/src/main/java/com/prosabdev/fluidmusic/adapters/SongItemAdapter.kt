package com.prosabdev.fluidmusic.adapters


class SongItemAdapter(
//    private val mContext: Context,
//    private val mOnItemClickListener: OnItemClickListener,
//    private val mOnSelectSelectableItemListener: OnSelectSelectableItemListener,
//    private var mOrganizeListGrid: Int = ConstantValues.ORGANIZE_LIST_SMALL
    )
//    : SelectablePlayingItemListAdapter<SongItemAdapter.SongItemViewHolder>(SongItem.diffCallback as DiffUtil.ItemCallback<Any>),
//    FastScroller.SectionIndexer
{

//    interface OnItemClickListener {
//        fun onSongItemClicked(position: Int)
//        fun onSongItemLongClicked(position: Int)
//    }
//
//    //Methods for selectable playing
//    fun getIsPlaying(): Boolean {
//        return getSelectableIsPlaying()
//    }
//    fun setIsPlaying(isPlaying: Boolean) {
//        return setSelectableIsPlaying(isPlaying)
//    }
//    fun getPlayingPosition(): Int {
//        return getSelectablePlayingPosition()
//    }
//    fun setPlayingPosition(position: Int) {
//        setSelectablePlayingPosition(position)
//    }
//
//    //Methods for selectable items
//    fun selectableGetSelectionMode(): Boolean {
//        return selectableItemGetSelectionMode()
//    }
//    fun selectableSetSelectionMode(value : Boolean, layoutManager : GridLayoutManager) {
//        return selectableItemSetSelectionMode(value, layoutManager)
//    }
//    private fun selectableIsSelected(position: Int): Boolean {
//        return selectableItemIsSelected(position)
//    }
//    fun selectableOnSelectFromPosition(position: Int, layoutManager : GridLayoutManager? = null) {
//        selectableItemOnSelectFromPosition(position, mOnSelectSelectableItemListener, layoutManager)
//    }
//    fun selectableOnSelectRange(layoutManager : GridLayoutManager? = null) {
//        selectableItemOnSelectRange(mOnSelectSelectableItemListener, layoutManager)
//    }
//    fun selectableGetSelectedItemCount(): Int {
//        return selectableItemGetSelectedItemCount()
//    }
//    fun selectableSelectAll(layoutManager : GridLayoutManager? = null) {
//        selectableItemSelectAll(layoutManager)
//    }
//    fun selectableClearSelection(layoutManager : GridLayoutManager? = null) {
//        selectableItemClearAllSelection(layoutManager)
//    }
//
//    override fun getSectionText(position: Int): CharSequence {
//        val tempText: String =
//            if(position >= 0 && position < currentList.size)
//                    (currentList[position] as SongItem?)?.title ?:
//                    (currentList[position] as SongItem?)?.fileName ?:
//                    "#"
//            else
//                "#"
//        return tempText.substring(0, 1)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
//        val view : View = LayoutInflater.from(parent.context).inflate(
//            getLayoutResourceId(),
//            parent,
//            false
//        )
//        return SongItemViewHolder(
//            view,
//            mOnItemClickListener
//        )
//    }
//
//    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
//        onBindViewHolder(holder, position, mutableListOf())
//    }
//    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int, payloads: MutableList<Any>) {
//        if (payloads.isNotEmpty()) {
//            for (payload in payloads) {
//                when (payload) {
//                    PAYLOAD_IS_SELECTED -> {
//                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_SELECTED")
//                        holder.updateAndAnimateSelectedStateUI(selectableIsSelected(position))
//                    }
//                    PAYLOAD_PLAYBACK_STATE -> {
//                        Log.i(ConstantValues.TAG, "PAYLOAD_PLAYBACK_STATE")
//                        holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
//
//                    }
//                    PAYLOAD_IS_COVERT_ART_TEXT -> {
//                        Log.i(ConstantValues.TAG, "PAYLOAD_IS_COVERT_ART_TEXT")
//                        holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
//                    }
//                    else -> {
//                        super.onBindViewHolder(holder, position, payloads)
//                    }
//                }
//            }
//        } else {
//            //If the is no payload specified on notify adapter, refresh all UI to be safe
//            holder.updateSelectedStateUI(selectableIsSelected(position))
//            holder.updateIsPlayingStateUI(mContext, getIsPlaying(), getPlayingPosition())
//            holder.updateCovertArtAndTitleUI(mContext, getItem(position) as SongItem)
//        }
//    }
//
//    class SongItemViewHolder(
//        itemView : View,
//        mOnItemClickListener: OnItemClickListener
//    ) : RecyclerView.ViewHolder(itemView) {
//        init {
//            mItemGenericExploreListBinding.cardViewClickable.setOnClickListener {
//                mOnItemClickListener.onSongItemClicked(bindingAdapterPosition)
//            }
//            mItemGenericExploreListBinding.cardViewClickable.setOnLongClickListener {
//                mOnItemClickListener.onSongItemLongClicked(bindingAdapterPosition)
//                true
//            }
//        }
//        fun updateCovertArtAndTitleUI(ctx: Context, songItem: SongItem) {
//            var tempTitle : String = songItem.title ?: ""
//            var tempArtist : String = songItem.artist ?: ""
//            if(tempTitle.isEmpty()) tempTitle = songItem.fileName ?: ctx.getString(R.string.unknown_title)
//            if(tempArtist.isEmpty()) tempArtist = ctx.getString(R.string.unknown_artist)
//            mItemGenericExploreListBinding.textTitle.text = tempTitle
//            mItemGenericExploreListBinding.textSubtitle.text = tempArtist
//            mItemGenericExploreListBinding.textDetails.text =
//                ctx.getString(
//                    R.string.item_song_card_text_details,
//                    FormattersUtils.formatSongDurationToString(songItem.duration),
//                    songItem.fileExtension
//                )
//
//            val tempUri: Uri? = Uri.parse(songItem.uri ?: "")
//            val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
//            imageRequest.uri = tempUri
//            imageRequest.imageView = mItemGenericExploreListBinding.imageviewCoverArt
//            imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
//            ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)
//        }
//
//        fun updateIsPlayingStateUI(ctx : Context, isPlaying: Boolean, playingPosition : Int) {
//            if(playingPosition == bindingAdapterPosition){
//                mItemGenericExploreListBinding.textTitle.setTypeface(null, Typeface.BOLD)
//                mItemGenericExploreListBinding.textSubtitle.setTypeface(null, Typeface.BOLD)
//                mItemGenericExploreListBinding.textDetails.setTypeface(null, Typeface.BOLD)
//                mItemGenericExploreListBinding.textNowPlaying.setTypeface(null, Typeface.BOLD)
//
//                val value = MaterialColors.getColor(mItemGenericExploreListBinding.textTitle  as View, com.google.android.material.R.attr.colorPrimary)
//                mItemGenericExploreListBinding.textTitle.setTextColor(value)
//                mItemGenericExploreListBinding.textSubtitle.setTextColor(value)
//                mItemGenericExploreListBinding.textDetails.setTextColor(value)
//                if(isPlaying){
//                    mItemGenericExploreListBinding.textNowPlaying.text = ctx.getString(R.string.playing)
//                }else{
//                    mItemGenericExploreListBinding.textNowPlaying.text = ctx.getString(R.string.paused)
//                }
//                mItemGenericExploreListBinding.textNowPlaying.setTextColor(value)
//                mItemGenericExploreListBinding.textNowPlaying.visibility = VISIBLE
//            }else{
//                mItemGenericExploreListBinding.textTitle.setTypeface(null, Typeface.NORMAL)
//                mItemGenericExploreListBinding.textSubtitle.setTypeface(null, Typeface.NORMAL)
//                mItemGenericExploreListBinding.textDetails.setTypeface(null, Typeface.NORMAL)
//                mItemGenericExploreListBinding.textNowPlaying.setTypeface(null, Typeface.NORMAL)
//
//                val value = MaterialColors.getColor(mItemGenericExploreListBinding.textTitle as View, com.google.android.material.R.attr.colorOnBackground)
//                mItemGenericExploreListBinding.textTitle.setTextColor(value)
//                mItemGenericExploreListBinding.textSubtitle.setTextColor(value)
//                mItemGenericExploreListBinding.textDetails.setTextColor(value)
//                mItemGenericExploreListBinding.textNowPlaying.setTextColor(value)
//                mItemGenericExploreListBinding.textNowPlaying.visibility = INVISIBLE
//            }
//        }
//
//        fun updateOrganizeListGridUI(organizeListGrid: Int) {
//            if(organizeListGrid == ConstantValues.ORGANIZE_LIST_SMALL_NO_IMAGE) {
//                //
//            }
//        }
//        fun updateAndAnimateSelectedStateUI(selectableIsSelected: Boolean) {
//            if(selectableIsSelected) {
//                if (
//                    mItemGenericExploreListBinding.songItemIsSelected.visibility != VISIBLE
//                ) {
//                    mItemGenericExploreListBinding.songItemIsSelected.clearAnimation()
//                    ViewAnimatorsUtils.crossFadeUp(
//                        mItemGenericExploreListBinding.songItemIsSelected,
//                        true,
//                        250,
//                        0.125f
//                    )
//                }
//            }
//            else {
//                ViewAnimatorsUtils.crossFadeDown(
//                    mItemGenericExploreListBinding.songItemIsSelected,
//                    true,
//                    250
//                )
//            }
//        }
//        fun updateSelectedStateUI(selectableIsSelected: Boolean) {
//            if(selectableIsSelected) {
//                ViewAnimatorsUtils.crossFadeUp(
//                    mItemGenericExploreListBinding.songItemIsSelected,
//                    false,
//                    0,
//                    0.15f
//                )
//            }
//            else {
//                ViewAnimatorsUtils.crossFadeDown(
//                    mItemGenericExploreListBinding.songItemIsSelected,
//                    false,
//                    0
//                )
//            }
//        }
//    }

    companion object {
        const val PAYLOAD_IS_COVERT_ART_TEXT = "PAYLOAD_IS_COVERT_ART_TEXT"
    }
}