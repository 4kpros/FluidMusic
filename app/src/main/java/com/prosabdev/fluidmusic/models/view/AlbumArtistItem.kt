package com.prosabdev.fluidmusic.models.view

import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView

@DatabaseView(
    "SELECT songItem.albumArtist as name, " +
            "MAX(songItem.artist) as artist, " +
            "MAX(songItem.album) as album, " +
            "MAX(songItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(songItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.composer) as numberComposers, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "MAX(songItem.hashedCovertArtSignature) as hashedCovertArtSignature, " +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.hashedCovertArtSignature = songItem.hashedCovertArtSignature LIMIT 1) as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.albumArtist ORDER BY SongItem.albumArtist"
)
class AlbumArtistItem {
    var name: String? = null
    var artist: String? = null
    var album: String? = null
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var numberArtists: Int = 0
    var numberComposers: Int = 0
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var hashedCovertArtSignature: Int = -1
    var uriImage: String? = null

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AlbumArtistItem>() {
            override fun areItemsTheSame(
                oldItem: AlbumArtistItem,
                newItem: AlbumArtistItem
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: AlbumArtistItem, newItem: AlbumArtistItem) =
                oldItem.name == newItem.name &&
                        oldItem.artist == newItem.artist &&
                        oldItem.album == newItem.album &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                        oldItem.numberArtists == newItem.numberArtists &&
                        oldItem.numberComposers == newItem.numberComposers &&
                        oldItem.numberTracks == newItem.numberTracks &&
                        oldItem.totalDuration == newItem.totalDuration &&
                        oldItem.hashedCovertArtSignature == newItem.hashedCovertArtSignature &&
                        oldItem.uriImage == newItem.uriImage
        }
    }
}