package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

abstract class BottomSheetFilterDialogManager {

    companion object {

        private var mSortSongsDialog: SortSongsBottomSheetDialogFragment = SortSongsBottomSheetDialogFragment.newInstance()
        private val mSortGenericDialog: SortContentExplorerIBottomSheetDialogFragment = SortContentExplorerIBottomSheetDialogFragment.newInstance()

        private var mOrganizeDialog: OrganizeItemBottomSheetDialogFragment = OrganizeItemBottomSheetDialogFragment.newInstance()

        fun showSongsSortBottomSheetDialog(
            fragmentManager: FragmentManager,
            genericListenDataViewModel: GenericListenDataViewModel,
            fromSource: String?,
            fromSourceValue: String?
        ) {
            if(mSortSongsDialog.isVisible) return
            mSortSongsDialog.updateBottomSheetData(
                genericListenDataViewModel,
                fromSource,
                fromSourceValue
            )
            mSortSongsDialog.show(fragmentManager, SortContentExplorerIBottomSheetDialogFragment.TAG)
        }
        fun showGenericSortBottomSheetDialog(
            fragmentManager: FragmentManager,
            genericListenDataViewModel: GenericListenDataViewModel,
            fromSource: String?,
            fromSourceValue: String? = null
        ) {
            if(mSortGenericDialog.isVisible) return
            mSortGenericDialog.updateBottomSheetData(
                genericListenDataViewModel,
                fromSource,
                fromSourceValue
            )
            mSortGenericDialog.show(fragmentManager, SortContentExplorerIBottomSheetDialogFragment.TAG)
        }

        fun showOrganizeBottomSheetDialog(
            fragmentManager: FragmentManager,
            genericListenDataViewModel: GenericListenDataViewModel,
            fromSource: String?,
            fromSourceValue: String? = null
        ) {
            if(mOrganizeDialog.isVisible) return
            mOrganizeDialog.updateBottomSheetData(
                genericListenDataViewModel,
                fromSource,
                fromSourceValue
            )
            mOrganizeDialog.show(fragmentManager, OrganizeItemBottomSheetDialogFragment.TAG)
        }

        fun getSpanCount(ctx: Context, organizeValue: Int?): Int {
            when (organizeValue ?: ConstantValues.ORGANIZE_LIST_MEDIUM) {
                ConstantValues.ORGANIZE_GRID_SMALL_NO_IMAGE -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_no_image_span_count)
                }
                ConstantValues.ORGANIZE_GRID_MEDIUM_NO_IMAGE -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_no_image_span_count)
                }
                ConstantValues.ORGANIZE_GRID_EXTRA_SMALL -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_extra_small_span_count)
                }
                ConstantValues.ORGANIZE_GRID_SMALL -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_small_span_count)
                }
                ConstantValues.ORGANIZE_GRID_MEDIUM -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_medium_span_count)
                }
                ConstantValues.ORGANIZE_GRID_LARGE -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_large_span_count)
                }
                ConstantValues.ORGANIZE_GRID_EXTRA_LARGE -> {
                    return ctx.resources.getInteger(R.integer.organize_grid_extra_large_span_count)
                }
                else -> {
                    return 1
                }
            }
        }
    }
}