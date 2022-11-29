package com.prosabdev.fluidmusic.models.view

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT SongItem.folder as name, " +
            "SongItem.uriTreeId as uriTreeId, " +
            "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(SongItem.id) as numberTracks, " +
            "SUM(SongItem.duration) as totalDuration " +
            "FROM SongItem " +
            "GROUP BY SongItem.folder, SongItem.uriTreeId ORDER BY SongItem.folder ASC"
)
class FolderItem {
    var name: String? = null
    var uriTreeId: Long = 0
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0
}