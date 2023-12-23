package com.prosabdev.fluidmusic.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.common.utils.Animators
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.common.workers.queuemusic.AddSongsToQueueMusicWorker
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.databinding.DialogAddToQueueMusicBinding
import com.prosabdev.fluidmusic.databinding.DialogShareSongBinding
import com.prosabdev.fluidmusic.databinding.FragmentMusicLibraryBinding
import com.prosabdev.fluidmusic.ui.activities.EditTagsActivity
import com.prosabdev.fluidmusic.ui.fragments.communication.FragmentsCommunication
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayingNowFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaPlayerDataViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.QueueMusicActionsWorkerViewModel
import com.prosabdev.fluidmusic.viewmodels.workers.SongActionsWorkerViewModel

class MusicLibraryFragment : Fragment() {

    //Data binding
    private lateinit var mDataBinding: FragmentMusicLibraryBinding

    //View models
    private val mMediaPlayerDataViewModel: MediaPlayerDataViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayingNowFragmentViewModel: PlayingNowFragmentViewModel by activityViewModels()
    //View models for work manager
    private val mQueueMusicActionsWorkerViewModel: QueueMusicActionsWorkerViewModel by activityViewModels()
    private val mSongActionsWorkerViewModel: SongActionsWorkerViewModel by activityViewModels()

