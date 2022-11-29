package com.prosabdev.fluidmusic.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.ui.fragments.settings.SettingsFragment
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout.GONE
import com.sothree.slidinguppanel.SlidingUpPanelLayout.VISIBLE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MainFragment : Fragment() {

    private lateinit var mFragmentMainBinding: FragmentMainBinding

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mMusicLibraryFragment = MusicLibraryFragment.newInstance()
    private val mFoldersHierarchyFragment = FoldersHierarchyFragment.newInstance()
    private val mPlaylistsFragment = PlaylistsFragment.newInstance()
    private val mStreamsFragment = StreamsFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        arguments?.let {
        }
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

        checkInteractions()
        observeLiveData()
    }
    private fun updateDrawerMenuUI(showCounter: Int) {
        when (activity?.supportFragmentManager?.findFragmentById(R.id.main_fragment_container)) {
            is MusicLibraryFragment -> {
                mFragmentMainBinding.navigationView.setCheckedItem(R.id.music_library)
            }
            is FoldersHierarchyFragment -> {
                mFragmentMainBinding.navigationView.setCheckedItem(R.id.folders_hierarchy)
            }
            is PlaylistsFragment -> {
                mFragmentMainBinding.navigationView.setCheckedItem(R.id.playlists)
            }
            is StreamsFragment -> {
                mFragmentMainBinding.navigationView.setCheckedItem(R.id.streams)
            }
        }
    }

    private fun observeLiveData() {
        //Observe live data from player fragment
        mPlayerFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner){
            updateMiniPlayerUI(it)
        }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(viewLifecycleOwner){
            updateMiniPlayerSliderUI(it)
        }
        mPlayerFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner){
            updateMiniPlayerPlayPauseStateUI(it)
        }

        //Observe live data from main fragment
        mMainFragmentViewModel.getShowSlidingPanelCounter().observe(viewLifecycleOwner){
            updateSlidingUpPanelStateUI(it, true)
        }
        mMainFragmentViewModel.getHideSlidingPanelCounter().observe(viewLifecycleOwner){
            updateSlidingUpPanelStateUI(it, false)
        }
        mMainFragmentViewModel.getSelectMode().observe(viewLifecycleOwner){
            updateSelectModeUI(it)
        }
        mMainFragmentViewModel.getTotalSelected().observe(viewLifecycleOwner){
            updateTotalSelectedTracksUI(it)
        }
        mMainFragmentViewModel.getScrollingState().observe(viewLifecycleOwner){
            updateMiniPlayerScrollingStateUI(it)
        }
        mMainFragmentViewModel.getShowDrawerMenuCounter().observe(viewLifecycleOwner){
            openDrawerMenuUI(it)
        }
    }

    private fun openDrawerMenuUI(showCounter: Int?) {
        if((showCounter ?: 0) <= 0) return
        if(!mFragmentMainBinding.drawerLayout.isOpen)
            mFragmentMainBinding.drawerLayout.open()
    }
    private var mIsAnimatingScroll1: Boolean = false
    private var mIsAnimatingScroll2: Boolean = false
    private fun updateMiniPlayerScrollingStateUI(scrollState: Int, animate: Boolean = true) {
        MainScope().launch {
            if(scrollState >= 1){
                if(mIsAnimatingScroll2){
                    mFragmentMainBinding.constraintMiniPlayerContainer.apply {
                        mIsAnimatingScroll2 = false
                        clearAnimation()
                    }
                }
                if(mIsAnimatingScroll1)
                    return@launch
                mIsAnimatingScroll1 = true
                ViewAnimatorsUtils.crossTranslateOutFromVertical(
                    mFragmentMainBinding.constraintMiniPlayerContainer,
                    1,
                    animate,
                    150,
                    300.0f
                )
            }else{
                if(mIsAnimatingScroll1){
                    mFragmentMainBinding.constraintMiniPlayerContainer.apply {
                        mIsAnimatingScroll1 = false
                        clearAnimation()
                    }
                }
                if(mIsAnimatingScroll2)
                    return@launch
                mIsAnimatingScroll2 = true
                ViewAnimatorsUtils.crossTranslateInFromVertical(
                    mFragmentMainBinding.constraintMiniPlayerContainer,
                    1, animate,
                    150,
                    300.0f
                )
            }
        }
    }
    private fun updateTotalSelectedTracksUI(totalSelected: Int, animate: Boolean = true) {
        MainScope().launch {
            if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.check_box
                    )
                }
                ViewAnimatorsUtils.crossFadeUp(
                    mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover,
                    false,
                    200,
                    0.8f
                )
            }else{
                mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.check_box_outline_blank
                    )
                }
                if (totalSelected >= 2 && mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover.visibility != GONE)
                    ViewAnimatorsUtils.crossFadeDown(
                        mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover,
                        animate,
                        200
                    )
                else if(totalSelected < 2 && mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover.visibility != VISIBLE)
                    ViewAnimatorsUtils.crossFadeUp(
                        mFragmentMainBinding.constraintBottomSelectionInclude.constraintRangeMenuHover,
                        animate,
                        200,
                        0.8f
                    )
            }
            mFragmentMainBinding.constraintTopSelectionInclude.textSelectedCount.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
        }
    }
    private fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true) {
        MainScope().launch {
            if (selectMode) {
                if(mFragmentMainBinding.constraintBottomSelectionContainer.visibility != VISIBLE)
                    ViewAnimatorsUtils.crossTranslateInFromVertical(
                        mFragmentMainBinding.constraintBottomSelectionContainer as View,
                        1,
                        animate,
                        300
                    )
                if(mFragmentMainBinding.constraintTopSelectionContainer.visibility != VISIBLE)
                    ViewAnimatorsUtils.crossTranslateInFromVertical(
                        mFragmentMainBinding.constraintTopSelectionContainer as View,
                        -1,
                        animate,
                        300
                    )
            }else {
                if(mFragmentMainBinding.constraintBottomSelectionContainer.visibility != GONE)
                    ViewAnimatorsUtils.crossTranslateOutFromVertical(
                        mFragmentMainBinding.constraintBottomSelectionContainer as View,
                        1,
                        animate,
                        300
                    )
                if(mFragmentMainBinding.constraintTopSelectionContainer.visibility != GONE)
                    ViewAnimatorsUtils.crossTranslateOutFromVertical(
                        mFragmentMainBinding.constraintTopSelectionContainer as View,
                        -1,
                        animate,
                        300
                    )
            }
        }
    }
    private fun updateSlidingUpPanelStateUI(counter: Int?, showPanel: Boolean) {
        if(showPanel){
            showSlidingUpPanel(counter)
        }else{
            hideSlidingUpPanel(counter)
        }
    }
    private fun hideSlidingUpPanel(counter: Int?) {
        if(counter == null || counter <= 0)
            return
        MainScope().launch {
            if(mFragmentMainBinding.slidingUpPanel.panelState != PanelState.COLLAPSED)
                mFragmentMainBinding.slidingUpPanel.panelState = PanelState.COLLAPSED
        }
    }
    private fun showSlidingUpPanel(counter: Int?) {
        if(counter == null || counter <= 0)
            return
        MainScope().launch {
            if(mFragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
                mFragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
        }
    }

    private fun updateMiniPlayerPlayPauseStateUI(isPlaying: Boolean?) {
        MainScope().launch {
            context?.let {
                if(isPlaying == true){
                    mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = AppCompatResources.getDrawable(it, R.drawable.pause)
                }else{
                    mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon = AppCompatResources.getDrawable(it, R.drawable.play_arrow)
                }
            }

        }
    }
    private fun updateMiniPlayerSliderUI(currentDuration: Long) {
        MainScope().launch {
            val totalDuration : Long = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
            mFragmentMainBinding.constraintMiniPlayerInclude.progressMiniPlayerIndicator.progress =
                (FormattersUtils.formatSongDurationToSliderProgress(currentDuration, totalDuration)).toInt()
        }
    }
    private fun updateMiniPlayerUI(songItem : SongItem?) {
        MainScope().launch {
            if(songItem == null){
                mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                    context?.getString(R.string.unknown_title)
                mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                    context?.getString(R.string.unknown_artist)
                context?.let {
                    ImageLoadersUtils.loadWithResourceID(
                        it,
                        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer,
                        0,
                        0
                    )
                    Glide.with(it).clear(
                        mFragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer
                    )
                }
                return@launch
            }
            mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                if(songItem.title != null )
                    songItem.title
                else
                    songItem.fileName

            mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                if(songItem.artist != null )
                    songItem.artist
                else
                    context?.getString(R.string.unknown_artist)

            val tempUri = Uri.parse(songItem.uri)
            context?.let {
                ImageLoadersUtils.loadCovertArtFromSongUri(
                    it,
                    mFragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer,
                    tempUri,
                    100,
                    100
                )
                ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                    it,
                    mFragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer,
                    tempUri,
                    25
                )
            }
        }
    }

    private fun checkInteractions() {
        mFragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.setOnClickListener{
            onClickButtonPlayPause()
        }
        mFragmentMainBinding.constraintMiniPlayerInclude.buttonSkipNext.setOnClickListener{
            onClickButtonSkipNextSong()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.setOnClickListener{
            onToggleButtonSelectAll()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.setOnClickListener {
            onToggleButtonSelectRange()
        }
        mFragmentMainBinding.constraintBottomSelectionInclude.buttonClose.setOnClickListener {
            onClickButtonCloseSelectionMenu()
        }
        mFragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer.setOnClickListener{
            onClickMiniPlayerContainer()
        }
        mFragmentMainBinding.slidingUpPanel.addPanelSlideListener(object : com.sothree.slidinguppanel.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                Log.i(ConstantValues.TAG, "On panel slide offset : $slideOffset")
                if(slideOffset <= 0.21f){
                    mFragmentMainBinding.slidingUpPanel.setDragView(mFragmentMainBinding.constraintMiniPlayerContainer)
                }else {
                    mFragmentMainBinding.slidingUpPanel.setDragView(mFragmentMainBinding.mainFragmentContainer)
                }
            }
            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                Log.i(ConstantValues.TAG, "On panel state changed $newState")
            }
        })
        mFragmentMainBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if(mFragmentMainBinding.navigationView.checkedItem?.itemId != menuItem.itemId){
                when (menuItem.itemId) {
                    R.id.music_library -> {
                        showMusicLibraryFragment()
                        mFragmentMainBinding.drawerLayout.close()
                    }
                    R.id.folders_hierarchy -> {
                        showFolderHierarchyFragment()
                        mFragmentMainBinding.drawerLayout.close()
                    }
                    R.id.playlists -> {
                        showPlaylistsFragment()
                        mFragmentMainBinding.drawerLayout.close()
                    }
                    R.id.streams -> {
                        showStreamsFragment()
                        mFragmentMainBinding.drawerLayout.close()
                    }
                }
            }
            when (menuItem.itemId) {
                R.id.settings -> {
                    activity?.supportFragmentManager?.commit {
                        setReorderingAllowed(true)
                        add(R.id.main_activity_fragment_container, SettingsFragment.newInstance())
                        addToBackStack(null)
                    }
                }
            }
            true
        }
    }
    private fun onClickMiniPlayerContainer() {
        if(mFragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
            mFragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
    }

    private fun onClickButtonCloseSelectionMenu() {
        mMainFragmentViewModel.setSelectMode(false)
    }

    private fun onToggleButtonSelectRange() {
        mMainFragmentViewModel.setToggleRange()
    }

    private fun onToggleButtonSelectAll() {
        val tempTotalSelected: Int = mMainFragmentViewModel.getTotalSelected().value ?: 0
        val tempTotalCount : Int = mMainFragmentViewModel.getTotalCount().value ?: 0
        if(tempTotalSelected >= tempTotalCount) {
            mMainFragmentViewModel.setTotalSelected(0)
        }else {
            mMainFragmentViewModel.setTotalSelected(tempTotalCount)
        }
    }
    private fun onClickButtonSkipNextSong() {
        mPlayerFragmentViewModel.setSkipNextTrackCounter()
    }
    private fun onClickButtonPlayPause() {
        mPlayerFragmentViewModel.toggleIsPlaying()
    }

    private fun setupFragments() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add(R.id.main_fragment_container, mMusicLibraryFragment)
            add(R.id.main_fragment_container, mFoldersHierarchyFragment)
            add(R.id.main_fragment_container, mPlaylistsFragment)
            add(R.id.main_fragment_container, mStreamsFragment)
        }

        showMusicLibraryFragment()
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            replace(R.id.player_fragment_container, PlayerFragment.newInstance())
        }
    }
    private fun showMusicLibraryFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            hide(mFoldersHierarchyFragment)
            hide(mPlaylistsFragment)
            hide(mStreamsFragment)
            show(mMusicLibraryFragment)
        }
    }
    private fun showFolderHierarchyFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            hide(mMusicLibraryFragment)
            show(mFoldersHierarchyFragment)
            hide(mPlaylistsFragment)
            hide(mStreamsFragment)
        }
    }
    private fun showPlaylistsFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            hide(mMusicLibraryFragment)
            hide(mFoldersHierarchyFragment)
            show(mPlaylistsFragment)
            hide(mStreamsFragment)
        }
    }
    private fun showStreamsFragment() {
        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            hide(mMusicLibraryFragment)
            hide(mFoldersHierarchyFragment)
            hide(mPlaylistsFragment)
            show(mStreamsFragment)
        }
    }

    private fun initViews() {
        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.isSelected = true
        mFragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.isSelected = true

        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentMainBinding.mainFragmentContainer)
        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentMainBinding.constraintTopSelectionInclude.constraintTopSelectionMenu)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentMainBinding.constraintBottomSelectionInclude.constraintBottomSelectionMenu)

        mFragmentMainBinding.navigationView.setCheckedItem(R.id.music_library)
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