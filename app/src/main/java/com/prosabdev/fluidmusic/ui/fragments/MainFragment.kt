package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModelFactory
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var mFragmentMainBinding: FragmentMainBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel
    private lateinit var mMainFragmentViewModel: MainFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentMainBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false)
        val view = mFragmentMainBinding.root

        initViews()
        setupFragments()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            checkInteractions()
            observeLiveData()
        }
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
        ) {
            MainScope().launch {
                updateMiniPlayerUI()
            }
        }
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner
        ) {
            MainScope().launch {
                updateMiniPlayerUI()
            }
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner
        ) {
            MainScope().launch {
                updatePlayerButtonsUI()
            }
        }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(mActivity as LifecycleOwner
        ) {
            MainScope().launch {
                updatePlayerButtonsUI()
            }
        }
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { selectMode ->
            MainScope().launch {
                updateSelectModeUI(selectMode ?: false)
            }
        }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner
        ){
            MainScope().launch {
                updateTotalSelectedUI(it ?: 0)
            }
        }
        mMainFragmentViewModel.getScrollingState().observe(mActivity as LifecycleOwner
        ){ animateScrollStateUI(it ?: 0) }
    }
    private var mIsAnimatingScroll1: Boolean = false
    private var mIsAnimatingScroll2: Boolean = false
    private fun animateScrollStateUI(i: Int, animate : Boolean = true) {
        Log.i(ConstantValues.TAG, "IS SCROLLING : ${i}")
        if(i >= 1){
            if(mIsAnimatingScroll2){
                mFragmentMainBinding.constraintMiniPlayerContainer.apply {
                    mIsAnimatingScroll2 = false
                    clearAnimation()
                }
            }
            if(mIsAnimatingScroll1)
                return
            mIsAnimatingScroll1 = true
            CustomAnimators.crossTranslateOutFromVertical(mFragmentMainBinding.constraintMiniPlayerContainer,  1, animate, 150,300.0f)
        }else{
            if(mIsAnimatingScroll1){
                mFragmentMainBinding.constraintMiniPlayerContainer.apply {
                    mIsAnimatingScroll1 = false
                    clearAnimation()
                }
            }
            if(mIsAnimatingScroll2)
                return
            mIsAnimatingScroll2 = true
            CustomAnimators.crossTranslateInFromVertical(mFragmentMainBinding.constraintMiniPlayerContainer,  1, animate, 150,300.0f)
        }
    }
    private fun updateTotalSelectedUI(
        totalSelected: Int,
        animate : Boolean = true
    ) = lifecycleScope.launch(context = Dispatchers.Default) {
        if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            MainScope().launch {
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = ContextCompat.getDrawable(mContext, R.drawable.check_box)
            }

            CustomAnimators.crossFadeUp(mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover, false, 200, 0.8f)
        }else{
            MainScope().launch {
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = ContextCompat.getDrawable(mContext, R.drawable.check_box_outline_blank)
            }

            if (totalSelected >= 2 && mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover.visibility != GONE)
                CustomAnimators.crossFadeDown(mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover, animate, 200)
            else if(totalSelected < 2 && mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover.visibility != VISIBLE)
                CustomAnimators.crossFadeUp(mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover, animate, 200, 0.8f)
        }
        MainScope().launch {
            mFragmentMainBinding.constraintTopSelectionInclude.textSelectedCount.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
        }
    }
    private fun updateSelectModeUI(
        selectMode : Boolean,
        animate : Boolean = true
    ) = lifecycleScope.launch(context = Dispatchers.Default) {
        if (selectMode) {
            if(mFragmentMainBinding.constraintBottomSelectionContainer.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromVertical(mFragmentMainBinding.constraintBottomSelectionContainer as View, 1, animate, 300)
            if(mFragmentMainBinding.constraintTopSelectionContainer.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromVertical(mFragmentMainBinding.constraintTopSelectionContainer as View, -1, animate, 300)
        }else {
            if(mFragmentMainBinding.constraintBottomSelectionContainer.visibility != GONE)
                CustomAnimators.crossTranslateOutFromVertical(mFragmentMainBinding.constraintBottomSelectionContainer as View, 1, animate, 300)
            if(mFragmentMainBinding.constraintTopSelectionContainer.visibility != GONE)
                CustomAnimators.crossTranslateOutFromVertical(mFragmentMainBinding.constraintTopSelectionContainer as View, -1, animate, 300)
        }
    }
    private fun updatePlayerButtonsUI() {
        MainScope().launch {
            if(mPlayerFragmentViewModel.getIsPlaying().value == true){
                mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = ContextCompat.getDrawable(mContext, R.drawable.pause)
            }else{
                mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = ContextCompat.getDrawable(mContext, R.drawable.play_arrow)
            }
        }
    }
    private suspend fun updateMiniPlayerUI(animate : Boolean = true) = lifecycleScope.launch(context = Dispatchers.Default) {
//        val tempQL : ArrayList<SongItem>? = null
//        val tempPositionInQL : Int = mPlayerFragmentViewModel.getCurrentSong().value ?: -1
//        if(tempQL!= null && tempQL.size > 0 && tempPositionInQL >= 0){
//            var tempTitle = ""
//            var tempArtist = ""
//            tempTitle = if(tempQL[tempPositionInQL].title != null && tempQL[tempPositionInQL].title!!.isNotEmpty()) tempQL[tempPositionInQL].title.toString() else tempQL[tempPositionInQL].fileName.toString() //Set song title
//
//            tempArtist = if(tempQL[tempPositionInQL].artist != null && tempQL[tempPositionInQL].artist!!.isNotEmpty()) tempQL[tempPositionInQL].artist.toString() else mContext.getString(R.string.unknown_artist)
//
//            val tempBinary : ByteArray? = if(tempQL.size > 0) tempQL[tempPositionInQL].covertArt?.binaryData else null
//
//            MainScope().launch {
//                mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text = tempTitle
//                mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text = tempArtist
//            }
//            CustomUILoaders.loadWithBinaryDataWithCrossFade(mContext, mFragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer, tempBinary, 60)
//            CustomUILoaders.loadBlurredWithImageLoader(mContext, mFragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer, tempBinary, 10)
//        }
    }

    private suspend fun checkInteractions() {
        mFragmentMainBinding.constraintMiniPlayerInclude.buttonSkipNext.setOnClickListener{
            onNextPage()
        }
        mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.setOnClickListener{
            onPlayPause()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.setOnClickListener{
            onToggleSelectAll()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.setOnClickListener {
            onToggleSelectRange()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonClose.setOnClickListener {
            onCloseSelectionMenu()
        }
        mFragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer.setOnClickListener{
            if(mFragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
                mFragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
        }
//        mFragmentMainBinding.slidingUpPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
//            override fun onPanelSlide(panel: View?, slideOffset: Float) {
//                Log.i(ConstantValues.TAG, "On panel slide offset : $slideOffset")
//                if(slideOffset <= 0.21f){
//                    mFragmentMainBinding.slidingUpPanel.setDragView(mFragmentMainBinding.constraintMiniPlayerContainer)
//                }else {
//                    mFragmentMainBinding.slidingUpPanel.setDragView(mFragmentMainBinding.mainFragmentContainer)
//                }
//            }
//
//            override fun onPanelStateChanged(
//                panel: View?,
//                previousState: PanelState?,
//                newState: PanelState
//            ) {
//                Log.i(ConstantValues.TAG, "onPanelStateChanged $newState")
//            }
//        })
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
//        mMainFragmentViewModel.setScrollingState(-1)
//        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
//        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    fun onNextPage(){
//        mMainFragmentViewModel.setScrollingState(-1)
//        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
//        val tempSongListSize :Int = mPlayerFragmentViewModel.getSongList().value?.size ?: 0
//        if(tempSongListSize > 0 && tempCS < tempSongListSize - 1)
//            mPlayerFragmentViewModel.setCurrentSong(tempCS + 1)
    }


    private fun setupFragments() {
        mActivity.supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MusicLibraryFragment>(R.id.main_fragment_container)
        }
        mActivity.supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }
    }

    private fun initViews() {
        mPlayerFragmentViewModel = PlayerFragmentViewModelFactory().create(PlayerFragmentViewModel::class.java)
        mMainFragmentViewModel = MainFragmentViewModelFactory().create(MainFragmentViewModel::class.java)

        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer.layout(0,0,0,0)
        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer.layout(0,0,0,0)
        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.isSelected = true
        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.isSelected = true

        CustomViewModifiers.updateTopViewInsets(mFragmentMainBinding.mainFragmentContainer)
        CustomViewModifiers.updateBottomViewInsets(mFragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer)
        CustomViewModifiers.updateBottomViewInsets(mFragmentMainBinding.constraintBottomSelectionInclude.constraintBottomSelectionMenu)
        CustomViewModifiers.updateTopViewInsets(mFragmentMainBinding.constraintTopSelectionInclude.constraintTopSelectionMenu)
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