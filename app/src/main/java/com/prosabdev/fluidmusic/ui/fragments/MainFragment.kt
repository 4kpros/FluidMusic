package com.prosabdev.fluidmusic.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var mContext: Context
    private var mActivity: FragmentActivity? = null

    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

    private var mTextTitleMiniPlayer: AppCompatTextView? = null
    private var mTextArtistMiniPlayer: AppCompatTextView? = null
    private var mProgressIndicatorMiniPlayer: LinearProgressIndicator? = null
    private var mCovertArtMiniPlayer: ImageView? = null
    private var mBlurredCovertArtMiniPlayer: ImageView? = null
    private var mButtonPlayPause: MaterialButton? = null
    private var mButtonSkipNext: MaterialButton? = null

    private var mSlidingUpPanel: SlidingUpPanelLayout? = null

    private var mMainFragmentContainer: FrameLayout? = null
    private var mPlayerFragmentContainer: FrameLayout? = null
    private var mMiniPlayerContainer: ConstraintLayout? = null
    private var mConstraintBottomSelectionContainer: ConstraintLayout? = null
    private var mConstraintTopSelectionContainer: ConstraintLayout? = null
    private var mConstraintBottomSelectionMenu: ConstraintLayout? = null
    private var mConstraintTopSelectionMenu: ConstraintLayout? = null

    private var mButtonSelectAll: MaterialButton? = null
    private var mButtonSelectRange: MaterialButton? = null
    private var mHoverMenuRange: View? = null
    private var mButtonCLoseSelectionMenu: MaterialButton? = null
    private var mTextSelectedCount: MaterialTextView? = null

    var mUpdateMiniPlayerUIJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MainExploreFragment>(R.id.main_fragment_container)
        }
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }

        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        initViews(view)
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    override fun onResume() {
        updateMiniPlayerUI(false)
        super.onResume()
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner
        ) { updateMiniPlayerUI() }
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
        ) { updateMiniPlayerUI() }
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner
        ) { updateMiniPlayerUI() }
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner
        ) { updatePlayerButtonsUI() }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(mActivity as LifecycleOwner
        ) { updatePlayerButtonsUI() }
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { selectMode -> updateSelectModeUI(selectMode ?: false) }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner
        ){ updateTotalSelectedUI(it ?: 0) }
    }
    private fun updateTotalSelectedUI(totalSelected: Int, animate : Boolean = true) {
        if (totalSelected >= 2 && mHoverMenuRange?.visibility != GONE)
            CustomAnimators.crossFadeDown(mHoverMenuRange!!, animate, 200)
        else if(totalSelected < 2 && mHoverMenuRange?.visibility != VISIBLE)
            CustomAnimators.crossFadeUp(mHoverMenuRange!!, animate, 200, 0.8f)

        if(totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            mButtonSelectAll?.icon = ContextCompat.getDrawable(mContext, R.drawable.check_box)

            if (totalSelected >= 2 && mHoverMenuRange?.visibility != GONE)
                CustomAnimators.crossFadeDown(mHoverMenuRange!!, animate, 200)
        }else{
            mButtonSelectAll?.icon = ContextCompat.getDrawable(mContext, R.drawable.check_box_outline_blank)
        }
        mTextSelectedCount?.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
    }
    private fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true){
        if (selectMode) {
            if(mConstraintBottomSelectionContainer?.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromVertical(mConstraintBottomSelectionContainer as View, 1, animate, 300)
            if(mConstraintTopSelectionContainer?.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromVertical(mConstraintTopSelectionContainer as View, -1, animate, 300)
        }else {
            if(mConstraintBottomSelectionContainer?.visibility != GONE)
                CustomAnimators.crossTranslateOutFromVertical(mConstraintBottomSelectionContainer as View, 1, animate, 300)
            if(mConstraintTopSelectionContainer?.visibility != GONE)
                CustomAnimators.crossTranslateOutFromVertical(mConstraintTopSelectionContainer as View, -1, animate, 300)
        }
    }
    private fun updatePlayerButtonsUI() {
        if(mPlayerFragmentViewModel.getIsPlaying().value == true){
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.pause)
        }else{
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.play_arrow)
        }
    }
    private fun updateMiniPlayerUI(animate : Boolean = true) {
        val tempQL : ArrayList<SongItem>? = mPlayerFragmentViewModel.getSongList().value
        val tempPositionInQL : Int = mPlayerFragmentViewModel.getCurrentSong().value ?: -1
        if(tempQL!= null && tempQL.size > 0 && tempPositionInQL >= 0){
            var tempTitle : String = ""
            var tempArtist : String = ""
            if(mTextTitleMiniPlayer != null)
                tempTitle = if(tempQL[tempPositionInQL].title != null && tempQL[tempPositionInQL].title!!.isNotEmpty()) tempQL[tempPositionInQL].title.toString() else tempQL[tempPositionInQL].fileName.toString() //Set song title

            if(mTextArtistMiniPlayer != null)
                tempArtist = if(tempQL[tempPositionInQL].artist != null && tempQL[tempPositionInQL].artist!!.isNotEmpty()) tempQL[tempPositionInQL].artist.toString() else mContext.getString(R.string.unknown_artist)

            val tempBinary : ByteArray? = if(tempQL.size > 0) tempQL[tempPositionInQL].covertArt?.binaryData else null

            if(animate){
                if(mUpdateMiniPlayerUIJob != null)
                    mUpdateMiniPlayerUIJob?.cancel()
                mUpdateMiniPlayerUIJob = MainScope().launch {
                    CustomAnimators.animateCrossFadeOutInTextView(mTextTitleMiniPlayer, tempTitle, 100)
                    CustomAnimators.animateCrossFadeOutInTextView(mTextArtistMiniPlayer, tempArtist, 100)
                    CustomAnimators.animateCrossFadeOutInImage(mContext, mCovertArtMiniPlayer, tempBinary, false, 100, 100)
                    CustomAnimators.animateCrossFadeOutInImage(mContext, mBlurredCovertArtMiniPlayer, tempBinary, true, 10, 100)
                }
            }else{
                CustomUILoaders.loadCovertArtFromBinaryData(mContext, mCovertArtMiniPlayer, tempBinary, 100)
                CustomUILoaders.loadBlurredWithImageLoader(mContext, mBlurredCovertArtMiniPlayer, tempBinary)
            }
        }
    }

    private fun checkInteractions() {
        mButtonSkipNext?.setOnClickListener{
            onNextPage()
        }
        mButtonPlayPause?.setOnClickListener{
            onPlayPause()
        }
        mButtonSelectAll?.setOnClickListener{
            onToggleSelectAll()
        }
        mButtonSelectRange?.setOnClickListener {
            onToggleSelectRange()
        }
        mButtonCLoseSelectionMenu?.setOnClickListener {
            onCloseSelectionMenu()
        }
        mSlidingUpPanel?.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                Log.i(ConstantValues.TAG, "Slide panel offset : $slideOffset")
                //Mini player visibility
//                if(1.0f - (slideOffset * 5.0f) >= 0.0f){
//                    mMiniPlayerContainer?.alpha = 1.0f - (slideOffset * 5.0f)
//                }else{
//                    mMiniPlayerContainer?.alpha = 0.0f
//                }
//                if(slideOffset < 1.0f){
//                    mMiniPlayerContainer?.visibility = VISIBLE
//                }else{
//                    mMiniPlayerContainer?.visibility = GONE
//                }

                //Player visibility
                if(slideOffset <= 0.21f){
//                    mPlayerFragmentContainer?.alpha = 0.0f
                    mSlidingUpPanel?.setDragView(mMiniPlayerContainer)
                }else {
//                    mPlayerFragmentContainer?.alpha = (slideOffset * 1.21f) - 0.21f
                    mSlidingUpPanel?.setDragView(mPlayerFragmentContainer)
                }
//                if (slideOffset <= 0.15f){
//                    mPlayerFragmentContainer?.visibility = GONE
//                }else{
//                    mPlayerFragmentContainer?.visibility = VISIBLE
//                }
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: PanelState?,
                newState: PanelState
            ) {
                Log.i(ConstantValues.TAG, "onPanelStateChanged $newState")
            }
        })
    }
    private fun onCloseSelectionMenu() {
        mMainFragmentViewModel.setSelectMode(false)
    }
    private fun onToggleSelectRange() {
        mMainFragmentViewModel.setToggleRange()
    }
    private fun onToggleSelectAll() {
        if(((mMainFragmentViewModel.getTotalSelected().value ?: 0) >= (mMainFragmentViewModel.getTotalCount().value ?: 0))) {
            mMainFragmentViewModel.setTotalSelected(0)
        }else {
            mMainFragmentViewModel.setTotalSelected(mMainFragmentViewModel.getTotalCount().value ?: 0)
        }
    }
    fun onPlayPause(){
        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    fun onNextPage(){
        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
        val tempSongListSize :Int = mPlayerFragmentViewModel.getSongList().value?.size ?: 0
        if(tempSongListSize > 0 && tempCS < tempSongListSize - 1)
            mPlayerFragmentViewModel.setCurrentSong(tempCS + 1)
    }

    private fun initViews(view: View) {

        mSlidingUpPanel = view.findViewById(R.id.sliding_up_panel)
        mMainFragmentContainer = view.findViewById(R.id.main_fragment_container)
        mPlayerFragmentContainer = view.findViewById(R.id.player_fragment_container)
        mMiniPlayerContainer = view.findViewById(R.id.constraint_mini_player)
        mConstraintBottomSelectionContainer = view.findViewById<ConstraintLayout>(R.id.constraint_bottom_selection_container)
        mConstraintTopSelectionContainer = view.findViewById<ConstraintLayout>(R.id.constraint_top_selection_container)
        mConstraintBottomSelectionMenu = view.findViewById<ConstraintLayout>(R.id.constraint_bottom_selection_menu)
        mConstraintTopSelectionMenu = view.findViewById<ConstraintLayout>(R.id.constraint_top_selection_menu)

        mButtonSelectAll = view.findViewById<MaterialButton>(R.id.button_select_all)
        mButtonSelectRange = view.findViewById<MaterialButton>(R.id.button_select_range)
        mHoverMenuRange = view.findViewById<View>(R.id.constraint_range_menu_hover)
        mButtonCLoseSelectionMenu = view.findViewById<MaterialButton>(R.id.button_close)

        mTextSelectedCount = view.findViewById<MaterialTextView>(R.id.text_selected_count)

        mTextTitleMiniPlayer = view.findViewById(R.id.text_mini_player_title)
        mTextArtistMiniPlayer = view.findViewById(R.id.text_mini_player_artist)
        mProgressIndicatorMiniPlayer = view.findViewById(R.id.progress_mini_player_indicator)
        mCovertArtMiniPlayer = view.findViewById(R.id.imageview_mini_player)
        mBlurredCovertArtMiniPlayer = view.findViewById(R.id.imageview_blurred_mini_player)

        mButtonPlayPause = view.findViewById(R.id.button_play_pause)
        mButtonSkipNext = view.findViewById(R.id.button_skip_next)

        mCovertArtMiniPlayer?.layout(0,0,0,0)
        mBlurredCovertArtMiniPlayer?.layout(0,0,0,0)
        mTextTitleMiniPlayer?.isSelected = true
        mTextArtistMiniPlayer?.isSelected = true

        CustomViewModifiers.updateTopViewInsets(mMainFragmentContainer as View)
        CustomViewModifiers.updateBottomViewInsets(mMiniPlayerContainer as View)
        CustomViewModifiers.updateBottomViewInsets(mConstraintBottomSelectionMenu as View)
        CustomViewModifiers.updateTopViewInsets(mConstraintTopSelectionMenu as View)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}