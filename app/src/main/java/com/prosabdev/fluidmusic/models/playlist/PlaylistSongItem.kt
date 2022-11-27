package com.prosabdev.fluidmusic.models.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.explore.SongItem

@Entity(
    foreignKeys = [
        ForeignKey(entity = PlaylistItem::class, parentColumns = ["id"], childColumns = ["playlistId"]),
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE, onUpdate = CASCADE)
    ]
)
class PlaylistSongItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var playlistId: Long = 0
    var songId: Long = 0
    var addedDate: Long = 0
}