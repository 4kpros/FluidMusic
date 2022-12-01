package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
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
import com.prosabdev.fluidmusic.ui.activities.SettingsActivity
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout.GONE
import com.sothree.slidinguppanel.SlidingUpPanelLayout.VISIBLE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MainFragment : Fragment() {

    private var mFragmentMainBinding: FragmentMainBinding? = null

    private val  mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val  mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

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
    ): View? {
        mFragmentMainBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false)
        val view = mFragmentMainBinding?.root

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
        mFragmentMainBinding?.let { fragmentMainBinding ->
            when (activity?.supportFragmentManager?.findFragmentById(R.id.main_fragment_container)) {
                is MusicLibraryFragment -> {
                    fragmentMainBinding.navigationView.setCheckedItem(R.id.music_library)
                }
                is FoldersHierarchyFragment -> {
                    fragmentMainBinding.navigationView.setCheckedItem(R.id.folders_hierarchy)
                }
                is PlaylistsFragment -> {
                    fragmentMainBinding.navigationView.setCheckedItem(R.id.playlists)
                }
                is StreamsFragment -> {
                    fragmentMainBinding.navigationView.setCheckedItem(R.id.streams)
                }
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
        mFragmentMainBinding?.let { fragmentMainBinding ->
            if ((showCounter ?: 0) <= 0) return
            if (!fragmentMainBinding.drawerLayout.isOpen)
                fragmentMainBinding.drawerLayout.open()
        }
    }
    private var mIsAnimatingScroll1: Boolean = false
    private var mIsAnimatingScroll2: Boolean = false
    private fun updateMiniPlayerScrollingStateUI(scrollState: Int, animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if (scrollState >= 1) {
                    if(fragmentMainBinding.constraintMiniPlayerContainer.visibility == GONE) return@launch

                    if (mIsAnimatingScroll2) {
                        fragmentMainBinding.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll2 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll1)
                        return@launch
                    mIsAnimatingScroll1 = true
                    ViewAnimatorsUtils.crossTranslateOutFromVertical(
                        fragmentMainBinding.constraintMiniPlayerContainer,
                        1,
                        animate,
                        150,
                        300.0f
                    )
                } else {
                    if(fragmentMainBinding.constraintMiniPlayerContainer.translationY == 0.0f) return@launch
                    if (mIsAnimatingScroll1) {
                        fragmentMainBinding.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll1 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll2)
                        return@launch
                    mIsAnimatingScroll2 = true
                    ViewAnimatorsUtils.crossTranslateInFromVertical(
                        fragmentMainBinding.constraintMiniPlayerContainer,
                        1, animate,
                        150,
                        300.0f
                    )
                }
            }
        }
    }
    private fun updateTotalSelectedTracksUI(totalSelected: Int, animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
                    fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.check_box
                        )
                    }
                    if(fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.isClickable)
                        ViewAnimatorsUtils.crossFadeDownClickable(
                            fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange,
                            animate,
                            200
                        )
                }else{
                    fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.icon = context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.check_box_outline_blank
                        )
                    }
                    if (totalSelected >= 2 && !fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.isClickable)
                        ViewAnimatorsUtils.crossFadeUpClickable(
                            fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange,
                            animate,
                            200,
                            1.0f
                        )
                    else if(totalSelected < 2 && fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.isClickable)
                        ViewAnimatorsUtils.crossFadeDownClickable(
                            fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange,
                            animate,
                            200
                        )
                }
                fragmentMainBinding.constraintTopSelectionInclude.textSelectedCount.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
            }
        }
    }
    private fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if (selectMode) {
                    if (fragmentMainBinding.constraintBottomSelectionContainer.visibility != VISIBLE)
                        ViewAnimatorsUtils.crossTranslateInFromVertical(
                            fragmentMainBinding.constraintBottomSelectionContainer as View,
                            1,
                            animate,
                            300
                        )
                    if (fragmentMainBinding.constraintTopSelectionContainer.visibility != VISIBLE)
                        ViewAnimatorsUtils.crossTranslateInFromVertical(
                            fragmentMainBinding.constraintTopSelectionContainer as View,
                            -1,
                            animate,
                            300
                        )
                } else {
                    if (fragmentMainBinding.constraintBottomSelectionContainer.visibility != GONE)
                        ViewAnimatorsUtils.crossTranslateOutFromVertical(
                            fragmentMainBinding.constraintBottomSelectionContainer as View,
                            1,
                            animate,
                            300
                        )
                    if (fragmentMainBinding.constraintTopSelectionContainer.visibility != GONE)
                        ViewAnimatorsUtils.crossTranslateOutFromVertical(
                            fragmentMainBinding.constraintTopSelectionContainer as View,
                            -1,
                            animate,
                            300
                        )
                }
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
        mFragmentMainBinding?.let { fragmentMainBinding ->
            if (counter == null || counter <= 0)
                return
            MainScope().launch {
                if (fragmentMainBinding.slidingUpPanel.panelState != PanelState.COLLAPSED)
                    fragmentMainBinding.slidingUpPanel.panelState = PanelState.COLLAPSED
            }
        }
    }
    private fun showSlidingUpPanel(counter: Int?) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            if (counter == null || counter <= 0)
                return
            MainScope().launch {
                if (fragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
                    fragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
            }
        }
    }

    private fun updateMiniPlayerPlayPauseStateUI(isPlaying: Boolean?) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                context?.let {
                    if (isPlaying == true) {
                        fragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon =
                            AppCompatResources.getDrawable(it, R.drawable.pause)
                    } else {
                        fragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.icon =
                            AppCompatResources.getDrawable(it, R.drawable.play_arrow)
                    }
                }

            }
        }
    }
    private fun updateMiniPlayerSliderUI(currentDuration: Long) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                val totalDuration: Long =
                    mPlayerFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
                fragmentMainBinding.constraintMiniPlayerInclude.progressMiniPlayerIndicator.progress =
                    (FormattersUtils.formatSongDurationToSliderProgress(
                        currentDuration,
                        totalDuration
                    )).toInt()
            }
        }
    }
    private fun updateMiniPlayerUI(songItem : SongItem?) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if (songItem == null) {
                    fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                        context?.getString(R.string.unknown_title)
                    fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                        context?.getString(R.string.unknown_artist)
                    context?.let {
                        ImageLoadersUtils.loadWithResourceID(
                            it,
                            fragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer,
                            0,
                            0
                        )
                        Glide.with(it).clear(
                            fragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer
                        )
                    }
                    return@launch
                }
                fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                    if (songItem.title != null)
                        songItem.title
                    else
                        songItem.fileName

                fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                    if (songItem.artist != null)
                        songItem.artist
                    else
                        context?.getString(R.string.unknown_artist)

                val tempUri = Uri.parse(songItem.uri)
                context?.let {
                    ImageLoadersUtils.loadCovertArtFromSongUri(
                        it,
                        fragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer,
                        tempUri,
                        100,
                        100
                    )
                    ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                        it,
                        fragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer,
                        tempUri,
                        25
                    )
                }
            }
        }
    }

    private fun checkInteractions() {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            fragmentMainBinding.constraintMiniPlayerInclude.buttonPlayPause.setOnClickListener {
                onClickButtonPlayPause()
            }
            fragmentMainBinding.constraintMiniPlayerInclude.buttonSkipNext.setOnClickListener {
                onClickButtonSkipNextSong()
            }
            fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectAll.setOnClickListener {
                onToggleButtonSelectAll()
            }
            fragmentMainBinding.constraintBottomSelectionInclude.buttonSelectRange.setOnClickListener {
                onToggleButtonSelectRange()
            }
            fragmentMainBinding.constraintBottomSelectionInclude.buttonClose.setOnClickListener {
                onClickButtonCloseSelectionMenu()
            }
            fragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer.setOnClickListener {
                onClickMiniPlayerContainer()
            }
            fragmentMainBinding.slidingUpPanel.addPanelSlideListener(object :
                com.sothree.slidinguppanel.PanelSlideListener {
                override fun onPanelSlide(panel: View, slideOffset: Float) {
                    Log.i(ConstantValues.TAG, "On panel slide offset : $slideOffset")
                    if (slideOffset <= 0.21f) {
                        fragmentMainBinding.slidingUpPanel.setDragView(fragmentMainBinding.constraintMiniPlayerContainer)
                    } else {
                        fragmentMainBinding.slidingUpPanel.setDragView(fragmentMainBinding.mainFragmentContainer)
                    }
                }

                override fun onPanelStateChanged(
                    panel: View,
                    previousState: PanelState,
                    newState: PanelState
                ) {
                    mMainFragmentViewModel.setSlidingUpPanelState(newState)
                }
            })
            fragmentMainBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
                if (fragmentMainBinding.navigationView.checkedItem?.itemId != menuItem.itemId) {
                    when (menuItem.itemId) {
                        R.id.music_library -> {
                            showMusicLibraryFragment()
                            fragmentMainBinding.drawerLayout.close()
                        }
                        R.id.folders_hierarchy -> {
                            showFolderHierarchyFragment()
                            fragmentMainBinding.drawerLayout.close()
                        }
                        R.id.playlists -> {
                            showPlaylistsFragment()
                            fragmentMainBinding.drawerLayout.close()
                        }
                        R.id.streams -> {
                            showStreamsFragment()
                            fragmentMainBinding.drawerLayout.close()
                        }
                    }
                }
                when (menuItem.itemId) {
                    R.id.settings -> {
                        startActivity(Intent(context, SettingsActivity::class.java).apply {})
                    }
                }
                true
            }
        }
    }
    private fun onClickMiniPlayerContainer() {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            if (fragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
                fragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
        }
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
        if((activity?.supportFragmentManager?.fragments?.size ?: 0) >= 4)
            return
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
        mFragmentMainBinding?.let { fragmentMainBinding ->
            fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerTitle.isSelected = true
            fragmentMainBinding.constraintMiniPlayerInclude.textMiniPlayerArtist.isSelected = true

            ViewInsetModifiersUtils.updateTopViewInsets(fragmentMainBinding.mainFragmentContainer)
            ViewInsetModifiersUtils.updateTopViewInsets(fragmentMainBinding.constraintTopSelectionInclude.constraintTopSelectionMenu)
            ViewInsetModifiersUtils.updateBottomViewInsets(fragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer)
            ViewInsetModifiersUtils.updateBottomViewInsets(fragmentMainBinding.constraintBottomSelectionInclude.constraintBottomSelectionMenu)

            fragmentMainBinding.navigationView.setCheckedItem(R.id.music_library)
        }
    }

    companion object {
        const val TAG: String = "MainFragment"

        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}