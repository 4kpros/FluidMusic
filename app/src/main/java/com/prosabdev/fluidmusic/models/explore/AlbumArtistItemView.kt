package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT SongItem.albumArtist as albumArtist, " +
            "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(SongItem.id) as numberTracks, " +
            "SUM(SongItem.duration) as totalDuration " +
            "FROM SongItem " +
            "GROUP BY SongItem.albumArtist ORDER BY SongItem.albumArtist"
)
class AlbumArtistItemView {
    var albumArtist: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}