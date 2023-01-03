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
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.ui.activities.SettingsActivity
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class MainFragment : Fragment() {

    private var mDataBidingView: FragmentMainBinding? = null

    private val  mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

    private val mMusicLibraryFragment = MusicLibraryFragment.newInstance()
    private val mFoldersHierarchyFragment = FoldersHierarchyFragment.newInstance()
    private val mPlaylistsFragment = PlaylistsFragment.newInstance()
    private val mStreamsFragment = StreamsFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false)
        val view = mDataBidingView?.root

        if(savedInstanceState == null){
            initViews()
            setupFragments()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            checkInteractions()
            observeLiveData()
        }
    }

    override fun onResume() {
        updateDrawerMenuUI()
        super.onResume()
    }

    private fun updateDrawerMenuUI() {
        mDataBidingView?.let { dataBidingView ->
            when (childFragmentManager.findFragmentById(R.id.main_fragment_container)) {
                is MusicLibraryFragment -> {
                    dataBidingView.navigationView.setCheckedItem(R.id.music_library)
                }
                is FoldersHierarchyFragment -> {
                    dataBidingView.navigationView.setCheckedItem(R.id.folders_hierarchy)
                }
                is PlaylistsFragment -> {
                    dataBidingView.navigationView.setCheckedItem(R.id.playlists)
                }
                is StreamsFragment -> {
                    dataBidingView.navigationView.setCheckedItem(R.id.streams)
                }
            }
        }
    }

    private fun observeLiveData() {
        //Observe live data from player fragment
//        mNowPlayingFragmentViewModel.getCurrentPlayingSong().observe(viewLifecycleOwner){
//            updateMiniPlayerUI(it)
//        }
//        mNowPlayingFragmentViewModel.getPlayingProgressValue().observe(viewLifecycleOwner){
//            updateMiniPlayerSliderUI(it)
//        }
//        mNowPlayingFragmentViewModel.getIsPlaying().observe(viewLifecycleOwner){
//            updateMiniPlayerPlayPauseStateUI(it)
//        }

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
        mMainFragmentViewModel.getSelectedDataList().observe(viewLifecycleOwner){
            updateTotalSelectedTracksUI(it.size)
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
        mDataBidingView?.let { dataBidingView ->
            if ((showCounter ?: 0) <= 0) return
            if (!dataBidingView.drawerLayout.isOpen)
                dataBidingView.drawerLayout.open()
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
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateInFromVertical(
                dataBidingView.constraintBottomSelectionContainer as View,
                animate,
                200
            )
        }
    }
    private fun hideBottomSelectionMenu(animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateOutFromVertical(
                dataBidingView.constraintBottomSelectionContainer as View,
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
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                if (scrollState == null || scrollState >= 1) {
                    if(dataBidingView.constraintMiniPlayerContainer.alpha < 1.0f) return@launch

                    if (mIsAnimatingScroll2) {
                        dataBidingView.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll2 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll1)
                        return@launch
                    mIsAnimatingScroll1 = true
                    com.prosabdev.common.utils.AnimatorsUtils.crossTranslateOutFromVertical(
                        dataBidingView.constraintMiniPlayerContainer,
                        1,
                        animate,
                        200,
                        500f
                    )
                } else {
                    if (mIsAnimatingScroll1) {
                        dataBidingView.constraintMiniPlayerContainer.apply {
                            mIsAnimatingScroll1 = false
                            clearAnimation()
                        }
                    }
                    if (mIsAnimatingScroll2)
                        return@launch
                    mIsAnimatingScroll2 = true
                    com.prosabdev.common.utils.AnimatorsUtils.crossTranslateInFromVertical(
                        dataBidingView.constraintMiniPlayerContainer,
                        animate,
                        200
                    )
                }
            }
        }
    }
    private fun updateTotalSelectedTracksUI(totalSelected: Int, animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                if(totalSelected > 0 && totalSelected >= (mMainFragmentViewModel.getTotalCount().value ?: 0)){
                    com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                        dataBidingView.includeBottomSelection.buttonSelectRange,
                        animate,
                        200
                    )
                    dataBidingView.includeBottomSelection.checkboxSelectAll.isChecked = true
                }else{
                    dataBidingView.includeBottomSelection.checkboxSelectAll.isChecked = false
                    if(totalSelected >= 2){
                        com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                            dataBidingView.includeBottomSelection.buttonSelectRange,
                            animate,
                            200
                        )
                    }else{
                        com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                            dataBidingView.includeBottomSelection.buttonSelectRange,
                            animate,
                            200
                        )
                    }
                }
                dataBidingView.includeTopSelection.textSelectedCount.text = "$totalSelected / ${mMainFragmentViewModel.getTotalCount().value}"
            }
        }
    }
    private fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                if (selectMode) {
                    updateMiniPlayerScrollingStateUI(1, animate)
                    showTopBottomSelectionMenu(animate)
                } else {
                    tryToUpdateMiniPlayerScrollStateUI(mMainFragmentViewModel.getScrollingState().value)
                    dataBidingView.includeBottomSelection.checkboxSelectAll.isChecked = false
                    hideTopBottomSelectionMenu(animate)
                }
            }
        }
    }
    private fun hideTopBottomSelectionMenu(animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateOutFromVertical(
                dataBidingView.constraintBottomSelectionContainer as View,
                1,
                animate,
                200,
                500f
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateOutFromVertical(
                dataBidingView.constraintTopSelectionContainer as View,
                -1,
                animate,
                200,
                500f
            )
        }
    }
    private fun showTopBottomSelectionMenu(animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateInFromVertical(
                dataBidingView.constraintBottomSelectionContainer as View,
                animate,
                200
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossTranslateInFromVertical(
                dataBidingView.constraintTopSelectionContainer as View,
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
        mDataBidingView?.let { dataBidingView ->
            if (counter == null || counter <= 0)
                return
            MainScope().launch {
                if (dataBidingView.slidingUpPanel.panelState != PanelState.COLLAPSED)
                    dataBidingView.slidingUpPanel.panelState = PanelState.COLLAPSED
            }
        }
    }
    private fun showSlidingUpPanel(counter: Int?) {
        mDataBidingView?.let { dataBidingView ->
            if (counter == null || counter <= 0)
                return
            MainScope().launch {
                if (dataBidingView.slidingUpPanel.panelState != PanelState.EXPANDED)
                    dataBidingView.slidingUpPanel.panelState = PanelState.EXPANDED
            }
        }
    }

    private fun updateMiniPlayerPlayPauseStateUI(isPlaying: Boolean?) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                context?.let {
                    if (isPlaying == true) {
                        tryToUpdateMiniPlayerScrollStateUI(-2)
                        dataBidingView.constraintMiniPlayerInclude.buttonPlayPause.icon =
                            AppCompatResources.getDrawable(it, R.drawable.pause)
                    } else {
                        dataBidingView.constraintMiniPlayerInclude.buttonPlayPause.icon =
                            AppCompatResources.getDrawable(it, R.drawable.play_arrow)
                    }
                }

            }
        }
    }
    private fun updateMiniPlayerSliderUI(currentDuration: Long) {
//        mDataBidingView?.let { dataBidingView ->
//            MainScope().launch {
//                val totalDuration: Long =
//                    mNowPlayingFragmentViewModel.getCurrentPlayingSong().value?.duration ?: 0
//                dataBidingView.constraintMiniPlayerInclude.progressMiniPlayerIndicator.progress =
//                    (com.prosabdev.common.utils.FormattersAndParsersUtils.formatSongDurationToSliderProgress(
//                        currentDuration,
//                        totalDuration
//                    )).toInt()
//            }
//        }
    }
    private fun updateMiniPlayerUI(songItem : com.prosabdev.common.models.songitem.SongItem?) {
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                if (songItem == null) {
                    dataBidingView.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                        context?.getString(R.string.unknown_title)
                    dataBidingView.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                        context?.getString(R.string.unknown_artist)
                    context?.let {
                        com.prosabdev.common.utils.ImageLoadersUtils.loadWithPlaceholderResourceID(
                            it,
                            dataBidingView.constraintMiniPlayerInclude.imageviewMiniPlayer,
                            0
                        )
                    }
                    return@launch
                }

                context?.let { ctx ->
                    dataBidingView.constraintMiniPlayerInclude.textMiniPlayerTitle.text =
                        songItem.title?.ifEmpty { songItem.fileName ?: ctx.getString(R.string.unknown_title) } ?: songItem.fileName ?: ctx.getString(R.string.unknown_title)
                    dataBidingView.constraintMiniPlayerInclude.textMiniPlayerArtist.text =
                        songItem.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)

                    val tempUri = Uri.parse(songItem.uri ?: "")
                    val imageRequest: com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem = com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem.newOriginalCardInstance()
                    val imageRequestBlurred: com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem = com.prosabdev.common.utils.ImageLoadersUtils.ImageRequestItem.newBlurInstance()

                    imageRequest.uri = tempUri
                    imageRequest.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                    imageRequest.imageView = dataBidingView.constraintMiniPlayerInclude.imageviewMiniPlayer
                    com.prosabdev.common.utils.ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequest)

                    imageRequestBlurred.uri = tempUri
                    imageRequestBlurred.hashedCovertArtSignature = songItem.hashedCovertArtSignature
                    imageRequestBlurred.imageView = dataBidingView.constraintMiniPlayerInclude.imageviewBlurredMiniPlayer
                    com.prosabdev.common.utils.ImageLoadersUtils.startExploreContentImageLoaderJob(ctx, imageRequestBlurred)
                }
            }
        }
    }

    private fun checkInteractions() {
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.constraintMiniPlayerInclude.buttonPlayPause.setOnClickListener {
                onClickButtonPlayPause()
            }
            dataBidingView.constraintMiniPlayerInclude.buttonSkipNext.setOnClickListener {
                onClickButtonSkipNextSong()
            }
            dataBidingView.includeBottomSelection.checkboxSelectAll.setOnClickListener {
                mMainFragmentViewModel.setReQuestToggleSelectAll()
            }
            dataBidingView.includeBottomSelection.buttonSelectRange.setOnClickListener {
                mMainFragmentViewModel.setReQuestToggleSelectRange()
            }
            dataBidingView.includeBottomSelection.buttonClose.setOnClickListener {
                onClickButtonCloseSelectionMenu()
            }
            dataBidingView.constraintMiniPlayerInclude.constraintMiniPlayer.setOnClickListener {
                onClickMiniPlayerContainer()
            }
            dataBidingView.slidingUpPanel.addPanelSlideListener(object :
                com.sothree.slidinguppanel.PanelSlideListener {
                override fun onPanelSlide(panel: View, slideOffset: Float) {
                    Log.i(com.prosabdev.common.utils.ConstantValues.TAG, "On panel slide offset : $slideOffset")
                    if (slideOffset <= 0.21f) {
                        dataBidingView.slidingUpPanel.setDragView(dataBidingView.constraintMiniPlayerContainer)
                    } else {
                        dataBidingView.slidingUpPanel.setDragView(dataBidingView.mainFragmentContainer)
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
            dataBidingView.navigationView.setNavigationItemSelectedListener { menuItem ->
                dataBidingView.drawerLayout.close()
                if (dataBidingView.navigationView.checkedItem?.itemId != menuItem.itemId) {
                    when (menuItem.itemId) {
                        R.id.music_library -> {
                            showMusicLibraryFragment()
                        }
                        R.id.folders_hierarchy -> {
                            showFolderHierarchyFragment()
                        }
                        R.id.playlists -> {
                            showPlaylistsFragment()
                        }
                        R.id.streams -> {
                            showStreamsFragment()
                        }
                        R.id.settings -> {
                            startActivity(Intent(context, SettingsActivity::class.java).apply {})
                        }
                    }
                }
                true
            }
        }
    }

    private fun onClickMiniPlayerContainer() {
        mDataBidingView?.let { dataBidingView ->
            if (dataBidingView.slidingUpPanel.panelState != PanelState.EXPANDED)
                dataBidingView.slidingUpPanel.panelState = PanelState.EXPANDED
        }
    }

    private fun onClickButtonCloseSelectionMenu() {
        mMainFragmentViewModel.setSelectedDataList(HashMap())
        mMainFragmentViewModel.setTotalCount(0)
        mMainFragmentViewModel.setSelectMode(false)
    }

    private fun onClickButtonSkipNextSong() {
//        mNowPlayingFragmentViewModel.setCanScrollCurrentPlayingSong(true)
//        mNowPlayingFragmentViewModel.setSkipNextTrackCounter()
    }
    private fun onClickButtonPlayPause() {
//        if(mNowPlayingFragmentViewModel.getIsPlaying().value == false) {
//            mNowPlayingFragmentViewModel.setCanScrollCurrentPlayingSong(true)
//        }
//        mNowPlayingFragmentViewModel.toggleIsPlaying()
    }

    private fun setupFragments() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.player_fragment_container, NowPlayingFragment.newInstance())
        }
        showMusicLibraryFragment()
    }
    private fun showMusicLibraryFragment() {
//        childFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.main_fragment_container, mMusicLibraryFragment)
//        }
    }
    private fun showFolderHierarchyFragment() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, mFoldersHierarchyFragment)
        }
    }
    private fun showPlaylistsFragment() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, mPlaylistsFragment)
        }
    }
    private fun showStreamsFragment() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, mStreamsFragment)
        }
    }

    private fun initViews() {
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.constraintMiniPlayerInclude.textMiniPlayerTitle.isSelected = true
            dataBidingView.constraintMiniPlayerInclude.textMiniPlayerArtist.isSelected = true

            com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(dataBidingView.includeTopSelection.constraintContainer)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.constraintMiniPlayerInclude.constraintMiniPlayer)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.includeBottomSelection.constraintContainer)

            dataBidingView.navigationView.setCheckedItem(R.id.music_library)
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