package com.prosabdev.fluidmusic.utils

import android.content.Context
import com.prosabdev.fluidmusic.R

abstract class DeviceDimensions {

    companion object {


        fun getSpecificWithForOrganizeListGridType(ctx: Context, organizeListGrid: Int): Int {
            var dimen: Int = 0
            dimen =
                when (organizeListGrid) {
                    ConstantValues.ORGANIZE_LIST_EXTRA_SMALL -> {
                        ctx.resources.getDimensionPixelSize(R.dimen.organize_list_extra_small_image_size)
                    }
                    ConstantValues.ORGANIZE_LIST_SMALL -> {
                        ctx.resources.getDimensionPixelSize(R.dimen.organize_list_small_image_size)
                    }
                    ConstantValues.ORGANIZE_LIST_MEDIUM -> {
                        ctx.resources.getDimensionPixelSize(R.dimen.organize_list_medium_image_size)
                    }
                    ConstantValues.ORGANIZE_LIST_LARGE -> {
                        ctx.resources.getDimensionPixelSize(R.dimen.organize_list_large_image_size)
                    }
                    else -> {
                        ctx.resources.getDimensionPixelSize(R.dimen.organize_list_small_image_size)
                    }
                }


            return dimen
        }
    }
}