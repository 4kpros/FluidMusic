package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE, onUpdate = CASCADE)
    ]
)
class QueueMusicItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var songId: Long = 0
    var addedDate: Long = 0
}