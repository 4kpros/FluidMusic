package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.ui.activities.SettingsActivity
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.sothree.slidinguppanel.PanelState
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

    override fun onResume() {
        super.onResume()
        updateDrawerMenuUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }
    private fun updateDrawerMenuUI() {
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
            tryToUpdateMiniPlayerScrollStateUI(it)
        }
        mMainFragmentViewModel.getIsFastScrolling().observe(viewLifecycleOwner){
            tryToUpdateFastScrollStateUI(it)
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
    private fun tryToUpdateFastScrollStateUI(isFastScrolling: Boolean = true) {
        if(isFastScrolling){
            updateMiniPlayerScrollingStateUI(1)
            hideBottomSelectionMenu()
        }else{
            tryToUpdateMiniPlayerScrollStateUI(mMainFragmentViewModel.getScrollingState().value)
            if(mMainFragmentViewModel.getSelectMode().value == true) {
                showBottomSelectionMenu()
            }else{
                hideBottomSelectionMenu()
            }
        }
    }
    private fun showBottomSelectionMenu(animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            AnimatorsUtils.crossTranslateInFromVertical(
                fragmentMainBinding.constraintBottomSelectionContainer as View,
                animate,
                200
            )
        }
    }
    private fun hideBottomSelectionMenu(animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            AnimatorsUtils.crossTranslateOutFromVertical(
                fragmentMainBinding.constraintBottomSelectionContainer as View,
                1,
                animate,
                100,
                500f
            )
        }
    }

    private fun tryToUpdateMiniPlayerScrollStateUI(scrollState: Int?, animate: Boolean = true) {
        if(
            mMainFragmentViewModel.getIsFastScrolling().value == true ||
            mMainFragmentViewModel.getSelectMode().value == true
        ) return
        updateMiniPlayerScrollingStateUI(scrollState, animate)
    }
    private var mIsAnimatingScroll1: Boolean = false
    private var mIsAnimatingScroll2: Boolean = false
    private fun updateMiniPlayerScrollingStateUI(scrollState: Int?, animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if (scrollState == null || scrollState >= 1) {
                    if(fragmentMainBinding.constraintMiniPlayerContainer.alpha < 1.0f) return@launch

                    if (mIsAnimatingScroll2) {
                        fragmentMainBinding.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll2 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll1)
                        return@launch
                    mIsAnimatingScroll1 = true
                    AnimatorsUtils.crossTranslateOutFromVertical(
                        fragmentMainBinding.constraintMiniPlayerContainer,
                        1,
                        animate,
                        200,
                        500f
                    )
                } else {
                    if (mIsAnimatingScroll1) {
                        fragmentMainBinding.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll1 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll2)
                        return@launch
                    mIsAnimatingScroll2 = true
                    AnimatorsUtils.crossTranslateInFromVertical(
                        fragmentMainBinding.constraintMiniPlayerContainer,
                        animate,
                        200
                    )
                }
            }
        }
    }
    private fun updateTotalSelectedTracksUI(totalSelected: Int, animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
                    AnimatorsUtils.crossFadeDownClickable(
                        fragmentMainBinding.includeBottomSelection.buttonSelectRange,
                        animate,
                        200
                    )
                    fragmentMainBinding.includeBottomSelection.checkboxSelectAll.isChecked = true
                }else{
                    fragmentMainBinding.includeBottomSelection.checkboxSelectAll.isChecked = false
                    if(totalSelected >= 2){
                        AnimatorsUtils.crossFadeUpClickable(
                            fragmentMainBinding.includeBottomSelection.buttonSelectRange,
                            animate,
                            200
                        )
                    }else{
                        AnimatorsUtils.crossFadeDownClickable(
                            fragmentMainBinding.includeBottomSelection.buttonSelectRange,
                            animate,
                            200
                        )
                    }
                }
                fragmentMainBinding.includeTopSelection.textSelectedCount.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
            }
        }
    }
    private fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            MainScope().launch {
                if (selectMode) {
                    updateMiniPlayerScrollingStateUI(1, animate)
                    showTopBottomSelectionMenu(animate)
                } else {
                    tryToUpdateMiniPlayerScrollStateUI(mMainFragmentViewModel.getScrollingState().value)
                    fragmentMainBinding.includeBottomSelection.checkboxSelectAll.isChecked = false
                    hideTopBottomSelectionMenu(animate)
                }
            }
        }
    }
    private fun hideTopBottomSelectionMenu(animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            AnimatorsUtils.crossTranslateOutFromVertical(
                fragmentMainBinding.constraintBottomSelectionContainer as View,
                1,
                animate,
                200,
                500f
            )
            AnimatorsUtils.crossTranslateOutFromVertical(
                fragmentMainBinding.constraintTopSelectionContainer as View,
                -1,
                animate,
                200,
                500f
            )
        }
    }
    private fun showTopBottomSelectionMenu(animate: Boolean = true) {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            AnimatorsUtils.crossTranslateInFromVertical(
                fragmentMainBinding.constraintBottomSelectionContainer as View,
                animate,
                200
            )
            AnimatorsUtils.crossTranslateInFromVertical(
                fragmentMainBinding.constraintTopSelectionContainer as View,
                animate,
                200
            )
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
                        tryToUpdateMiniPlayerScrollStateUI(-2)
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
                    (FormattersAndParsersUtils.formatSongDurationToSliderProgress(
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
                            0
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

                val tempUri = Uri.parse(songItem.uri ?: "")
                context?.let { ctx ->
                    val imageRequest: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
                    val imageRequestBlurred: ImageLoadersUtils.ImageRequestItem = ImageLoadersUtils.ImageRequestItem.newBlurInstance()

                    imageRequest.uri = tempUri
                    imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                    imageRequest.imageView = fragmentMainBinding.constraintMiniPlayerInclude.imageviewMiniPlayer
                    ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)

                    imageRequestBlurred.uri = tempUri
                    imageRequestBlurred.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                    imageRequestBlurred.imageView = fragmentMainBinding.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer
                    ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
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
            fragmentMainBinding.includeBottomSelection.checkboxSelectAll.setOnClickListener {
                onCheckAllButtonClicked()
            }
            fragmentMainBinding.includeBottomSelection.buttonSelectRange.setOnClickListener {
                onToggleButtonSelectRange()
            }
            fragmentMainBinding.includeBottomSelection.buttonClose.setOnClickListener {
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

    private fun onCheckAllButtonClicked() {
        val totalCount = mMainFragmentViewModel.getTotalCount().value ?: 0
        val totalSelected = mMainFragmentViewModel.getTotalSelected().value ?: 0
        if(totalSelected < totalCount){
            mMainFragmentViewModel.setTotalSelected(totalCount)
        }else{
            mMainFragmentViewModel.setTotalSelected(0)
        }
    }

    private fun onClickMiniPlayerContainer() {
        mFragmentMainBinding?.let { fragmentMainBinding ->
            if (fragmentMainBinding.slidingUpPanel.panelState != PanelState.EXPANDED)
                fragmentMainBinding.slidingUpPanel.panelState = PanelState.EXPANDED
        }
    }

    private fun onClickButtonCloseSelectionMenu() {
        mMainFragmentViewModel.setTotalSelected(0)
        mMainFragmentViewModel.setTotalCount(0)
        mMainFragmentViewModel.setSelectMode(false)
    }

    private fun onToggleButtonSelectRange() {
        mMainFragmentViewModel.setToggleRange()
    }

    private fun onClickButtonSkipNextSong() {
        mPlayerFragmentViewModel.setCanScrollCurrentPlayingSong(true)
        mPlayerFragmentViewModel.setSkipNextTrackCounter()
    }
    private fun onClickButtonPlayPause() {
        if(mPlayerFragmentViewModel.getIsPlaying().value == false) {
            mPlayerFragmentViewModel.setCanScrollCurrentPlayingSong(true)
        }
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

            InsetModifiersUtils.updateTopViewInsets(fragmentMainBinding.mainFragmentContainer)
            InsetModifiersUtils.updateTopViewInsets(fragmentMainBinding.includeTopSelection.constraintTopSelectionMenu)
            InsetModifiersUtils.updateBottomViewInsets(fragmentMainBinding.constraintMiniPlayerInclude.constraintMiniPlayer)
            InsetModifiersUtils.updateBottomViewInsets(fragmentMainBinding.includeBottomSelection.constraintBottomSelectionMenu)

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