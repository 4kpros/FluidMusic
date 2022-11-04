package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.TabLayoutAdapter
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel

class MainExploreFragment : Fragment() {

    private var mExploreContentParam: Int = 0

    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mTabLayout: TabLayout
    private lateinit var mTopAppBar: Toolbar
    private lateinit var mTabLayoutAdapter: TabLayoutAdapter
    private lateinit var mViewPager: ViewPager2
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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_explore, container, false)
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
        //
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(exploreContent: Int = 0) =
            MainExploreFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.ARGS_EXPLORE_CONTENT, exploreContent)
                }
            }
    }
}