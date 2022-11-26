package com.prosabdev.fluidmusic.models.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
)
class PlaylistItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var playlist: String = ""
    var addedDate: Long = 0
}