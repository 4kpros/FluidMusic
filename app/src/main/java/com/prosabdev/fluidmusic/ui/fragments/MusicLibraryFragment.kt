package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.databinding.FragmentMusicLibraryBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicLibraryFragment : Fragment() {
    private lateinit var mFragmentMusicLibraryBinding: FragmentMusicLibraryBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mMainFragmentViewModel: MainFragmentViewModel

    private var mTabLayoutAdapter: TabLayoutAdapter? = null

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
        mFragmentMusicLibraryBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_music_library,container,false)
        val view = mFragmentMusicLibraryBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            setupTabLayoutViewPagerAdapter()
            checkInteractions()
        }
        observeLiveData()
    }

    private fun setupTabLayoutViewPagerAdapter() {
        mTabLayoutAdapter = TabLayoutAdapter(this)
        mFragmentMusicLibraryBinding.viewPagerMainExplore.adapter = mTabLayoutAdapter
        mFragmentMusicLibraryBinding.viewPagerMainExplore.offscreenPageLimit = 5
        TabLayoutMediator(mFragmentMusicLibraryBinding.tabLayout, mFragmentMusicLibraryBinding.viewPagerMainExplore){tab,position->
            applyToolBarTitle(position, tab)
        }.attach()
        mFragmentMusicLibraryBinding.viewPagerMainExplore.currentItem = mMainFragmentViewModel.getActivePage().value ?: 0
        applyAppBarTitle(mMainFragmentViewModel.getActivePage().value ?: 0)
    }
    private fun applyToolBarTitle(position: Int, tab: TabLayout.Tab) {
        when(position){
            0->{
                tab.text = getString(R.string.songs)
            }
            1->{
                tab.text = getString(R.string.folders)
            }
            2->{
                tab.text = getString(R.string.albums)
            }
            3->{
                tab.text = getString(R.string.artists)
            }
            4->{
                tab.text = getString(R.string.genre)
            }
        }
    }

    private fun observeLiveData() {
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { selectMode -> updateSelectModeUI(selectMode ?: false) }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner
        ) { totalSelected -> updateTotalSelectedItemsUI(totalSelected ?: 0) }
    }
    private  fun updateTotalSelectedItemsUI(totalSelected : Int, animate : Boolean = true){
        if (totalSelected > 0 && mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != GONE)
            CustomAnimators.crossFadeDown(mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200)
        else if(totalSelected <= 0 && mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != VISIBLE)
            CustomAnimators.crossFadeUp(mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200, 0.8f)
    }
    private  fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true){
        if (selectMode) {
            mFragmentMusicLibraryBinding.viewPagerMainExplore.isUserInputEnabled = false
            if(mFragmentMusicLibraryBinding.constraintSideMenuHoverContainer.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromHorizontal(mFragmentMusicLibraryBinding.constraintSideMenuHoverContainer as View, 1, animate, 300)
        }else {
            mFragmentMusicLibraryBinding.viewPagerMainExplore.isUserInputEnabled = true
            if(mFragmentMusicLibraryBinding.constraintSideMenuHoverContainer.visibility != GONE)
                CustomAnimators.crossTranslateOutFromHorizontal(mFragmentMusicLibraryBinding.constraintSideMenuHoverContainer as View, 1, animate, 300)
        }
    }

    private suspend fun checkInteractions() {
        mFragmentMusicLibraryBinding.topAppBar.setNavigationOnClickListener {
            mMainFragmentViewModel.setOnActionBarClickListened(true)
        }
        mFragmentMusicLibraryBinding.viewPagerMainExplore.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                applyAppBarTitle(position)
                mMainFragmentViewModel.setActivePage(position)
                updateSelectModeUI(false)
            }
        })
        mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonPlayAfter.setOnClickListener {
            onAddToPlayAfter()
        }
        mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonQueueMusicAdd.setOnClickListener {
            onAddSelectionToQueueMusicDialog()
        }
        mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonPlaylistAdd.setOnClickListener {
            onShowAddSelectionToPlaylistDialog()
        }
        mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonShare.setOnClickListener {
            onShareSelectionDialog()
        }
        mFragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonDelete.setOnClickListener {
            onShowDeleteSelectionDialog()
        }
    }
    private fun onShareSelectionDialog() {
        Toast.makeText(mContext, "On share selection", Toast.LENGTH_SHORT).show()
    }
    private fun onShowAddSelectionToPlaylistDialog() {
        Toast.makeText(mContext, "On add selection to playlist", Toast.LENGTH_SHORT).show()
    }
    private fun onAddSelectionToQueueMusicDialog() {
        Toast.makeText(mContext, "On add selection to queue music", Toast.LENGTH_SHORT).show()
    }
    private fun onShowDeleteSelectionDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle(resources.getString(R.string.dialog_delete_selection_title))
            .setMessage(resources.getString(R.string.dialog_delete_selection_description))
            .setNeutralButton(resources.getString(R.string.delete_files)) { dialog, which ->
                onDeleteForeverFilesSelection()
            }
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.delete)) { dialog, which ->
                onDeleteSelection()
            }
            .show()
    }
    private fun onAddToPlayAfter() {
        Toast.makeText(mContext, "On add to play after selection", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteForeverFilesSelection() {
        Toast.makeText(mContext, "On delete forever files selection", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteSelection() {
        Toast.makeText(mContext, "On delete selection", Toast.LENGTH_SHORT).show()
    }

    private fun applyAppBarTitle(position: Int) {
        when(position){
            0->{
                mFragmentMusicLibraryBinding.topAppBar.title = getString(R.string.songs)
            }
            1->{
                mFragmentMusicLibraryBinding.topAppBar.title = getString(R.string.folders)
            }
            2->{
                mFragmentMusicLibraryBinding.topAppBar.title = getString(R.string.albums)
            }
            3->{
                mFragmentMusicLibraryBinding.topAppBar.title = getString(R.string.artists)
            }
            4->{
                mFragmentMusicLibraryBinding.topAppBar.title = getString(R.string.genre)
            }
        }
    }

    private fun initViews() {
        mMainFragmentViewModel = MainFragmentViewModelFactory().create(MainFragmentViewModel::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(exploreContent: Int = 0) =
            MusicLibraryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.ARGS_EXPLORE_CONTENT, exploreContent)
                }
            }
    }
}