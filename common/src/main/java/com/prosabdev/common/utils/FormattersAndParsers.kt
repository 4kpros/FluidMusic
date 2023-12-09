package com.prosabdev.common.utils

import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.common.R
import com.prosabdev.common.constants.MainConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.floor

object FormattersAndParsers {
    fun formatBandLevelToString(bandLevel: Short): String {
        val tempValue = bandLevel/100
        return if(tempValue > 0) "+${tempValue}dB" else "${tempValue}dB"
    }
    fun formatCenterFreqToString(centerFreq: Int): String {
        val tempFormat = centerFreq / 1000.0f
        val intPart: Int = tempFormat.toInt()
        val decimalPart: Float = tempFormat - intPart
        return if(tempFormat >= 1000){
            formatCenterFreqToString(tempFormat)
        }else{
            "${if(decimalPart > 0.0f) tempFormat else intPart}Hz"
        }
    }
    private fun formatCenterFreqToString(centerFreq: Float): String {
        val tempFormat = centerFreq / 1000.0f
        val intPart: Int = tempFormat.toInt()
        val decimalPart: Float = tempFormat - intPart
        return "${if(decimalPart > 0.0f) tempFormat else intPart}Hz"
    }
    fun formatPercentToBandFreq(progressLevel: Int, minBandLevelRange: Short, maxBandLevelRange: Short): Short {
        val newMaxFreq = maxBandLevelRange + (minBandLevelRange * -1)
        val tempCurrentConvertedFreq = progressLevel * newMaxFreq / 100
        return (tempCurrentConvertedFreq - (minBandLevelRange * -1)).toShort()
    }
    fun formatBandToPercent(bandLevel: Short, minBandLevelRange: Short, maxBandLevelRange: Short): Int {
        val newCurrentFreq = bandLevel + (minBandLevelRange * -1)
        val newMaxFreq = maxBandLevelRange + (minBandLevelRange * -1)
        return if (newMaxFreq > newCurrentFreq) newCurrentFreq * 100 / newMaxFreq else 100
    }

    fun getSpecificWidthSizeForListType(ctx: Context, organizeListGrid: Int): Int {
        return when (organizeListGrid) {
            MainConst.ORGANIZE_LIST_EXTRA_SMALL -> {
                ctx.resources.getDimensionPixelSize(R.dimen.organize_list_extra_small_image_size)
            }
            MainConst.ORGANIZE_LIST_SMALL -> {
                ctx.resources.getDimensionPixelSize(R.dimen.organize_list_small_image_size)
            }
            MainConst.ORGANIZE_LIST_MEDIUM -> {
                ctx.resources.getDimensionPixelSize(R.dimen.organize_list_medium_image_size)
            }
            MainConst.ORGANIZE_LIST_LARGE -> {
                ctx.resources.getDimensionPixelSize(R.dimen.organize_list_large_image_size)
            }
            else -> {
                ctx.resources.getDimensionPixelSize(R.dimen.organize_list_small_image_size)
            }
        }
    }
    fun getUnderLinedWord(word: CharSequence?): CharSequence {
        val uWord = SpannableString(word ?: "")
        uWord.setSpan(UnderlineSpan(), 0, uWord.length, 0)
        return uWord
    }
    //Return duration to formatted string
    fun formatSongDurationToSliderProgress(currentDuration: Long, totalDuration: Long): Float {
        return if (totalDuration > currentDuration) currentDuration * 100.0f / totalDuration else 100.0f
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

    suspend fun formatAndReturnFolderUriSAF(context: Context, uri: Uri): com.prosabdev.common.models.FolderUriTree {
        return withContext(Dispatchers.Default){
            val documentFile = DocumentFile.fromTreeUri(context, uri)

            val tempFolderUriTree = com.prosabdev.common.models.FolderUriTree()
            tempFolderUriTree.uriTree = documentFile?.uri.toString()
            tempFolderUriTree.lastPathSegment = uri.lastPathSegment ?: ""
            tempFolderUriTree.pathTree = uri.path.toString().trim()
            tempFolderUriTree.normalizeScheme = uri.normalizeScheme().toString()
            tempFolderUriTree.path = "/${(uri.lastPathSegment ?: "").substringAfter(":")}"
            tempFolderUriTree.deviceName =
                if ((uri.lastPathSegment ?: "").substringBefore(":") == DeviceInfo.STORAGE_ID_PRIMARY)
                    DeviceInfo.getDeviceName()
                else
                    DeviceInfo.getDeviceName()

            tempFolderUriTree
        }
    }

    private fun getMaxTime(value: Int, max: Int): Int {
        var tempValue = value
        while (tempValue > max) {
            tempValue -= max
        }
        return tempValue
    }
}