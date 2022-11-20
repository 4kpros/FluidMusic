package com.prosabdev.fluidmusic.models.explore

import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree
import org.jaudiotagger.tag.images.Artwork

@Entity(
    foreignKeys = [
        ForeignKey(entity = FolderUriTree::class, parentColumns = ["id"], childColumns = ["uriTreeId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["id", "uri"], unique = true)],
    primaryKeys = ["id"]
)
open class SongItem() {
    var id : Long = -1
    var uriTreeId: Long = -1
    var uri: String? = null
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var composer: String? = null
    var albumArtist: String? = null
    var album: String? = null
    var genre: String? = null
    var relativePath: String? = null
    var folder: String? = null
    var year: String? = null
    var duration: Long = 0

    //
    var typeMime: String? = null
    var sampleRate = 0
    var bitrate = 0.0

    //
    var lastUpdateDate: String? = null
    var lastAddedDateToLibrary: String? = null

    //
    var storageName: String? = null


    //
    @Ignore
    var covertArt: Artwork? = null
    @Ignore
    var covertArtUrl: String? = null
}