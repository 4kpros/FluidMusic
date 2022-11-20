package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView("SELECT SongItem.album as album, " +
        "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
        "COUNT(SongItem.album) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM SongItem " +
        "GROUP BY SongItem.album ORDER BY SongItem.album")
class AlbumItem {
    var album: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}