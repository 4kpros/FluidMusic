package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
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
import com.prosabdev.fluidmusic.databinding.FragmentMusicLibraryBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.ViewAnimatorsUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicLibraryFragment : Fragment() {
    private var mFragmentMusicLibraryBinding: FragmentMusicLibraryBinding? = null

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayoutViewPagerAdapter()
        checkInteractions()
        observeLiveData()
    }

    private fun setupTabLayoutViewPagerAdapter() {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            mTabLayoutAdapter = TabLayoutAdapter(this)
            fragmentMusicLibraryBinding.viewPagerMainExplore.adapter = mTabLayoutAdapter
            fragmentMusicLibraryBinding.viewPagerMainExplore.offscreenPageLimit = 5
            TabLayoutMediator(
                fragmentMusicLibraryBinding.tabLayout,
                fragmentMusicLibraryBinding.viewPagerMainExplore
            ) { tab, position ->
                applyToolBarTitle(position, tab)
            }.attach()
            fragmentMusicLibraryBinding.viewPagerMainExplore.currentItem =
                mMainFragmentViewModel.getActivePage().value ?: 0
            applyAppBarTitle(mMainFragmentViewModel.getActivePage().value ?: 0)
        }
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
        mMainFragmentViewModel.getSelectMode().observe(viewLifecycleOwner) {
            updateSelectModeUI(it)
        }
        mMainFragmentViewModel.getTotalSelected().observe(viewLifecycleOwner
        ) { totalSelected -> updateTotalSelectedItemsUI(totalSelected ?: 0) }
    }
    private  fun updateTotalSelectedItemsUI(totalSelected : Int, animate : Boolean = true){
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            if (totalSelected > 0 && fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != GONE)
                ViewAnimatorsUtils.crossFadeDown(fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200)
            else if(totalSelected <= 0 && fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != VISIBLE)
                ViewAnimatorsUtils.crossFadeUp(fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200, 0.8f)
        }
    }
    private  fun updateSelectModeUI(isSelectMode: Boolean, animate : Boolean = true){
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            if (isSelectMode) {
                fragmentMusicLibraryBinding.viewPagerMainExplore.isUserInputEnabled = false
                if (fragmentMusicLibraryBinding.constraintSideMenuHoverContainer.visibility != VISIBLE)
                    ViewAnimatorsUtils.crossTranslateInFromHorizontal(
                        fragmentMusicLibraryBinding.constraintSideMenuHoverContainer as View,
                        1,
                        animate,
                        300
                    )
//                fragmentMusicLibraryBinding.tabLayout.visibility = GONE
            } else {
                fragmentMusicLibraryBinding.viewPagerMainExplore.isUserInputEnabled = true
                if (fragmentMusicLibraryBinding.constraintSideMenuHoverContainer.visibility != GONE)
                    ViewAnimatorsUtils.crossTranslateOutFromHorizontal(
                        fragmentMusicLibraryBinding.constraintSideMenuHoverContainer as View,
                        1,
                        animate,
                        300
                    )
//                fragmentMusicLibraryBinding.tabLayout.visibility = VISIBLE
            }
        }
    }

    private fun checkInteractions() {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            fragmentMusicLibraryBinding.topAppBar.setNavigationOnClickListener {
                mMainFragmentViewModel.setShowDrawerMenuCounter()
            }
            fragmentMusicLibraryBinding.viewPagerMainExplore.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    applyAppBarTitle(position)
                    mMainFragmentViewModel.setSelectMode(false)
                    mMainFragmentViewModel.setTotalSelected(0)
                    mMainFragmentViewModel.setActivePage(position)
                    updateSelectModeUI(false)
                }
            })
            fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonPlayAfter.setOnClickListener {
                addToPlayAfter()
            }
            fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonQueueMusicAdd.setOnClickListener {
                addSelectionToQueueMusicDialog()
            }
            fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonPlaylistAdd.setOnClickListener {
                openAddSelectionToPlaylistDialog()
            }
            fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonShare.setOnClickListener {
                openShareSelectionDialog()
            }
            fragmentMusicLibraryBinding.constraintSideMenuHoverInclude.buttonDelete.setOnClickListener {
                openDeleteSelectionDialog()
            }
        }
    }
    private fun openShareSelectionDialog() {
        Toast.makeText(this.requireContext(), "On share selection", Toast.LENGTH_SHORT).show()
    }
    private fun openAddSelectionToPlaylistDialog() {
        Toast.makeText(this.requireContext(), "On add selection to playlist", Toast.LENGTH_SHORT).show()
    }
    private fun addSelectionToQueueMusicDialog() {
        Toast.makeText(this.requireContext(), "On add selection to queue music", Toast.LENGTH_SHORT).show()
    }
    private fun openDeleteSelectionDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(resources.getString(R.string.dialog_delete_selection_title))
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
        Toast.makeText(this.requireContext(), "On delete selection", Toast.LENGTH_SHORT).show()
    }
    private fun addToPlayAfter() {
        Toast.makeText(this.requireContext(), "On add to play after selection", Toast.LENGTH_SHORT)
            .show()
    }

    private fun applyAppBarTitle(position: Int) {
        mFragmentMusicLibraryBinding?.let { fragmentMusicLibraryBinding ->
            MainScope().launch {
                when (position) {
                    0 -> {
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.songs)
                    }
                    1 -> {
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.folders)
                    }
                    2 -> {
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.albums)
                    }
                    3 -> {
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.artists)
                    }
                    4 -> {
                        fragmentMusicLibraryBinding.topAppBar.title = getString(R.string.genre)
                    }
                }
            }
        }
    }

    private fun initViews() {
    }

    companion object {
        @JvmStatic
        fun newInstance(exploreContent: Int = 0) =
            MusicLibraryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.ARGS_EXPLORE_MUSIC_LIBRARY, exploreContent)
                }
            }
    }
}