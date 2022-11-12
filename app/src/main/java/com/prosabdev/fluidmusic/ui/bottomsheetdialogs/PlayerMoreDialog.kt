package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R

class PlayerMoreDialog : GenericBottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_player_more, container, false)

        initViews(view)
        checkInteractions()

        return view
    }

    private fun checkInteractions() {

    }

    private fun initViews(view: View?) {

    }

    companion object {
        const val TAG = "PlayerMoreDialog"
    }
}