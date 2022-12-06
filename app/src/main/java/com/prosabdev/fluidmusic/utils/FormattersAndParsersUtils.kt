package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.FolderUriTree
import kotlin.math.floor

abstract class FormattersAndParsersUtils {
    companion object {
        fun getSpecificWidthSizeForListType(ctx: Context, organizeListGrid: Int): Int {
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
        fun getUnderLinedWord(word: String?): CharSequence {
            val uWord = SpannableString(word ?: "")
            uWord.setSpan(UnderlineSpan(), 0, uWord.length, 0)
            return uWord
        }
        //Return duration to formatted string
        fun formatSongDurationToSliderProgress(currentDuration: Long, totalDuration: Long): Float {
            return if (totalDuration > currentDuration) currentDuration * 100.0f / totalDuration else 0.0f
        }
        fun formatSliderProgressToLongDuration(sliderProgress : Int, totalDuration: Long) : Long{
            val tempProgress: Long = sliderProgress * totalDuration / 100
            return if(tempProgress > totalDuration) totalDuration else tempProgress
        }
        fun formatSliderProgressToLongDuration(sliderProgress : Float, totalDuration: Long) : Long{
            val tempProgress: Long = floor(sliderProgress).toInt() * totalDuration / 100
            return if(tempProgress > totalDuration) totalDuration else tempProgress
        }
        fun formatSongDurationToString(durationTemp: Long): String {
            var totalOut = ""
            var totalSec: Int
            var totalMin: Int
            var totalHours: Int
            var totalDays: Int
            val seconds = durationTemp.toDouble() / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            totalSec = seconds.toInt()
            totalMin = minutes.toInt()
            totalHours = hours.toInt()
            totalDays = days.toInt()
            totalSec = getMaxTime(totalSec, 59)
            totalMin = getMaxTime(totalMin, 59)
            totalHours = getMaxTime(totalHours, 59)
            totalDays = getMaxTime(totalDays, 23)
            if (totalDays > 0) {
                if (totalDays < 10) {
                    totalOut += "0"
                }
                totalOut += totalDays.toString() + "day "
            }
            if (totalHours > 0) {
                if (totalHours < 10) {
                    totalOut += "0"
                }
                totalOut += "$totalHours:"
            }
            if (totalMin < 10) {
                totalOut += "0"
            }
            totalOut += "$totalMin:"
            if (totalSec < 10) {
                totalOut += "0"
            }
            totalOut += totalSec.toString() + ""
            return totalOut
        }

        fun formatAndReturnFolderUriSAF(context: Context, uri: Uri): FolderUriTree {
            val documentFile = DocumentFile.fromTreeUri(context, uri)

            val tempFolderUriTree = FolderUriTree()
            tempFolderUriTree.uriTree = documentFile?.uri.toString()
            tempFolderUriTree.lastPathSegment = uri.lastPathSegment
            tempFolderUriTree.pathTree = uri.path.toString().trim()
            tempFolderUriTree.normalizeScheme = uri.normalizeScheme().toString()
            tempFolderUriTree.path = "/${(uri.lastPathSegment ?: "").substringAfter(":")}"
            tempFolderUriTree.deviceName =
                if ((uri.lastPathSegment ?: "").substringBefore(":") == DeviceInfoUtils.STORAGE_ID_PRIMARY)
                    DeviceInfoUtils.getDeviceName()
                else
                    DeviceInfoUtils.getDeviceName()
            return tempFolderUriTree
        }

        private fun getMaxTime(value: Int, max: Int): Int {
            var tempValue = value
            while (tempValue > max) {
                tempValue -= max
            }
            return tempValue
        }
    }
}