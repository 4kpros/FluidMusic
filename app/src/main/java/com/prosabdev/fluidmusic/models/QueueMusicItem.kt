package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.explore.SongItem

@Entity(
    foreignKeys = [
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE, onUpdate = CASCADE)
    ],
    indices = [Index(value = ["id"], unique = true) ],
    primaryKeys = ["id"]
)
class QueueMusicItem {
    var id: Long = -1
    var songId: Long = -1
}