package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["id", "uriTree"], unique = true)]
)
class FolderUriTree {
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1
    var uriTree: String? = null
    var path: String? = null
    var pathTree: String? = null
    var lastPathSegment: String? = null
    var normalizeScheme: String? = null
    var deviceName: String? = null

    var lastModified: Long = -1
}