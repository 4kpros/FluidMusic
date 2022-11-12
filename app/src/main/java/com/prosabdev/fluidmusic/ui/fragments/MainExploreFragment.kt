package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.viewmodels.MainFragmentViewModel
import kotlinx.coroutines.NonCancellable.cancel

class MainExploreFragment : Fragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mTabLayout: TabLayout
    private lateinit var mTopAppBar: Toolbar
    private lateinit var mTabLayoutAdapter: TabLayoutAdapter
    private lateinit var mViewPager: ViewPager2
    private lateinit var mSelectionRightSelectionMenu: LinearLayoutCompat
    private lateinit var mConstraintSelectMenuHover: ConstraintLayout

    private lateinit var mButtonPlayAfter: MaterialButton
    private lateinit var mButtonQueueMusicAdd: MaterialButton
    private lateinit var mButtonPlaylistAdd: MaterialButton
    private lateinit var mButtonShare: MaterialButton
    private lateinit var mButtonDelete: MaterialButton

    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()

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
        val view: View = inflater.inflate(R.layout.fragment_main_explore, container, false)

        initViews(view)
        setupTabLayoutViewPagerAdapter(view)
        checkInteractions(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData(view)
    }

    private fun setupTabLayoutViewPagerAdapter(view: View) {
        mTabLayoutAdapter = TabLayoutAdapter(this)
        mViewPager.adapter = mTabLayoutAdapter
        mViewPager.offscreenPageLimit = 5
        TabLayoutMediator(mTabLayout, mViewPager){tab,position->
            applyToolBarTitle(position, tab)
        }.attach()
        mViewPager.currentItem = mMainFragmentViewModel.getActivePage().value ?: 0
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

    private fun observeLiveData(view: View) {
        mMainFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner
        ) { selectMode -> updateSelectModeUI(selectMode ?: false) }
        mMainFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner
        ) { totalSelected -> updateTotalSelectedItemsUI(totalSelected ?: 0) }
    }
    private  fun updateTotalSelectedItemsUI(totalSelected : Int, animate : Boolean = true){
        if (totalSelected > 0 && mConstraintSelectMenuHover.visibility != GONE)
            CustomAnimators.crossFadeDown(mConstraintSelectMenuHover, animate, 200)
        else if(totalSelected <= 0 && mConstraintSelectMenuHover.visibility != VISIBLE)
            CustomAnimators.crossFadeUp(mConstraintSelectMenuHover, animate, 200, 0.8f)
    }
    private  fun updateSelectModeUI(selectMode : Boolean, animate : Boolean = true){
        if (selectMode) {
            mViewPager.isUserInputEnabled = false
            if(mSelectionRightSelectionMenu.visibility != VISIBLE)
                CustomAnimators.crossTranslateInFromHorizontal(mSelectionRightSelectionMenu as View, 1, animate, 300)
        }else {
            mViewPager.isUserInputEnabled = true
            if(mSelectionRightSelectionMenu.visibility != GONE)
                CustomAnimators.crossTranslateOutFromHorizontal(mSelectionRightSelectionMenu as View, 1, animate, 300)
        }
    }

    private fun checkInteractions(view: View) {
        mTopAppBar.setNavigationOnClickListener {
            mMainFragmentViewModel.setOnActionBarClickListened(true)
        }
        mViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                applyAppBarTitle(position)
                mMainFragmentViewModel.setActivePage(position)
                updateSelectModeUI(false)
            }
        })
        mButtonPlayAfter.setOnClickListener {
            onAddToPlayAfter()
        }
        mButtonQueueMusicAdd.setOnClickListener {
            onAddSelectionToQueueMusicDialog()
        }
        mButtonPlaylistAdd.setOnClickListener {
            onShowAddSelectionToPlaylistDialog()
        }
        mButtonShare.setOnClickListener {
            onShareSelectionDialog()
        }
        mButtonDelete.setOnClickListener {
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
        if(mContext == null)
            return
        MaterialAlertDialogBuilder(mContext!!)
            .setTitle(resources.getString(R.string.dialog_delete_selection_title))
            .setMessage(resources.getString(R.string.dialog_delete_selection_description))
//            .setNeutralButton(resources.getString(R.string.delete_files)) { dialog, which ->
//                onDeleteForeverFilesSelection()
//            }
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
                mTopAppBar.title = getString(R.string.songs)
            }
            1->{
                mTopAppBar.title = getString(R.string.folders)
            }
            2->{
                mTopAppBar.title = getString(R.string.albums)
            }
            3->{
                mTopAppBar.title = getString(R.string.artists)
            }
            4->{
                mTopAppBar.title = getString(R.string.genre)
            }
        }
    }

    private fun initViews(view: View) {
        mAppBarLayout = view.findViewById(R.id.app_bar_layout)
        mTopAppBar = view.findViewById(R.id.top_app_bar)
        mTabLayout = view.findViewById(R.id.tab_layout)
        mViewPager = view.findViewById(R.id.view_pager_main_explore)
        mSelectionRightSelectionMenu = view.findViewById<LinearLayoutCompat>(R.id.selection_panel_editor_container)
        mConstraintSelectMenuHover = view.findViewById<ConstraintLayout>(R.id.constraint_side_menu_hover)

        mButtonPlayAfter = view.findViewById<MaterialButton>(R.id.button_play_after)
        mButtonQueueMusicAdd = view.findViewById<MaterialButton>(R.id.button_queue_music_add)
        mButtonPlaylistAdd = view.findViewById<MaterialButton>(R.id.button_playlist_add)
        mButtonShare = view.findViewById<MaterialButton>(R.id.button_share)
        mButtonDelete = view.findViewById<MaterialButton>(R.id.button_delete)
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