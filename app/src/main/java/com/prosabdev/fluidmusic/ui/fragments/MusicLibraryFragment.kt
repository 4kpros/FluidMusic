package com.prosabdev.fluidmusic.ui.fragments

import android.graphics.Bitmap
import android.icu.text.CaseMap.Fold
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.databinding.DialogAddToQueueMusicBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.databinding.FragmentMusicLibraryBinding
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.*
import com.prosabdev.fluidmusic.service.intents.IntentActionsManager
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.EditTagsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.fragments.commonmethods.CommonPlaybackAction
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.PlaylistActionsWorkerViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.QueueMusicActionsWorkerViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.SongActionsWorkerViewModel
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import com.prosabdev.fluidmusic.workers.queuemusic.AddSongsToQueueMusicWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicLibraryFragment : Fragment() {
    private var mFragmentMusicLibraryBinding: FragmentMusicLibraryBinding? = null

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mPlaylistActionsWorkerViewModel: PlaylistActionsWorkerViewModel by activityViewModels()
    private val mQueueMusicActionsWorkerViewModel: QueueMusicActionsWorkerViewModel by activityViewModels()
    private val mSongActionsWorkerViewModel: SongActionsWorkerViewModel by activityViewModels()

    private val mEditTagsBottomSheetDialogFragment: EditTagsBottomSheetDialogFragment = EditTagsBottomSheetDialogFragment.newInstance()

    private var mTabLayoutAdapter: TabLayoutAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentMusicLibraryBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_music_library,container,false)
        val view = mFragmentMusicLibraryBinding?.root

        initViews()
        setupTabLayoutViewPagerAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
        observeLiveData()
    }

    private fun setupTabLayoutViewPagerAdapter() {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            mTabLayoutAdapter = TabLayoutAdapter(this)
            fragmentMusicLibraryBinding.viewPager.adapter = mTabLayoutAdapter
            fragmentMusicLibraryBinding.viewPager.offscreenPageLimit = 1
            TabLayoutMediator(
                fragmentMusicLibraryBinding.tabLayout,
                fragmentMusicLibraryBinding.viewPager
            ) { tab, position ->
                applyToolBarTitle(position, tab)
            }.attach()
            fragmentMusicLibraryBinding.viewPager.currentItem = 0
        }
    }
    private fun applyToolBarTitle(position: Int, tab: TabLayout.Tab) {
        when(position){
            0->{
                tab.text = getString(R.string.songs)
            }
            1->{
                tab.text = getString(R.string.albums)
            }
            2->{
                tab.text = getString(R.string.artists)
            }
            3->{
                tab.text = getString(R.string.folders)
            }
            4->{
                tab.text = getString(R.string.genre)
            }
            5->{
                tab.text = getString(R.string.album_artists)
            }
            6->{
                tab.text = getString(R.string.composers)
            }
            7->{
                tab.text = getString(R.string.years)
            }
        }
    }

    private fun observeLiveData() {
        mMainFragmentViewModel.getSelectMode().observe(viewLifecycleOwner) {
            updateSelectModeUI(it)
        }
        mMainFragmentViewModel.getSelectedDataList().observe(viewLifecycleOwner
        ) { dataList ->
            updateTotalSelectedItemsUI(dataList.size)
        }
        mMainFragmentViewModel.getIsFastScrolling().observe(viewLifecycleOwner){
            tryToUpdateFastScrollStateUI(it)
        }
    }
    private fun tryToUpdateFastScrollStateUI(isFastScrolling: Boolean = true) {
        if(isFastScrolling){
            if(mMainFragmentViewModel.getSelectMode().value == true){
                hideSideContentSelectionMenu()
            }
            mFragmentMusicLibraryBinding?.appBarLayout?.setExpanded(false)
        }else{
            if(mMainFragmentViewModel.getSelectMode().value == true){
                showSideContentSelectionMenu()
            }
        }
    }
    private fun showSideContentSelectionMenu(animate: Boolean = true) {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            AnimatorsUtils.crossFadeUp(
                fragmentMusicLibraryBinding.includeSideSelectionMenu.relativeContainer as View,
                animate,
                50
            )
            AnimatorsUtils.crossFadeUp(
                fragmentMusicLibraryBinding.includeSideSelectionMenu.cardViewContainer as View,
                animate,
                200
            )
        }
    }
    private fun hideSideContentSelectionMenu(animate: Boolean = true) {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            AnimatorsUtils.crossFadeDown(
                fragmentMusicLibraryBinding.includeSideSelectionMenu.relativeContainer as View,
                animate,
                25
            )
            AnimatorsUtils.crossFadeDown(
                fragmentMusicLibraryBinding.includeSideSelectionMenu.cardViewContainer as View,
                animate,
                100
            )
        }
    }
    private  fun updateTotalSelectedItemsUI(totalSelected : Int){
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            if (totalSelected > 0) {
                enableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlayAfter)
                enableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlaylistAdd)
                enableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonEditTags)
                enableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonShare)
                enableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonDelete)
            }else{
                disableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlayAfter)
                disableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlaylistAdd)
                disableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonEditTags)
                disableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonShare)
                disableSideSelectionActions(fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonDelete)
            }
        }
    }
    private fun disableSideSelectionActions(view : View) {
        AnimatorsUtils.crossFadeDownClickable(
            view,
            true
        )
    }
    private fun enableSideSelectionActions(view : View) {
        AnimatorsUtils.crossFadeUpClickable(
            view,
            true
        )
    }

    private fun updateSelectModeUI(isSelectMode: Boolean, animate : Boolean = true){
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            if (isSelectMode) {
                fragmentMusicLibraryBinding.viewPager.isUserInputEnabled = false
                AnimatorsUtils.crossTranslateInFromHorizontal(
                    fragmentMusicLibraryBinding.sideSelectionMenuContainer as View,
                    animate,
                    200
                )
            } else {
                fragmentMusicLibraryBinding.viewPager.isUserInputEnabled = true
                AnimatorsUtils.crossTranslateOutFromHorizontal(
                    fragmentMusicLibraryBinding.sideSelectionMenuContainer as View,
                    1,
                    animate,
                    200,
                    500f
                )
            }
        }
    }

    private fun checkInteractions() {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            fragmentMusicLibraryBinding.topAppBar.setNavigationOnClickListener {
                mMainFragmentViewModel.setShowDrawerMenuCounter()
            }
            fragmentMusicLibraryBinding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateViewModelLibraryPage(position)
                    mMainFragmentViewModel.setSelectMode(false)
                    mMainFragmentViewModel.setSelectedDataList(HashMap())
                    updateSelectModeUI(false)
                    applyAppBarTitle(position)
                }
            })
            fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlayAfter.setOnClickListener {
                addToQueueMusic()
            }
            fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonPlaylistAdd.setOnClickListener {
                openPlaylistAddDialog()
            }
            fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonEditTags.setOnClickListener {
                openTagEditorDialog()
            }
            fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonShare.setOnClickListener {
                openShareSelectionDialog()
            }
            fragmentMusicLibraryBinding.includeSideSelectionMenu.buttonDelete.setOnClickListener {
                openDeleteSelectionDialog()
            }
        }
    }
    private fun openTagEditorDialog() {
        if(!mEditTagsBottomSheetDialogFragment.isVisible){
            mEditTagsBottomSheetDialogFragment.show(childFragmentManager, EditTagsBottomSheetDialogFragment.TAG)
        }
    }
    private fun openPlaylistAddDialog() {
        //
    }

    private fun updateViewModelLibraryPage(position: Int) {
        mMainFragmentViewModel.setTotalCount(0)
        mMainFragmentViewModel.setSelectedDataList(HashMap())
        mMainFragmentViewModel.setSelectMode(false)
        when (position) {
            0 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(AllSongsFragment.TAG)
            }
            1 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(AlbumsFragment.TAG)
            }
            2 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(ArtistsFragment.TAG)
            }
            3 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(FoldersFragment.TAG)
            }
            4 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(GenresFragment.TAG)
            }
            5 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(AlbumArtistsFragment.TAG)
            }
            6 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(ComposersFragment.TAG)
            }
            7 -> {
                mMainFragmentViewModel.setCurrentSelectablePage(YearsFragment.TAG)
            }
        }
    }

    private fun openShareSelectionDialog() {
        context?.let { ctx ->
            val dialogShareSongBinding : DialogShareSongBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_share_song, null, false)
            dialogShareSongBinding.buttonShareScreenshot.isEnabled =
                (mMainFragmentViewModel.getSelectedDataList().value?.size ?: 0) <= 1
            dialogShareSongBinding.buttonShareFile.setOnClickListener {
                shareSelectedFiles()
            }
            dialogShareSongBinding.buttonShareScreenshot.setOnClickListener {
                shareScreenShootForSelectedUniqueItem()
            }
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(ctx.getString(R.string.share))
                .setIcon(R.drawable.share)
                .setView(dialogShareSongBinding.root)
                .setNegativeButton(ctx.getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show().apply {
                }
        }
    }

    private fun shareSelectedFiles() {
        //
    }

    private fun shareScreenShootForSelectedUniqueItem() {
        //
    }

    private fun openDeleteSelectionDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(resources.getString(R.string.dialog_delete_selection_title))
            .setIcon(R.drawable.delete)
            .setMessage(resources.getString(R.string.dialog_delete_selection_description))
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.delete_files)) { dialog, which ->
                deleteSelection()
            }
            .show()
    }
    private fun deleteSelection() {
        val modelTypeInfo = CommonPlaybackAction.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.getSelectedDataList().value ?: return
        mSongActionsWorkerViewModel.deleteSongs(
            modelTypeInfo[0],
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun addToQueueMusic() {
        val dataBidingView : DialogAddToQueueMusicBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_add_to_queue_music, null, false)
        dataBidingView.buttonAddToTop.setOnClickListener{
            addSelectionToTopOfQueueMusic()
        }
        dataBidingView.buttonPlayNext.setOnClickListener{
            addSelectionToPlayNextOfQueueMusic()
        }
        dataBidingView.buttonAddToEnd.setOnClickListener{
            addSelectionToBottomOfQueueMusic()
        }
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle("Add to queue")
            .setIcon(R.drawable.queue_music)
            .setView(dataBidingView.root)
            .setNegativeButton(resources.getString(R.string.close)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addSelectionToBottomOfQueueMusic() {
        val modelTypeInfo = CommonPlaybackAction.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.getSelectedDataList().value ?: return
        mQueueMusicActionsWorkerViewModel.addSongsToQueueMusic(
            modelTypeInfo[0],
            AddSongsToQueueMusicWorker.ADD_METHOD_ADD_TO_BOTTOM,
            -1,
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun addSelectionToPlayNextOfQueueMusic() {
        val modelTypeInfo = CommonPlaybackAction.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.getSelectedDataList().value ?: return
        val currentPlayingPosition = mPlayerFragmentViewModel.getCurrentPlayingSong().value?.position ?: 0
        mQueueMusicActionsWorkerViewModel.addSongsToQueueMusic(
            modelTypeInfo[0],
            AddSongsToQueueMusicWorker.ADD_METHOD_ADD_AT_POSITION,
            currentPlayingPosition,
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun addSelectionToTopOfQueueMusic() {
        val modelTypeInfo = CommonPlaybackAction.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.getSelectedDataList().value ?: return
        mQueueMusicActionsWorkerViewModel.addSongsToQueueMusic(
            modelTypeInfo[0],
            AddSongsToQueueMusicWorker.ADD_METHOD_ADD_TO_TOP,
            0,
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun applyAppBarTitle(position: Int) {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            MainScope().launch {
                when (position) {
                    0->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.songs)
                    }
                    1->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.albums)
                    }
                    2->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.artists)
                    }
                    3->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.folders)
                    }
                    4->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.genre)
                    }
                    5->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.album_artists)
                    }
                    6->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.composers)
                    }
                    7->{
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.years)
                    }
                }
            }
        }
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "MusicLibraryFragment"

        @JvmStatic
        fun newInstance() =
            MusicLibraryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}