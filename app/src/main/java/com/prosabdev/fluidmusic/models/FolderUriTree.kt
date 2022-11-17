package com.prosabdev.fluidmusic.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.utils.ConstantValues

@Entity(
    tableName = ConstantValues.FLUID_MUSIC_FOLDER_URI_TREE,
    indices = [Index(value = ["id", "uriTree"], unique = true) ],
)//room ; to create sqlite objects
class FolderUriTree() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var uriTree: String? = null
    var path: String? = null
    var pathTree: String? = null
    var lastPathSegment: String? = null
    var normalizeScheme: String? = null
    var deviceName: String? = null
}