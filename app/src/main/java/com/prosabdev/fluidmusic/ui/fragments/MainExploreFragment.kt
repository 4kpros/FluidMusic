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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.databinding.FragmentMainExploreBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.MainFragmentViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainExploreFragment : Fragment() {
    private lateinit var mFragmentMainExploreBinding: FragmentMainExploreBinding

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
        mFragmentMainExploreBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_explore,container,false)
        val view = mFragmentMainExploreBinding.root

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
        mFragmentMainExploreBinding.viewPagerMainExplore.adapter = mTabLayoutAdapter
        mFragmentMainExploreBinding.viewPagerMainExplore.offscreenPageLimit = 5
        TabLayoutMediator(mFragmentMainExploreBinding.tabLayout, mFragmentMainExploreBinding.viewPagerMainExplore){tab,position->
            applyToolBarTitle(position, tab)
        }.attach()
        mFragmentMainExploreBinding.viewPagerMainExplore.currentItem = mMainFragmentViewModel.getActivePage().value ?: 0
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
        if (totalSelected > 0 && mFragmentMainExploreBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != GONE)
            CustomAnimators.crossFadeDown(mFragmentMainExploreBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200)
        else if(totalSelected <= 0 && mFragmentMainExploreBinding.constraintSideMenuHoverInclude.constraintSideMenuHover.visibility != VISIBLE)
            CustomAnimators.crossFadeUp(mFragmentMainExploreBinding.constraintSideMenuHoverInclude.constraintSideMenuHover, animate, 200, 0.8f)
    }
    private  fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true){
        if (selectMode) {
            mFragmentMainExploreBinding.viewPagerMainExplore.isUserInputEnabled = false
            if(mFragmentMainExploreBinding.constraintSideMenuHoverContainer.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromHorizontal(mFragmentMainExploreBinding.constraintSideMenuHoverContainer as View, 1, animate, 300)
        }else {
            mFragmentMainExploreBinding.viewPagerMainExplore.isUserInputEnabled = true
            if(mFragmentMainExploreBinding.constraintSideMenuHoverContainer.visibility != GONE)
                CustomAnimators.crossTranslateOutFromHorizontal(mFragmentMainExploreBinding.constraintSideMenuHoverContainer as View, 1, animate, 300)
        }
    }

    private suspend fun checkInteractions() {
        mFragmentMainExploreBinding.topAppBar.setNavigationOnClickListener {
            mMainFragmentViewModel.setOnActionBarClickListened(true)
        }
        mFragmentMainExploreBinding.viewPagerMainExplore.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                applyAppBarTitle(position)
                mMainFragmentViewModel.setActivePage(position)
                updateSelectModeUI(false)
            }
        })
        mFragmentMainExploreBinding.constraintSideMenuHoverInclude.buttonPlayAfter.setOnClickListener {
            onAddToPlayAfter()
        }
        mFragmentMainExploreBinding.constraintSideMenuHoverInclude.buttonQueueMusicAdd.setOnClickListener {
            onAddSelectionToQueueMusicDialog()
        }
        mFragmentMainExploreBinding.constraintSideMenuHoverInclude.buttonPlaylistAdd.setOnClickListener {
            onShowAddSelectionToPlaylistDialog()
        }
        mFragmentMainExploreBinding.constraintSideMenuHoverInclude.buttonShare.setOnClickListener {
            onShareSelectionDialog()
        }
        mFragmentMainExploreBinding.constraintSideMenuHoverInclude.buttonDelete.setOnClickListener {
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
                mFragmentMainExploreBinding.topAppBar.title = getString(R.string.songs)
            }
            1->{
                mFragmentMainExploreBinding.topAppBar.title = getString(R.string.folders)
            }
            2->{
                mFragmentMainExploreBinding.topAppBar.title = getString(R.string.albums)
            }
            3->{
                mFragmentMainExploreBinding.topAppBar.title = getString(R.string.artists)
            }
            4->{
                mFragmentMainExploreBinding.topAppBar.title = getString(R.string.genre)
            }
        }
    }

    private fun initViews() {
        mMainFragmentViewModel = MainFragmentViewModelFactory().create(MainFragmentViewModel::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(exploreContent: Int = 0) =
            MainExploreFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.ARGS_EXPLORE_CONTENT, exploreContent)
                }
            }
    }
}