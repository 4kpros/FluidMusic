package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.EditTagsBottomSheetDialogFragment
import com.prosabdev.fluidmusic.ui.fragments.commonmethods.CommonPlaybackAction
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.QueueMusicActionsWorkerViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.SongActionsWorkerViewModel
import com.prosabdev.fluidmusic.workers.queuemusic.AddSongsToQueueMusicWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicLibraryFragment : Fragment() {
    private var mDataBidingView: FragmentMusicLibraryBinding? = null

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

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
        mDataBidingView = DataBindingUtil.inflate(inflater,R.layout.fragment_music_library,container,false)
        val view = mDataBidingView?.root

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
        mDataBidingView?.let { dataBidingView ->
            mTabLayoutAdapter = TabLayoutAdapter(this)
            MainScope().launch {
                dataBidingView.viewPager.adapter = mTabLayoutAdapter
                dataBidingView.viewPager.offscreenPageLimit = 8
                TabLayoutMediator(
                    dataBidingView.tabLayout,
                    dataBidingView.viewPager
                ) { tab, position ->
                    applyToolBarTitle(position, tab)
                }.attach()
                dataBidingView.viewPager.currentItem = 0
            }
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
            mDataBidingView?.appBarLayout?.setExpanded(false)
        }else{
            if(mMainFragmentViewModel.getSelectMode().value == true){
                showSideContentSelectionMenu()
            }
        }
    }
    private fun showSideContentSelectionMenu(animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            AnimatorsUtils.crossFadeUp(
                dataBidingView.includeSideSelectionMenu.relativeContainer as View,
                animate,
                50
            )
            AnimatorsUtils.crossFadeUp(
                dataBidingView.includeSideSelectionMenu.cardViewContainer as View,
                animate,
                200
            )
        }
    }
    private fun hideSideContentSelectionMenu(animate: Boolean = true) {
        mDataBidingView?.let { dataBidingView ->
            AnimatorsUtils.crossFadeDown(
                dataBidingView.includeSideSelectionMenu.relativeContainer as View,
                animate,
                25
            )
            AnimatorsUtils.crossFadeDown(
                dataBidingView.includeSideSelectionMenu.cardViewContainer as View,
                animate,
                100
            )
        }
    }
    private  fun updateTotalSelectedItemsUI(totalSelected : Int){
        mDataBidingView?.let { dataBidingView ->
            if (totalSelected > 0) {
                enableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonPlayAfter)
                enableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonPlaylistAdd)
                enableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonEditTags)
                enableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonShare)
                enableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonDelete)
            }else{
                disableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonPlayAfter)
                disableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonPlaylistAdd)
                disableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonEditTags)
                disableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonShare)
                disableSideSelectionActions(dataBidingView.includeSideSelectionMenu.buttonDelete)
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
        mDataBidingView?.let { dataBidingView ->
            if (isSelectMode) {
                dataBidingView.viewPager.isUserInputEnabled = false
                AnimatorsUtils.crossTranslateInFromHorizontal(
                    dataBidingView.sideSelectionMenuContainer as View,
                    animate,
                    200
                )
            } else {
                dataBidingView.viewPager.isUserInputEnabled = true
                AnimatorsUtils.crossTranslateOutFromHorizontal(
                    dataBidingView.sideSelectionMenuContainer as View,
                    1,
                    animate,
                    200,
                    500f
                )
            }
        }
    }

    private fun checkInteractions() {
        mDataBidingView?.let { dataBidingView ->
            dataBidingView.topAppBar.setNavigationOnClickListener {
                mMainFragmentViewModel.setShowDrawerMenuCounter()
            }
            dataBidingView.viewPager.registerOnPageChangeCallback(object :
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
            dataBidingView.includeSideSelectionMenu.buttonPlayAfter.setOnClickListener {
                addToQueueMusic()
            }
            dataBidingView.includeSideSelectionMenu.buttonPlaylistAdd.setOnClickListener {
                openPlaylistAddDialog()
            }
            dataBidingView.includeSideSelectionMenu.buttonEditTags.setOnClickListener {
                openTagEditorDialog()
            }
            dataBidingView.includeSideSelectionMenu.buttonShare.setOnClickListener {
                openShareSelectionDialog()
            }
            dataBidingView.includeSideSelectionMenu.buttonDelete.setOnClickListener {
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
        mDataBidingView?.let { dataBidingView ->
            MainScope().launch {
                when (position) {
                    0->{
                        dataBidingView.topAppBar.title = getString(R.string.songs)
                    }
                    1->{
                        dataBidingView.topAppBar.title = getString(R.string.albums)
                    }
                    2->{
                        dataBidingView.topAppBar.title = getString(R.string.artists)
                    }
                    3->{
                        dataBidingView.topAppBar.title = getString(R.string.folders)
                    }
                    4->{
                        dataBidingView.topAppBar.title = getString(R.string.genre)
                    }
                    5->{
                        dataBidingView.topAppBar.title = getString(R.string.album_artists)
                    }
                    6->{
                        dataBidingView.topAppBar.title = getString(R.string.composers)
                    }
                    7->{
                        dataBidingView.topAppBar.title = getString(R.string.years)
                    }
                }
            }
        }
    }

    private fun initViews() {
        mDataBidingView?.let { dataBidingView ->
            InsetModifiersUtils.updateTopViewInsets(dataBidingView.coordinatorLayout)
        }
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