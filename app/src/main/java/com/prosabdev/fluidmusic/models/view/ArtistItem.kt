package com.prosabdev.fluidmusic.models.view

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT SongItem.artist as name, " +
            "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(SongItem.artist) as numberTracks, " +
            "SUM(SongItem.duration) as totalDuration " +
            "FROM SongItem " +
            "GROUP BY SongItem.artist ORDER BY SongItem.artist"
)
class ArtistItem {
    var name: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}