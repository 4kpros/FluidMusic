package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.adapters.SelectableRecycleViewAdapter
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel

class MainExploreFragment : Fragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mExploreContentParam: Int = 0

    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mTabLayout: TabLayout
    private lateinit var mTopAppBar: Toolbar
    private lateinit var mTabLayoutAdapter: TabLayoutAdapter
    private lateinit var mViewPager: ViewPager2
    private lateinit var mSelectionPanelEditor: LinearLayoutCompat

    private lateinit var mTextSelectedCount: AppCompatTextView
    private lateinit var mButtonClose: MaterialCardView
    private lateinit var mButtonSelectAll: MaterialButton
    private lateinit var mButtonPlayAfter: MaterialButton
    private lateinit var mButtonQueueMusicAdd: MaterialButton
    private lateinit var mButtonPlaylistAdd: MaterialButton
    private lateinit var mButtonDelete: MaterialButton

    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mExploreContentParam = it.getInt(ConstantValues.ARGS_EXPLORE_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_main_explore, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupViewPagerAdapter(view)
        checkInteractions(view)
        observeLiveData(view)
    }

    private fun setupViewPagerAdapter(view: View) {
        //Setup adapter
        mTabLayoutAdapter = TabLayoutAdapter(this)
        mViewPager.adapter = mTabLayoutAdapter

        //Setup tab layout mediator
        TabLayoutMediator(mTabLayout, mViewPager){tab,position->
            applyToolBarTitle(position, tab)
        }.attach()
        mViewPager.currentItem = mExploreContentParam
        applyAppBarTitle(mExploreContentParam)
    }
    private fun applyToolBarTitle(position: Int, tab: TabLayout.Tab) {
        when(position){
            0->{
                tab.text = getString(R.string.folders)
            }
            1->{
                tab.text = getString(R.string.songs)
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

    //Method to listen all view models
    private fun observeLiveData(view: View) {
        mMainExploreFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner, object : Observer<Boolean>{
            override fun onChanged(selectMode: Boolean?) {
                Log.i(ConstantValues.TAG, "onSelectionModeChange, SELECT MODE = $selectMode, TOTAL = ${mMainExploreFragmentViewModel.getTotalSelected().value ?: 0}, TOTAL COUNT = ${mMainExploreFragmentViewModel.getTotalCount().value ?: 0}")
                updateSelectedItems(selectMode ?: false, mMainExploreFragmentViewModel.getTotalSelected().value ?: 0, mMainExploreFragmentViewModel.getTotalCount().value ?: 0, true)
            }

        })
        mMainExploreFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(totalSelected: Int?) {
                if(mMainExploreFragmentViewModel.getSelectMode().value == true){
                    Log.i(ConstantValues.TAG, "onSelectionModeChange, SELECT MODE = $mMainExploreFragmentViewModel.getSelectMode().value ?: false, TOTAL = ${mMainExploreFragmentViewModel.getTotalSelected().value ?: 0}, TOTAL COUNT = ${mMainExploreFragmentViewModel.getTotalCount().value ?: 0}")
                    updateSelectedItems(mMainExploreFragmentViewModel.getSelectMode().value ?: false, totalSelected ?: 0, mMainExploreFragmentViewModel.getTotalCount().value ?: 0)
                }
            }
        })
    }

    //Method to check all interactions
    private fun checkInteractions(view: View) {
        mTopAppBar.setNavigationOnClickListener {
            mMainExploreFragmentViewModel.setOnActionBarClickListened(true)
        }

        mViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                applyAppBarTitle(position)
                mMainExploreFragmentViewModel.setActivePage(position)
                //Hide selection mode
                CustomAnimators.crossFadeDown(mSelectionPanelEditor, true)
            }
        })
        mButtonClose.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonDelete", Toast.LENGTH_SHORT).show()
            }

        })
        mButtonSelectAll.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonSelectAll", Toast.LENGTH_SHORT).show()
            }

        })
        mButtonPlayAfter.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonPlayAfter", Toast.LENGTH_SHORT).show()
            }

        })
        mButtonQueueMusicAdd.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonQueueMusicAdd", Toast.LENGTH_SHORT).show()
            }

        })
        mButtonPlaylistAdd.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonPlaylistAdd", Toast.LENGTH_SHORT).show()
            }

        })
        mButtonDelete.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(context, "mButtonDelete", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun applyAppBarTitle(position: Int) {
        when(position){
            0->{
                mTopAppBar.title = getString(R.string.folders)
            }
            1->{
                mTopAppBar.title = getString(R.string.songs)
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

    //Init all views
    private fun initViews(view: View) {
        //Find views by IDs
        mAppBarLayout = view.findViewById(R.id.app_bar_layout)
        mTopAppBar = view.findViewById(R.id.top_app_bar)
        mTabLayout = view.findViewById(R.id.tab_layout)
        mViewPager = view.findViewById(R.id.view_pager_main_explore)
        mSelectionPanelEditor = view.findViewById<LinearLayoutCompat>(R.id.selection_panel_editor_container)

        mTextSelectedCount = view.findViewById<AppCompatTextView>(R.id.text_selected_count)

        mButtonClose = view.findViewById<MaterialCardView>(R.id.button_close)
        mButtonSelectAll = view.findViewById<MaterialButton>(R.id.button_select_all)
        mButtonPlayAfter = view.findViewById<MaterialButton>(R.id.button_play_after)
        mButtonQueueMusicAdd = view.findViewById<MaterialButton>(R.id.button_queue_music_add)
        mButtonPlaylistAdd = view.findViewById<MaterialButton>(R.id.button_playlist_add)
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

    private  fun updateSelectedItems(selectMode : Boolean, totalSelected: Int, totalCount : Int, animate : Boolean = false){

        mTextSelectedCount.text = "$totalSelected/$totalCount"
        if(totalSelected > 0 && totalSelected >= totalCount)
            mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
        else
            mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)

        //Update UI
        if (selectMode){
            if((mMainExploreFragmentViewModel.getTotalCount().value ?: 0) > 0 && (mMainExploreFragmentViewModel.getTotalSelected().value ?: 0) >= (mMainExploreFragmentViewModel.getTotalCount().value ?: 0))
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
            else
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)
            //Show selection mode
            if(animate)
                CustomAnimators.crossFadeUp(mSelectionPanelEditor, true)
        }else{
            if((mMainExploreFragmentViewModel.getTotalSelected().value ?: 0) > 0 && (mMainExploreFragmentViewModel.getTotalSelected().value ?: 0) >= (mMainExploreFragmentViewModel.getTotalCount().value ?: 0))
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
            else
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)
            //Hide selection mode
            if(animate)
                CustomAnimators.crossFadeDown(mSelectionPanelEditor, true)
        }
    }
}