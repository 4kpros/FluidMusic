package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetDialogEditTagsBinding

class EditTagsBottomSheetDialogFragment : GenericFullBottomSheetDialogFragment() {

    private var mDataBidingView: BottomSheetDialogEditTagsBinding? = null

    private var mDataList: List<String>? = null
    private var mModelType: String? = null
    private var mModelTypeValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_edit_tags, container, false)
        val view = mDataBidingView?.root

        initViews()
        return view
    }

    private fun initViews() {
        //
    }

    fun updateData(
        dataList: List<String>?,
        modelType: String?,
        modelTypeValue: String?
    ){
        mDataList = dataList
        mModelType = modelType
        mModelTypeValue = modelTypeValue
    }

    companion object {
        const val TAG: String = "EditTagsFragment"

        @JvmStatic
        fun newInstance() =
            EditTagsBottomSheetDialogFragment().apply {
            }
    }
}