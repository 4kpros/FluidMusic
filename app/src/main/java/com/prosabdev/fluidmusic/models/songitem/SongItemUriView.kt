package com.prosabdev.fluidmusic.models.songitem

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT songItem.id as id, " +
            "SongItem.uriTreeId as uriTreeId, " +
            "SongItem.uri as uri " +
            "FROM SongItem"
)
class SongItemUriView {
    var id: Long = 0
    var uriTreeId: Long = 0
    var uri: String? = null
}