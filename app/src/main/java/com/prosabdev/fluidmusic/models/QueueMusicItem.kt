package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.collections.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues

@Entity(
    tableName = ConstantValues.FLUID_MUSIC_QUEUE_MUSIC_TABLE,
    foreignKeys = [
        ForeignKey(entity = SongItem::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE, onUpdate = CASCADE)
    ],
    indices = [Index(value = ["id"], unique = true) ],
)//room ; to create sqlite objects
class QueueMusicItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var songId: Int = 0
}