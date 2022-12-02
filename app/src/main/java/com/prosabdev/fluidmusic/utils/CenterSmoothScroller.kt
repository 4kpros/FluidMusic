package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterSmoothScroller(
    ctx : Context
): LinearSmoothScroller(ctx)
{
    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
//        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
//        return super.calculateSpeedPerPixel(displayMetrics)
        Log.i(ConstantValues.TAG, "TIME TO SCROLL MILLISECONDS_PER_INCH : ${MILLISECONDS_PER_INCH / (displayMetrics?.densityDpi ?: 0)}")
        return MILLISECONDS_PER_INCH / (displayMetrics?.densityDpi ?: 0)
    }

    override fun calculateTimeForScrolling(dx: Int): Int {
//        return super.calculateTimeForScrolling(dx)
        return 500
    }

    override fun calculateTimeForDeceleration(dx: Int): Int {
//        return super.calculateTimeForDeceleration(dx)
        return 500
    }

    companion object {
        private const val MILLISECONDS_PER_INCH: Float = 150f
    }

}