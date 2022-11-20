package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView("SELECT SongItem.year as year, " +
        "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
        "COUNT(SongItem.id) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM SongItem " +
        "GROUP BY SongItem.year ORDER BY SongItem.year DESC")
class YearItem {
    var year: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}