package com.prosabdev.fluidmusic.models.view

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT SongItem.year as name, " +
            "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(SongItem.id) as numberTracks, " +
            "SUM(SongItem.duration) as totalDuration " +
            "FROM SongItem " +
            "GROUP BY SongItem.year ORDER BY SongItem.year DESC"
)
class YearItem {
    var name: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}