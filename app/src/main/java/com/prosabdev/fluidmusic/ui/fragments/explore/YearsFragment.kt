package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.adapters.explore.YearItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentYearsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.views.explore.YearItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class YearsFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentYearsBinding: FragmentYearsBinding

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private lateinit var mYearItemViewModel: YearItemViewModel

    private var mEmptyBottomSpaceAdapter: HeadlinePlayShuffleAdapter? = null
    private var mYearItemAdapter: YearItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_YEARS)
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentYearsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_years, container,false)
        val view = mFragmentYearsBinding.root

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

    }

    private fun observeLiveData() {

    }

    private fun setupRecyclerViewAdapter() {
    }

    private fun initViews() {
    }

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int) =
            YearsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_YEARS, pageIndex)
                }
            }
    }
}