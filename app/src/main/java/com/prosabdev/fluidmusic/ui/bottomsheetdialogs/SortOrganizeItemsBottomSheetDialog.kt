package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.CustomViewModifiers

class SortOrganizeItemsBottomSheetDialog : GenericBottomSheetDialogFragment() {
    private var mButtonClose: MaterialButton? = null
    private var mButtonMoreSettings: MaterialButton? = null
    private var mMainContainer: LinearLayoutCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_sort_organize_items_container, container, false)

        initViews(view)
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun checkInteractions() {
        mButtonMoreSettings?.setOnClickListener(){
            //Go to more settings
        }
        mButtonClose?.setOnClickListener(){
            this@SortOrganizeItemsBottomSheetDialog.dismiss()
        }
    }

    private fun initViews(view: View) {
        mButtonMoreSettings = view.findViewById<MaterialButton>(R.id.button_more_settings)
        mButtonClose = view.findViewById<MaterialButton>(R.id.button_close)
        mMainContainer = view.findViewById<LinearLayoutCompat>(R.id.constraint_sort_organize_container)

        if(mMainContainer!= null)
        CustomViewModifiers.updateBottomViewInsets(mMainContainer!!)
    }

    companion object {
        const val TAG = "SortOrganizeItemsBottomSheetDialog"
    }
}