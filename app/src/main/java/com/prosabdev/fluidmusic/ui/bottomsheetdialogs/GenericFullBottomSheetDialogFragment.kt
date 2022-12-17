package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class GenericFullBottomSheetDialogFragment: BottomSheetDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

//        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
//        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//        mBottomSheetAddToPlaylistBinding.coordinatorLayout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

    }
}