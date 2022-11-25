package com.prosabdev.fluidmusic.utils

abstract class FormattersUtils {
    companion object {
        //Return duration to formatted string
        fun formatSongDurationToString(durationTemp: Long): String? {
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

        private fun getMaxTime(value: Int, max: Int): Int {
            var tempValue = value
            while (tempValue > max) {
                tempValue -= max
            }
            return tempValue
        }
    }
}