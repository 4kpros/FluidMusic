package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView("SELECT SongItem.genre as genre, " +
        "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
        "COUNT(SongItem.id) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM SongItem " +
        "GROUP BY SongItem.genre ORDER BY SongItem.genre ASC")
class GenreItem {
    var genre: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}