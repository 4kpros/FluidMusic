package com.prosabdev.fluidmusic.ui.fragments.explore

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.SongItemAdapter
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.explore.AllSongsFragmentViewModel
import kotlinx.coroutines.*

class AllSongsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by activityViewModels()
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by activityViewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mSongItemAdapter: SongItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLoadingContentProgress: LinearProgressIndicator? = null

    private var mSongList : ArrayList<SongItem> = ArrayList<SongItem>()

    private var mShortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ALL_SONGS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_all_songs, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        runBlocking {
            launch {
                initViews(view)
                setupRecyclerViewAdapter(view)
                observeLiveData()
                checkInteractions(view)
            }
        }

        return view
    }

    private suspend fun observeLiveData() {
        hideLoadingAndRecyclerView()
        mAllSongsFragmentViewModel.requestLoadAsyncSongs(mActivity as Activity)
        //Listen when current page = this page, then try to load view data
        mMainExploreFragmentViewModel.getActivePage().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(page: Int?) {
                Log.i(ConstantValues.TAG, "Page $page !")
                if(page == mPageIndex){
                    if(mAllSongsFragmentViewModel.getFirstLoaded().value == false){
                        mAllSongsFragmentViewModel.setFirstLoaded(true)
                        showLoadingView()
                        Log.i(ConstantValues.TAG, "Start loading data for adapter !")
                        MainScope().launch { // launch new coroutine in background and continue
                            MainScope().launch {
                                var canLoadData = 0
                                mAllSongsFragmentViewModel.onDataReady().observe(mActivity as LifecycleOwner, object  : Observer<Boolean>{
                                    override fun onChanged(t: Boolean?) {
                                        canLoadData++
                                        if(canLoadData == 1){
                                            mAllSongsFragmentViewModel.loadMoreSongs()?.observe(mActivity as LifecycleOwner, object : Observer<ArrayList<SongItem>>{
                                                override fun onChanged(songList: ArrayList<SongItem>?) {
                                                    Log.i(ConstantValues.TAG, "Songs loaded : $songList !")
                                                    launch {
                                                        addSongsToAdapter(songList)
                                                    }
                                                }

                                            })
                                        }else{
                                            hideLoadingView()
                                        }
                                    }

                                })
                            }
                        }
                    }else{
                        hideLoadingView()
                    }
                }
            }

        })
    }

    private fun hideLoadingAndRecyclerView() {
        mRecyclerView?.visibility = GONE
        mLoadingContentProgress?.visibility = GONE
    }
    private fun showLoadingView() {
        mRecyclerView?.visibility = GONE
        mLoadingContentProgress?.visibility = VISIBLE
    }
    private fun hideLoadingView(animate : Boolean = false) {
        if(animate){
            crossFadeLoadingViewToContentView(mRecyclerView as View, mLoadingContentProgress as View)
        }else{
            mRecyclerView?.visibility = VISIBLE
            mLoadingContentProgress?.visibility = GONE
        }
    }
    private fun crossFadeLoadingViewToContentView(contentView : View, loadingView : View) {
        mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        //Animate content view to show
        contentView.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(mShortAnimationDuration.toLong())
                .setListener(null)
        }
        //Animate loading view to hide
        loadingView.animate()
            .alpha(0f)
            .setDuration(mShortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loadingView.visibility = View.GONE
                }
            })
    }

    private suspend fun addSongsToAdapter(songList: ArrayList<SongItem>?) = coroutineScope{
        if (songList != null) {
            val startPosition = mSongList.size -1
            mSongList.addAll(songList)
            mSongItemAdapter?.notifyItemRangeInserted(startPosition, mSongList.size-1)
        }
        delay(250L)
        MainScope().run {
            hideLoadingView(true)
        }
    }

    private fun checkInteractions(view: View) {

    }

    private fun setupRecyclerViewAdapter(view: View) {
        val spanCount = 1

        val listHeadlines : ArrayList<Long> = ArrayList<Long>()
        listHeadlines.add(1)

        //Setup headline adapter
        mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
            override fun onPlayButtonClicked() {
                Toast.makeText(mContext, "onPlayButtonClicked", Toast.LENGTH_SHORT).show()
            }
            override fun onShuffleButtonClicked() {
                Toast.makeText(mContext, "onShuffleButtonClicked", Toast.LENGTH_SHORT).show()
            }
        })
        //Setup song adapter
        mSongItemAdapter = SongItemAdapter(mSongList, mContext!!, object : SongItemAdapter.OnItemClickListener{
            override fun onSongItemClicked(position: Int) {
                Toast.makeText(mContext, "onSongItemClicked", Toast.LENGTH_SHORT).show()
            }

            override fun onSongItemPlayClicked(position: Int) {
                Toast.makeText(mContext, "onSongItemPlayClicked", Toast.LENGTH_SHORT).show()
            }

            override fun onSongItemLongClicked(position: Int) {
                Toast.makeText(mContext, "onSongItemLongClicked", Toast.LENGTH_SHORT).show()
            }

        })
        //Setup adapters with stable ID for concatenation
//        val config = ConcatAdapter.Config.Builder().setStableIdMode(ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS).build()
        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mHeadlineTopPlayShuffleAdapter!!)
        concatAdapter.addAdapter(mSongItemAdapter!!)
        mRecyclerView?.adapter = concatAdapter

        //Add Layout manager
        val layoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager
    }

    private fun initViews(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_all_songs)
        mLoadingContentProgress = view.findViewById<LinearProgressIndicator>(R.id.loading_content_progress)

    }

    companion object {
        @JvmStatic
        fun newInstance(page : Int) =
            AllSongsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ALL_SONGS, page)
                }
            }
    }
}