    //Tab layout adapter
    private var mTabLayoutAdapter: TabLayoutAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_music_library, container, false)
        val view = mDataBinding.root

        //Load your UI content
        if (savedInstanceState == null){
            initViews()
            setupTabLayoutAdapter()
            checkInteractions()
            observeLiveData()
        }

        return view
    }

    private fun observeLiveData() {
        mMainFragmentViewModel.selectMode.observe(viewLifecycleOwner) {
            updateUISelectMode(it)
        }
        mMainFragmentViewModel.selectedItems.observe(
            viewLifecycleOwner
        ) { dataList ->
            updateUITotalSelectedItems(dataList.size)
        }
        mMainFragmentViewModel.isFastScrolling.observe(viewLifecycleOwner) {
            updateUIFastScroller(it)
        }
    }

    private fun updateUIFastScroller(isFastScrolling: Boolean = true) {
        if (isFastScrolling) {
            if (mMainFragmentViewModel.selectMode.value == true) {
                Animators.crossFadeDown(
                    mDataBinding.includeSideSelectionMenu.relativeContainer,
                    true,
                    25
                )
                Animators.crossFadeDown(
                    mDataBinding.includeSideSelectionMenu.cardViewContainer,
                    true,
                    100
                )
            }
            mDataBinding.appBarLayout.setExpanded(false)
        } else {
            if (mMainFragmentViewModel.selectMode.value == true) {
                Animators.crossFadeUp(
                    mDataBinding.includeSideSelectionMenu.relativeContainer,
                    true,
                    50
                )
                Animators.crossFadeUp(
                    mDataBinding.includeSideSelectionMenu.cardViewContainer,
                    true,
                    200
                )
            }
        }
    }

    private fun updateUITotalSelectedItems(totalSelected: Int) {
        if (totalSelected > 0) {
            enableActionButton(mDataBinding.includeSideSelectionMenu.buttonAddToQueueMusic)
            enableActionButton(mDataBinding.includeSideSelectionMenu.buttonAddToPlaylist)
            enableActionButton(mDataBinding.includeSideSelectionMenu.buttonEditTags)
            enableActionButton(mDataBinding.includeSideSelectionMenu.buttonShare)
            enableActionButton(mDataBinding.includeSideSelectionMenu.buttonDelete)
        } else {
            disableActionButton(mDataBinding.includeSideSelectionMenu.buttonAddToQueueMusic)
            disableActionButton(mDataBinding.includeSideSelectionMenu.buttonAddToPlaylist)
            disableActionButton(mDataBinding.includeSideSelectionMenu.buttonEditTags)
            disableActionButton(mDataBinding.includeSideSelectionMenu.buttonShare)
            disableActionButton(mDataBinding.includeSideSelectionMenu.buttonDelete)
        }
    }
    private fun disableActionButton(v: View) { Animators.crossFadeDownClickable(v, true) }
    private fun enableActionButton(v: View) { Animators.crossFadeUpClickable(v, true) }

    private fun updateUISelectMode(isSelectMode: Boolean, animate: Boolean = true) {
        mDataBinding.let { dataBidingView ->
            if (isSelectMode) {
                dataBidingView.viewPager.isUserInputEnabled = false
                Animators.crossTranslateInFromHorizontal(
                    dataBidingView.sideSelectionMenuContainer as View,
                    animate,
                    200
                )
            } else {
                dataBidingView.viewPager.isUserInputEnabled = true
                Animators.crossTranslateOutFromHorizontal(
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
        mDataBinding.topAppBar.setNavigationOnClickListener {
            mMainFragmentViewModel.setShowDrawerMenuCounter()
        }
        mDataBinding.topAppBar.setOnMenuItemClickListener {
            if (it?.itemId == R.id.search) {
                onSearchButtonClicked()
            }
            true
        }
        mDataBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageChanged(position)
            }
        })
        mDataBinding.includeSideSelectionMenu.buttonAddToQueueMusic.setOnClickListener {
            onAddToQueueMusicButtonClicked()
        }
        mDataBinding.includeSideSelectionMenu.buttonAddToPlaylist.setOnClickListener {
            onAddToPlaylistButtonClicked()
        }
        mDataBinding.includeSideSelectionMenu.buttonEditTags.setOnClickListener {
            onEditTagsButtonClicked()
        }
        mDataBinding.includeSideSelectionMenu.buttonShare.setOnClickListener {
            onShareButtonClicked()
        }
        mDataBinding.includeSideSelectionMenu.buttonDelete.setOnClickListener {
            onDeleteButtonClicked()
        }
    }

    private fun onDeleteButtonClicked(){
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
        val modelTypeInfo = FragmentsCommunication.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.selectedItems.value ?: return
        mSongActionsWorkerViewModel.deleteSongs(
            modelTypeInfo[0],
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun onShareButtonClicked(){
        context?.also { ctx ->
            val dialogShareSongBinding: DialogShareSongBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.dialog_share_song, null, false)
            dialogShareSongBinding.buttonShareScreenshot.isEnabled =
                (mMainFragmentViewModel.selectedItems.value?.size ?: 0) <= 1
            dialogShareSongBinding.buttonShareFile.setOnClickListener {
                shareSelectedFiles()
            }
            dialogShareSongBinding.buttonShareScreenshot.setOnClickListener {
                shareScreenShootForSelectedUniqueItem()
            }
            MaterialAlertDialogBuilder(ctx)
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

    private fun onEditTagsButtonClicked(){
        startActivity(Intent(context, EditTagsActivity::class.java).apply {})
        activity?.also {
            it.supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(
                    R.id.main_activity_fragment_container,
                    EditTagsFragment.newInstance(
                        null,
                        null,
                        null
                    ),
                    EditTagsFragment.TAG
                )
                addToBackStack(null)
            }
        }
    }

    private fun onAddToPlaylistButtonClicked(){
    }

    private fun onAddToQueueMusicButtonClicked() {
        val dataBidingView: DialogAddToQueueMusicBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_add_to_queue_music, null, false)
        dataBidingView.buttonAddToTop.setOnClickListener {
            addSelectionToTopOfQueueMusic()
        }
        dataBidingView.buttonPlayNext.setOnClickListener {
            addSelectionToPlayNextOfQueueMusic()
        }
        dataBidingView.buttonAddToEnd.setOnClickListener {
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
        val modelTypeInfo = FragmentsCommunication.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.selectedItems.value ?: return
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
        val modelTypeInfo = FragmentsCommunication.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.selectedItems.value ?: return
        val currentPlayingIndex = mMediaPlayerDataViewModel.currentMediaItemIndex.value ?: 0
        mQueueMusicActionsWorkerViewModel.addSongsToQueueMusic(
            modelTypeInfo[0],
            AddSongsToQueueMusicWorker.ADD_METHOD_ADD_AT_POSITION,
            currentPlayingIndex,
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }
    private fun addSelectionToTopOfQueueMusic() {
        val modelTypeInfo = FragmentsCommunication.getModelTypeInfo(mMainFragmentViewModel)
        val dataList = mMainFragmentViewModel.selectedItems.value ?: return
        mQueueMusicActionsWorkerViewModel.addSongsToQueueMusic(
            modelTypeInfo[0],
            AddSongsToQueueMusicWorker.ADD_METHOD_ADD_TO_TOP,
            0,
            dataList.values,
            modelTypeInfo[1],
            modelTypeInfo[2]
        )
    }

    private fun onPageChanged(position: Int) {
        mMainFragmentViewModel.totalCount.value = 0
        mMainFragmentViewModel.selectedItems.value = HashMap()
        mMainFragmentViewModel.selectMode.value = false
        updateUISelectMode(false)
        updateUIAppbarTitle(position)
    }

    private fun onSearchButtonClicked() {
        //
    }

    private fun shareSelectedFiles() {
        //
    }

    private fun shareScreenShootForSelectedUniqueItem() {
        //
    }

    private fun updateUIAppbarTitle(position: Int) {
        when (position) {
            0 -> mDataBinding.topAppBar.title = getString(R.string.songs)
            1 -> mDataBinding.topAppBar.title = getString(R.string.albums)
            2 -> mDataBinding.topAppBar.title = getString(R.string.artists)
            3 -> mDataBinding.topAppBar.title = getString(R.string.folders)
            4 -> mDataBinding.topAppBar.title = getString(R.string.genre)
            5 -> mDataBinding.topAppBar.title = getString(R.string.album_artists)
            6 -> mDataBinding.topAppBar.title = getString(R.string.composers)
            7 -> mDataBinding.topAppBar.title = getString(R.string.years)
        }
    }

    private fun setupTabLayoutAdapter() {
        mTabLayoutAdapter = TabLayoutAdapter(this)
        mDataBinding.viewPager.adapter = mTabLayoutAdapter
        mDataBinding.viewPager.offscreenPageLimit = 8
        TabLayoutMediator(mDataBinding.tabLayout, mDataBinding.viewPager) { tab, position ->
            updateUIToolbarTitle(position, tab)
        }.attach()
        mDataBinding.viewPager.currentItem = 0
    }
    private fun updateUIToolbarTitle(position: Int, tab: TabLayout.Tab) {
        when (position) {
            0 -> tab.text = getString(R.string.songs)
            1 -> tab.text = getString(R.string.albums)
            2 -> tab.text = getString(R.string.artists)
            3 -> tab.text = getString(R.string.folders)
            4 -> tab.text = getString(R.string.genre)
            5 -> tab.text = getString(R.string.album_artists)
            6 -> tab.text = getString(R.string.composers)
            7 -> tab.text = getString(R.string.years)
        }
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBinding.coordinatorLayout)
    }

    companion object {
        const val TAG = "MusicLibraryFragment"

        @JvmStatic
        fun newInstance() =
            MusicLibraryFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}