package com.prosabdev.fluidmusic.models.explore

import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree
import org.jaudiotagger.tag.images.Artwork

@Entity(
    foreignKeys = [
        ForeignKey(entity = FolderUriTree::class, parentColumns = ["id"], childColumns = ["uriTreeId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["uri"], unique = true)]
)
class SongItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var uriTreeId: Long = 0
    var uri: String? = null
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var composer: String? = null
    var album: String? = null
    var genre: String? = null
    var relativePath: String? = null
    var folder: String? = null
    var folderUri: String? = null
    var year: String? = null
    var duration: Long = 0
    var language: String? = null

    //
    var typeMime: String? = null
    var sampleRate: Int = 0
    var bitrate: Double = 0.0
    var bitPerSample: String? = null

    //
    var lastUpdateDate: String? = null
    var lastAddedDateToLibrary: Long = 0

    var author: String? = null
    var writer: String? = null
    var cdTrackNumber: String? = null
    var numberTracks: String? = null
}