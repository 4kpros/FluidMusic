package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["id", "playlist"], unique = true) ],
)
class PlaylistItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = -1
    var playlist: String = ""
}