package com.prosabdev.fluidmusic.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
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
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel

class MainExploreFragment : Fragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_main_explore, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        initViews(view)
        setupViewPagerAdapter(view)
        checkInteractions(view)
        observeLiveData(view)

        return view
    }

    private fun setupViewPagerAdapter(view: View) {
        //Setup adapter
        mTabLayoutAdapter = TabLayoutAdapter(this)
        mViewPager.adapter = mTabLayoutAdapter

        //Setup tab layout mediator
        TabLayoutMediator(mTabLayout, mViewPager){tab,position->
            applyToolBarTitle(position, tab)
        }.attach()
        mViewPager.currentItem = mMainExploreFragmentViewModel.getActivePage().value ?: 0
        applyAppBarTitle(mMainExploreFragmentViewModel.getActivePage().value ?: 0)
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

    //Method to listen all view models
    private fun observeLiveData(view: View) {
        mMainExploreFragmentViewModel.getSelectMode().observe(mActivity as LifecycleOwner, object : Observer<Boolean>{
            override fun onChanged(selectMode: Boolean?) {
                updateSelectedItems(selectMode ?: false, mMainExploreFragmentViewModel.getTotalSelected().value ?: 0, mMainExploreFragmentViewModel.getTotalCount().value ?: 0, true)
            }
        })
        mMainExploreFragmentViewModel.getTotalSelected().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(totalSelected: Int?) {
                if(mMainExploreFragmentViewModel.getSelectMode().value == true){
                    updateSelectedItems(mMainExploreFragmentViewModel.getSelectMode().value ?: false, totalSelected ?: 0, mMainExploreFragmentViewModel.getTotalCount().value ?: 0)
                }
            }
        })
    }
    private  fun updateSelectedItems(selectMode : Boolean, totalSelected: Int, totalCount : Int, animate : Boolean = false){
        mTextSelectedCount.text = "$totalSelected/$totalCount"
        if(totalSelected > 0 && totalSelected >= totalCount)
            mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
        else
            mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)

        //Update UI
        if (selectMode){
            if(totalCount > 0 && totalSelected == totalCount)
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
            else
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)
            //Show selection mode
            if(animate)
                CustomAnimators.crossFadeUp(mSelectionPanelEditor, true)
        }else{
            if(totalSelected > 0 && totalSelected >= totalCount)
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.deselect)
            else
                mButtonSelectAll.icon = ContextCompat.getDrawable(mContext!!, R.drawable.select_all)
            //Hide selection mode
            if(animate)
                CustomAnimators.crossFadeDown(mSelectionPanelEditor, true)
        }
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
                mMainExploreFragmentViewModel.setSelectMode(false)
                mMainExploreFragmentViewModel.setTotalSelected(0)
            }

        })
        mButtonSelectAll.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                if((mMainExploreFragmentViewModel.getTotalCount().value ?: 0) > 0 && (mMainExploreFragmentViewModel.getTotalSelected().value ?: 0) < (mMainExploreFragmentViewModel.getTotalCount().value ?: 0)){
                    mMainExploreFragmentViewModel.setTotalSelected(mMainExploreFragmentViewModel.getTotalCount().value ?: 0)
                }else{
                    mMainExploreFragmentViewModel.setTotalSelected(0)
                }
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
}