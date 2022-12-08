package com.prosabdev.fluidmusic.models.queuemusic

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.songitem.SongItem

@Entity(
    foreignKeys = [
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songUri"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.NO_ACTION)
    ]
)
class QueueMusicItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var songUri: String? = null
    var addedDate: Long = 0
    var playOrder: Int = 0
}