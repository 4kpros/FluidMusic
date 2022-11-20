package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.explore.SongItem

@Entity(
    foreignKeys = [
        ForeignKey(entity = PlaylistItem::class, parentColumns = ["id"], childColumns = ["playlistId"], onDelete = CASCADE, onUpdate = CASCADE),
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE, onUpdate = CASCADE)
    ],
    indices = [Index(value = ["id"], unique = true) ],
    primaryKeys = ["id", "playlistId"]
)
class PlaylistSongItem {
    var id: Long = 0
    var playlistId: Int = -1
    var songId: Int = -1
}