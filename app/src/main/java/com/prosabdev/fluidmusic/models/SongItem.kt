package com.prosabdev.fluidmusic.models

import androidx.recyclerview.widget.DiffUtil
import androidx.room.*

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
    var uriPath: String? = null
    var folder: String? = null
    var folderUri: String? = null
    var year: String? = null
    var duration: Long = 0
    var language: String? = null

    //
    var typeMime: String? = null
    var sampleRate: Int = 0
    var bitrate: Double = 0.0

    var size: Long = 0

    var channelCount: Int = 0
    var fileExtension: String? = null
    var bitPerSample: String? = null

    //
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0

    var author: String? = null
    var diskNumber: String? = null
    var writer: String? = null
    var cdTrackNumber: String? = null
    var numberTracks: String? = null

    var hashedCovertArtSignature: Int = -1

    @Ignore
    var position: Int = -1

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<SongItem>() {
            override fun areItemsTheSame(
                oldItem: SongItem,
                newItem: SongItem
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem) =
                oldItem.id == newItem.id && oldItem.uri == newItem.uri
        }
    }
}