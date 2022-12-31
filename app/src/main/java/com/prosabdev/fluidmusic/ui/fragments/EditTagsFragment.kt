package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEditTagsBinding

class EditTagsFragment : Fragment() {

    private var mDataBidingView: FragmentEditTagsBinding? = null

    private var mDataList: List<String>? = null
    private var mModelType: String? = null
    private var mModelTypeValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_tags, container, false)

        initViews()
        return mDataBidingView?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSongInfoData()
        observeLiveData()
        checkInteractions()
    }

    private fun checkInteractions() {
    }

    private fun observeLiveData() {
    }

    private fun setupSongInfoData() {
    }

    private fun initViews() {
        mDataBidingView?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(dataBidingView.coordinatorLayout)
            com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(dataBidingView.constraintNestedScrollView)
        }
    }

    companion object {
        const val TAG: String = "EditTagsFragment"

        @JvmStatic
        fun newInstance(dataList: List<String>?, modelType: String?, modelTypeValue: String?) =
            EditTagsFragment().apply {
                mDataList = dataList
                mModelType = modelType
                mModelTypeValue = modelTypeValue
            }
    }
}