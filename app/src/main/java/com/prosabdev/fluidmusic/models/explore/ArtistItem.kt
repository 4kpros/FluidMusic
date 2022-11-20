package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView("SELECT SongItem.artist as artist, " +
        "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
        "COUNT(SongItem.artist) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM SongItem " +
        "GROUP BY SongItem.artist ORDER BY SongItem.artist")
class ArtistItem {
    var artist: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}