package com.prosabdev.fluidmusic.models.view

import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView

@DatabaseView(
    "SELECT songItem.composer as name, " +
            "MAX(songItem.year) as year, " +
            "MAX(songItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(songItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.album) as numberAlbums, " +
            "COUNT(DISTINCT songItem.albumArtist) as numberAlbumArtists, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "MAX(songItem.hashedCovertArtSignature) as hashedCovertArtSignature, " +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.hashedCovertArtSignature = songItem.hashedCovertArtSignature LIMIT 1) as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.composer ORDER BY SongItem.composer"
)
class ComposerItem {
    var name: String? = null
    var year: String? = null
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var numberArtists: Int = 0
    var numberAlbums: Int = 0
    var numberAlbumArtists: Int = 0
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var hashedCovertArtSignature: Int = -1
    var uriImage: String? = null

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ComposerItem>() {
            override fun areItemsTheSame(
                oldItem: ComposerItem,
                newItem: ComposerItem
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: ComposerItem, newItem: ComposerItem) =
                oldItem.name == newItem.name &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                        oldItem.numberArtists == newItem.numberArtists &&
                        oldItem.numberAlbums == newItem.numberAlbums &&
                        oldItem.numberAlbumArtists == newItem.numberAlbumArtists &&
                        oldItem.numberTracks == newItem.numberTracks &&
                        oldItem.totalDuration == newItem.totalDuration &&
                        oldItem.hashedCovertArtSignature == newItem.hashedCovertArtSignature &&
                        oldItem.uriImage == newItem.uriImage
        }
    }
}