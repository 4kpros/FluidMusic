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

    private var mDataBiding: FragmentEditTagsBinding? = null

    private var mDataList: List<String>? = null
    private var mModelType: String? = null
    private var mModelTypeValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply transition animation
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_tags, container,false)
        val view = mDataBiding?.root

        //Load your UI content
        initViews()
        setupSongInfoData()
        observeLiveData()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
    }

    private fun observeLiveData() {
    }

    private fun setupSongInfoData() {
    }

    private fun initViews() {
        mDataBiding?.let { dataBidingView ->
            com.prosabdev.common.utils.InsetModifiers.updateTopViewInsets(dataBidingView.coordinatorLayout)
            com.prosabdev.common.utils.InsetModifiers.updateBottomViewInsets(dataBidingView.constraintNestedScrollView)
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