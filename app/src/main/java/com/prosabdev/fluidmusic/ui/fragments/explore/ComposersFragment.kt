package com.prosabdev.fluidmusic.ui.fragments.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.explore.ComposerItemAdapter
import com.prosabdev.fluidmusic.databinding.FragmentComposersBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.ComposerItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ComposersFragment : Fragment() {
    private var mPageIndex: Int? = -1

    private lateinit var mFragmentComposersBinding: FragmentComposersBinding

    private lateinit var mComposerItemViewModel: ComposerItemViewModel

    private var mComposerItemAdapter: ComposerItemAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageIndex = it.getInt(ConstantValues.EXPLORE_COMPOSERS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentComposersBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_composers, container,false)
        val view = mFragmentComposersBinding.root

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
        const val TAG = "ComposersFragment"

        @JvmStatic
        fun newInstance(pageIndex: Int) =
            ComposersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ConstantValues.EXPLORE_COMPOSERS, pageIndex)
                }
            }
    }
}