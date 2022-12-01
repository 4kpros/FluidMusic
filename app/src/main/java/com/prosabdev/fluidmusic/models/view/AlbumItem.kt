package com.prosabdev.fluidmusic.models.view

import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView

@DatabaseView(
    "SELECT SongItem.album as name, " +
            "MAX(SongItem.artist) as artist, " +
            "MAX(SongItem.albumArtist) as albumArtist, " +
            "MAX(SongItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(SongItem.id) as numberTracks, " +
            "SUM(SongItem.duration) as totalDuration " +
            "FROM SongItem " +
            "GROUP BY SongItem.album ORDER BY SongItem.album"
)
class AlbumItem {
    var name: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var lastAddedDateToLibrary: String? = null
    var numberTracks: Int = 0
    var totalDuration: Long = 0

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AlbumItem>() {
            override fun areItemsTheSame(
                oldItem: AlbumItem,
                newItem: AlbumItem
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: AlbumItem, newItem: AlbumItem) =
                oldItem.name == newItem.name &&
                        oldItem.artist == newItem.artist &&
                        oldItem.albumArtist == newItem.albumArtist &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                        oldItem.numberTracks == newItem.numberTracks &&
                        oldItem.totalDuration == newItem.totalDuration
        }
    }
}