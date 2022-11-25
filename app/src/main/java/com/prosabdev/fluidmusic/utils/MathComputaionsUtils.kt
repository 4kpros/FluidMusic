package com.prosabdev.fluidmusic.utils

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowMetrics
import kotlin.math.floor
import kotlin.math.sin


abstract class MathComputaionsUtils {
    companion object {

        fun fadeWithPageOffset(vew : View?, positionOffset: Float){
            vew?.alpha = ((sin(-positionOffset.toDouble() * 3) / 2) + 1).toFloat()
        }
        //Return screen width in pixels
        fun getScreenWidth(activity: Activity): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.width() - insets.left - insets.right
            } else {
                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }

        //Return random number different from current between 0 and max
        fun randomExcluded(currentPosition: Int, maxPosition: Int): Int {
            var n = floor(Math.random() * maxPosition + 0).toInt()
            if (n >= currentPosition) n++
            return n
        }
    }
}