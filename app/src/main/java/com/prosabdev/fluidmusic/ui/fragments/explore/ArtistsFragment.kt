package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.l4digital.fastscroll.FastScroller
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.CustomGridItemDecoration
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.ArtistItemListAdapter
import com.prosabdev.fluidmusic.adapters.generic.SelectableItemListAdapter
import com.prosabdev.fluidmusic.databinding.FragmentArtistsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.ArtistsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentArtistsBinding: FragmentArtistsBinding

    private val mArtistsFragmentViewModel: ArtistsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mArtistItemListAdapter: ArtistItemListAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_ARTISTS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentArtistsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_artists, container,false)
        val view = mFragmentArtistsBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            setupRecyclerViewAdapter()
            observeLiveData()
            checkInteractions()
        }
    }

    private fun checkInteractions() {
        //
    }

    private fun observeLiveData() {
        mArtistsFragmentViewModel.getAll().observe(viewLifecycleOwner){
            addDataToAdapter(it)
        }
    }
    private fun addDataToAdapter(albumList: ArrayList<Any>?) {
        mArtistItemListAdapter?.submitList(albumList)
        if(mMainFragmentViewModel.getCurrentSelectablePage().value == ConstantValues.EXPLORE_ALBUMS){
            mMainFragmentViewModel.setTotalCount(albumList?.size ?: 0)
        }
    }

    private suspend fun setupRecyclerViewAdapter() {
        withContext(Dispatchers.Default){
            val ctx : Context = context ?: return@withContext
            val spanCount = 2
            //Setup headline adapter
            val listHeadlines : ArrayList<Int> = ArrayList<Int>()
            listHeadlines.add(0)
            mHeadlineTopPlayShuffleAdapter = HeadlinePlayShuffleAdapter(listHeadlines, object : HeadlinePlayShuffleAdapter.OnItemClickListener{
                override fun onPlayButtonClicked() {
                    //
                }
                override fun onShuffleButtonClicked() {
                    //
                }
                override fun onSortButtonClicked() {
                    //
                }
                override fun onOrganizeButtonClicked() {
                    //
                }
            })

            //Setup content adapter
            mArtistItemListAdapter = ArtistItemListAdapter(
                ctx,
                object : ArtistItemListAdapter.OnItemClickListener{
                    override fun onItemClicked(position: Int) {
                        //
                    }

                    override fun onItemLongClicked(position: Int) {
                        //
                    }
                },
                object : SelectableItemListAdapter.OnSelectSelectableItemListener{
                    override fun onSelectModeChange(selectMode: Boolean) {
                        //
                    }

                    override fun onTotalSelectedItemChange(totalSelected: Int) {
                        //
                    }
                }
            )

            //Setup empty bottom space adapter
            val listEmptyBottomSpace : ArrayList<String> = ArrayList()
            listEmptyBottomSpace.add("")
            mEmptyBottomAdapter = EmptyBottomAdapter(listEmptyBottomSpace)

            //Setup concat adapter
            val concatAdapter = ConcatAdapter()
            mHeadlineTopPlayShuffleAdapter?.let {
                concatAdapter.addAdapter(it)
            }
            mArtistItemListAdapter?.let {
                concatAdapter.addAdapter(it)
            }
            mEmptyBottomAdapter?.let {
                concatAdapter.addAdapter(it)
            }

            //Add Layout manager
            mLayoutManager = GridLayoutManager(ctx, spanCount, GridLayoutManager.VERTICAL, false)
            mLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0 -> spanCount
                        ((mLayoutManager?.itemCount ?: 0) - 1) -> spanCount
                        else -> 1
                    }
                }
            }
            MainScope().launch {
                mFragmentArtistsBinding.recyclerView.adapter = concatAdapter
                mFragmentArtistsBinding.recyclerView.layoutManager = mLayoutManager
                context?.let { ctx ->
                    mFragmentArtistsBinding.recyclerView.addItemDecoration(CustomGridItemDecoration(ctx, spanCount, false))
                }

                mFragmentArtistsBinding.fastScroller.setSectionIndexer(mArtistItemListAdapter)
                mFragmentArtistsBinding.fastScroller.attachRecyclerView(mFragmentArtistsBinding.recyclerView)
                mFragmentArtistsBinding.fastScroller.setFastScrollListener(object :
                    FastScroller.FastScrollListener {
                    override fun onFastScrollStart(fastScroller: FastScroller) {
                        mMainFragmentViewModel.setIsFastScrolling(true)
                        println("FAST SCROLLING STARTED")
                    }

                    override fun onFastScrollStop(fastScroller: FastScroller) {
                        mMainFragmentViewModel.setIsFastScrolling(false)
                        println("FAST SCROLLING STOPED")
                    }

                })
            }
        }
    }

    private fun initViews() {
        //
    }

    companion object {
        const val TAG = "ArtistsFragment"

        @JvmStatic
        fun newInstance(pageIndex: Int) =
            ArtistsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_ARTISTS, pageIndex)
                }
            }
    }
}