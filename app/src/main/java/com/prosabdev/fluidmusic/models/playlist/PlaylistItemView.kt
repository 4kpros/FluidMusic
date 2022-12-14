package com.prosabdev.fluidmusic.models.playlist

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView

@DatabaseView(
    "SELECT " +
            "PlaylistItem.id as playlistId, " +
            "PlaylistItem.name as name, " +
            "songItem.uri as songUri, " +
            "MAX(songItem.year) as year, " +
            "MAX(PlaylistItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(PlaylistItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.album) as numberAlbums, " +
            "COUNT(DISTINCT songItem.albumArtist) as numberAlbumArtists, " +
            "COUNT(DISTINCT songItem.composer) as numberComposers, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "MAX(songItem.hashedCovertArtSignature) as hashedCovertArtSignature, " +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.hashedCovertArtSignature = songItem.hashedCovertArtSignature LIMIT 1) as uriImage " +
            "FROM PlaylistItem " +
            "INNER JOIN PlaylistSongItem ON PlaylistItem.id = PlaylistSongItem.playlistId "+
            "INNER JOIN SongItem ON PlaylistSongItem.songUri = SongItem.uri " +
            "GROUP BY PlaylistItem.id ORDER BY PlaylistItem.name DESC"
)
class PlaylistItemView {
    var playlistId: Long = 0
    var name: String? = ""
    var songUri: String? = ""
    var year: String? = ""
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var numberArtists: Int = 0
    var numberComposers: Int = 0
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var hashedCovertArtSignature: Int = -1
    var uriImage: String? = ""

    companion object {
        const val TAG = "PlaylistItemView"
        const val DEFAULT_INDEX = "name"

        fun getStringIndexRequestFastScroller(ctx: Context, dataItem: Any): String {
            if(dataItem is PlaylistItemView) {
                return dataItem.name ?: "#"
            }
            return "#"
        }
        val diffCallback = object : DiffUtil.ItemCallback<PlaylistItemView>() {
            override fun areItemsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem.playlistId == newItem.playlistId
            }

            override fun areContentsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem.playlistId == newItem.playlistId &&
                        oldItem.name == newItem.name &&
                        oldItem.songUri == newItem.songUri &&
                        oldItem.year == newItem.year &&
                        oldItem.lastUpdateDate == newItem.lastUpdateDate &&
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
}