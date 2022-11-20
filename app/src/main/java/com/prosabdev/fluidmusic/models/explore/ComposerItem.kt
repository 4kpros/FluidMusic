package com.prosabdev.fluidmusic.models.explore

import androidx.room.DatabaseView

@DatabaseView("SELECT SongItem.composer as composer, " +
        "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
        "COUNT(SongItem.composer) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM SongItem " +
        "GROUP BY SongItem.composer ORDER BY SongItem.composer")
class ComposerItem {
    var composer: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}