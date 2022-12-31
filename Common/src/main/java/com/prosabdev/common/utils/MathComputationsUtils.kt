package com.prosabdev.common.utils

import android.view.View
import kotlin.math.floor
import kotlin.math.sin


abstract class MathComputationsUtils {
    companion object {

        fun fadeWithPageOffset(vew : View?, positionOffset: Float){
            vew?.alpha = ((sin(-positionOffset.toDouble() * 3) / 2) + 1).toFloat()
        }

        //Return random number different from current between 0 and max
        fun randomExcluded(currentPosition: Int, maxPosition: Int): Int {
            var n = floor(Math.random() * maxPosition + 0).toInt()
            if (n >= currentPosition) n++
            return n
        }
    }
}