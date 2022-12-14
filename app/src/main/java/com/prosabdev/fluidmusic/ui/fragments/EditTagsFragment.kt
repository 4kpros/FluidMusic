package com.prosabdev.fluidmusic.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentEditTagsBinding
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils

class EditTagsFragment : Fragment() {

    private var mFragmentEditTagsBinding: FragmentEditTagsBinding? = null

    private var mDataList: List<String>? = null
    private var mModelType: String? = null
    private var mModelTypeValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()

        arguments?.let {
            mDataList = it.getStringArrayList(DATA_LIST)
            mModelType = it.getString(MODEL_TYPE)
            mModelTypeValue = it.getString(MODEL_TYPE_VALUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentEditTagsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_tags, container, false)

        initViews()
        setupSongInfoData()
        return mFragmentEditTagsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        mFragmentEditTagsBinding?.let { dataBidingView ->
            InsetModifiersUtils.updateTopViewInsets(dataBidingView.relativeTopApp)
            InsetModifiersUtils.updateBottomViewInsets(dataBidingView.container)
        }
    }

    companion object {
        const val TAG: String = "EditTagsFragment"

        const val DATA_LIST: String = "DATA_LIST"
        const val MODEL_TYPE: String = "MODEL_TYPE"
        const val MODEL_TYPE_VALUE: String = "MODEL_TYPE_VALUE"

        @JvmStatic
        fun newInstance(dataList: List<String>?, modelType: String?, modelTypeValue: String?) =
            EditTagsFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(DATA_LIST, dataList as ArrayList<String>?)
                    putString(MODEL_TYPE, modelType)
                    putString(MODEL_TYPE_VALUE, modelTypeValue)
                }
            }
    }
}