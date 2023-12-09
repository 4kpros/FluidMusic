package com.prosabdev.common.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["uriTree"], unique = true)]
)
data class FolderUriTree (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var uriTree: String = "",
    var path: String = "",
    var pathTree: String = "",
    var lastPathSegment: String = "",
    var normalizeScheme: String = "",
    var deviceName: String = "",

    var lastModified: Long = 0
){
    companion object {
        const val TAG = "FolderUriTree"
    }
}