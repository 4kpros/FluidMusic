package com.prosabdev.fluidmusic.ui.bottomsheetdialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetStorageAccessBinding
import com.prosabdev.fluidmusic.viewmodels.StorageAccessActivityViewModel

class StorageAccessDialog : GenericBottomSheetDialogFragment() {

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mBottomSheetStorageAccessBinding : BottomSheetStorageAccessBinding
    private val mStorageAccessActivityViewModel: StorageAccessActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mContext = requireContext()
        mActivity = requireActivity()

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
        mBottomSheetStorageAccessBinding.buttonRestoreDefault.setOnClickListener(){
            onRestoreDefaultSettings()
        }
        mBottomSheetStorageAccessBinding.buttonRollbackPrevious.setOnClickListener(){
            onRollbackPreviousSettings()
        }
        mBottomSheetStorageAccessBinding.buttonRemoveAll.setOnClickListener(){
            showRemoveAllDialog()
        }
    }

    private fun showRemoveAllDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Remove all folders ?")
            .setMessage("All folders will be removed from your accessible files. Do you want to delete all anyway ?")
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
        //
        dialog.dismiss()
    }

    private fun onRollbackPreviousSettings() {
        showRollbackPreviousSettingsDialog()
    }
    private fun showRollbackPreviousSettingsDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Restore previous selected folder ?")
            .setMessage("This will only restore previous saved folders.")
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, which ->
                rollbackPreviousSettings(dialog)
            }
            .show()
        dismiss()
    }
    private fun rollbackPreviousSettings(dialog: DialogInterface) {
        //
        dialog.dismiss()
    }

    private fun onRestoreDefaultSettings() {
        showRestoreDefaultDialog()
    }
    private fun showRestoreDefaultDialog() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Reset to default ?")
            .setMessage("All folders will be removed from your accessible files. Do you want to delete restore anyway ?")
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, which ->
                restoreDefault(dialog)
            }
            .show()
        dismiss()
    }
    private fun restoreDefault(dialog: DialogInterface) {
        //
        dialog.dismiss()
    }

    private fun initViews() {
    }

    companion object {
        const val TAG = "PlayerMoreDialog"
    }
}