package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetSortItemsBinding

class SortItemsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mBottomSheetSortItemsBinding: BottomSheetSortItemsBinding

    private var mFromSource: String? = null
    private var mFromSourceValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetSortItemsBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_sort_items, container, false)
        val view = mBottomSheetSortItemsBinding.root

        initViews()
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkInteractions() {
        //
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "SortItemsBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance(fromSource: String?, fromSourceValue: String? = null) =
            SortItemsBottomSheetDialogFragment().apply {
                mFromSource = fromSource
                mFromSourceValue = fromSourceValue
            }
    }
}