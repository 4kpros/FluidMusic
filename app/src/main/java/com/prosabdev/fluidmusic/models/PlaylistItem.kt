package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
)
class PlaylistItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var playlist: String = ""
}