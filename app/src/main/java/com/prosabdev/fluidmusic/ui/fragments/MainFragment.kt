package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mFragmentMainBinding: FragmentMainBinding
    private lateinit var mContext: Context

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mContext = requireContext()
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
        mPlayerFragmentViewModel.getCurrentSong().observe(this.requireContext() as LifecycleOwner
        ) {
            MainScope().launch {
                updateMiniPlayerUI(it)
            }
        }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(this.requireContext() as LifecycleOwner
        ) {
            MainScope().launch {
                updateSliderUI(it)
            }
        }
        mMainFragmentViewModel.getSelectMode().observe(this.requireContext() as LifecycleOwner
        ) { selectMode ->
            MainScope().launch {
                updateSelectModeUI(selectMode ?: false)
            }
        }
        mMainFragmentViewModel.getTotalSelected().observe(this.requireContext() as LifecycleOwner
        ){
            MainScope().launch {
                updateTotalSelectedUI(it ?: 0)
            }
        }
        mMainFragmentViewModel.getScrollingState().observe(this.requireContext() as LifecycleOwner
        ){ animateScrollStateUI(it ?: 0) }
    }

    private fun updateSliderUI(it: Long?) {
        mFragmentMainBinding.constraintMiniPlayerInclude.progressMiniPlayerIndicator.progress = it?.toInt() ?: 0
    }

    private var mIsAnimatingScroll1: Boolean = false
    private var mIsAnimatingScroll2: Boolean = false
    private fun animateScrollStateUI(i: Int, animate : Boolean = true) {
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
    ) {
        val ctx : Context = this.context ?: return
        if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
            MainScope().launch {
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = ContextCompat.getDrawable(
                    ctx,
                    R.drawable.check_box
                )
            }

            CustomAnimators.crossFadeUp(mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover, false, 200, 0.8f)
        }else{
            MainScope().launch {
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = ContextCompat.getDrawable(
                    ctx,
                    R.drawable.check_box_outline_blank
                )
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
    private fun updatePlayerButtonsUI(playing : Boolean) {
        MainScope().launch {
            if(playing){
                mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = AppCompatResources.getDrawable(mContext, R.drawable.pause)
            }else{
                mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = AppCompatResources.getDrawable(mContext, R.drawable.play_arrow)
            }
        }
    }
    private var mOldPlayingSong : CurrentPlayingSongSP? = null
    private suspend fun updateMiniPlayerUI(currentSong : CurrentPlayingSongSP?) {
        withContext(Dispatchers.IO){
            val ctx : Context = this@MainFragment.context ?: return@withContext
            if(currentSong != null) {
                if (currentSong.uri != mOldPlayingSong?.uri) {
                    mOldPlayingSong = currentSong
                    val tempUri = Uri.parse(currentSong.uri)
                    MainScope().launch {

                        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                            if(currentSong.title != null )
                                currentSong.title
                            else
                                currentSong.fileName

                        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                            if(currentSong.artist != null )
                                currentSong.artist
                            else
                                ctx.getString(R.string.unknown_artist)
                    }
                    CustomUILoaders.loadCovertArtFromSongUri(
                        ctx,
                        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer,
                        tempUri,
                        60,
                        100
                    )
                    CustomUILoaders.loadBlurredCovertArtFromSongUri(
                        ctx,
                        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer,
                        tempUri,
                        25
                    )
                }
            }
        }
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
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<MusicLibraryFragment>(R.id.main_fragment_container)
        }
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }
    }

    private fun initViews() {
//        mPlayerFragmentViewModel = FragmentViewModelFactory().create(PlayerFragmentViewModel::class.java)
//        mMainFragmentViewModel = FragmentViewModelFactory().create(MainFragmentViewModel::class.java)

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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG -> {
//                val currentPlayingSongItem: CurrentPlayingSongItem? = SharedPreferenceManager.loadCurrentPlayingSong(this@PlayerFragment.requireContext(), sharedPreferences)
                MainScope().launch {
//                    val position : Int = currentPlayingSongItem?.position?.toInt() ?: 0
//                    updateTextTitleSubtitleDurationUI(position)
//                    updateTextCurrentDurationUI(0)
//                    updateSliderCurrentDurationUI(0)
//                    updateBlurredBackgroundUI(position)
                }
            }
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE -> {
//                loadSongsForQueueListSource(
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE
//                )
            }
//            ConstantValues.SHARED_PREFERENCES_REPEAT -> {
//                val repeatValue = SharedPreferenceManager.loadRepeat(this.requireContext(), sharedPreferences)
//                updateRepeatUI(repeatValue)
//            }
//            ConstantValues.SHARED_PREFERENCES_SHUFFLE -> {
//                val shuffleValue = SharedPreferenceManager.loadRepeat(this.requireContext(), sharedPreferences)
//                updateShuffleUI(shuffleValue)
//            }
        }
    }
}