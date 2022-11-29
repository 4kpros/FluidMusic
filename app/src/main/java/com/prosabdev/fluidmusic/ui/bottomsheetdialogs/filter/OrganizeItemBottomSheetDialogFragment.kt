package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetOrganizeItemsBinding

class OrganizeItemBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mBottomSheetOrganizeItemsBinding: BottomSheetOrganizeItemsBinding

    private var mFromSource: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBottomSheetOrganizeItemsBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_organize_items, container, false)
        val view = mBottomSheetOrganizeItemsBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
    }

    private fun checkInteractions() {
        //
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "OrganizeItemBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance(fromSource: String?) =
            OrganizeItemBottomSheetDialogFragment().apply {
                mFromSource = fromSource
            }
    }
}