package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetStorageAccessBinding
import com.prosabdev.fluidmusic.viewmodels.activities.StorageAccessActivityViewModel

class StorageAccessFullBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var mBottomSheetStorageAccessBinding : BottomSheetStorageAccessBinding

    private lateinit var mStorageAccessActivityViewModel: StorageAccessActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBottomSheetStorageAccessBinding = DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_storage_access,container,false)
        val view = mBottomSheetStorageAccessBinding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        checkInteractions()
    }

    private fun checkInteractions() {
        mBottomSheetStorageAccessBinding.buttonRemoveAll.setOnClickListener(){
            showRemoveAllDialog()
        }
    }
    private fun showRemoveAllDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle("Remove all folders ?")
            .setMessage("All folders with songs(on your database, not on device) will be removed from your accessible files. Do you want to remove all anyway ?")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Remove") { dialog, which ->
                removeAllFolder(dialog)
            }
            .show()
        dismiss()
    }
    private fun removeAllFolder(dialog: DialogInterface) {
        mStorageAccessActivityViewModel.setRemoveAllFoldersCounter()
        dialog.dismiss()
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "PlayerMoreDialog"

        @JvmStatic
        fun newInstance(storageAccessActivityViewModel : StorageAccessActivityViewModel) =
            StorageAccessFullBottomSheetDialog().apply {
                mStorageAccessActivityViewModel = storageAccessActivityViewModel
            }
    }
}