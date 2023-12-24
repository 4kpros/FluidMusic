package com.prosabdev.common.models.view

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView
import com.prosabdev.common.R
import com.prosabdev.common.models.generic.GenericItemListGrid
import com.prosabdev.common.utils.FormattersAndParsers

@DatabaseView(
    "SELECT songItem.artist as name, " +
            "MAX(songItem.year) as year, " +
            "MAX(songItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(songItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.album) as numberAlbums, " +
            "COUNT(DISTINCT songItem.albumArtist) as numberAlbumArtists, " +
            "COUNT(DISTINCT songItem.composer) as numberComposers, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "(SELECT tempSI.hashedCovertArtSignature FROM SongItem as tempSI WHERE tempSI.artist = songItem.artist AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.artist,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as hashedCovertArtSignature," +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.artist = songItem.artist AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.artist,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.artist ORDER BY SongItem.artist"
)
data class ArtistItem (
    var name: String? = "",
    var year: String? = "",
    var lastUpdateDate: Long = 0,
    var lastAddedDateToLibrary: Long = 0,
    var numberAlbums: Int = 0,
    var numberAlbumArtists: Int = 0,
    var numberComposers: Int = 0,
    var numberTracks: Int = 0,
    var totalDuration: Long = 0,
    var hashedCovertArtSignature: Int = -1,
    var uriImage: String? = ""
){
    companion object {
        const val TAG = "ArtistItem"
        const val DEFAULT_INDEX = "name"
        const val INDEX_COLUM_TO_SONG_ITEM = "artist"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is ArtistItem) {
                return dataItem.name?.ifEmpty { "" } ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is ArtistItem) {
                return dataItem.name?.ifEmpty { "#" } ?: "#"
            }
            return "#"
        }

        fun castDataItemToGeneric(ctx: Context, dataItem: Any?, setAllText: Boolean = false): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is ArtistItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name?.ifEmpty { ctx.getString(R.string.unknown_artists) } ?: ctx.getString(R.string.unknown_artists)
                val tempSubtitle = "${dataItem.numberAlbums} album(s)"
                val tempDetails =
                    if(setAllText)
                        ctx.getString(
                            R.string.item_content_explore_text_details,
                            FormattersAndParsers.formatSongDurationToString(dataItem.totalDuration),
                            dataItem.numberTracks.toString()
                        )
                    else
                        ""
                tempResult.name = dataItem.name
                tempResult.title = tempTitle
                tempResult.subtitle = tempSubtitle
                tempResult.details = tempDetails
                if(dataItem.uriImage?.isNotEmpty() == true){
                    tempResult.mediaUri = Uri.parse(dataItem.uriImage)
                }
                tempResult.hashedCovertArtSignature = dataItem.hashedCovertArtSignature
            }
            return tempResult
        }

        val diffCallback = object : DiffUtil.ItemCallback<ArtistItem>() {
            override fun areItemsTheSame(
                oldItem: ArtistItem,
                newItem: ArtistItem
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: ArtistItem, newItem: ArtistItem) =
                oldItem.name == newItem.name &&
                        oldItem.year == newItem.year &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                        oldItem.numberAlbums == newItem.numberAlbums &&
                        oldItem.numberAlbumArtists == newItem.numberAlbumArtists &&
                        oldItem.numberComposers == newItem.numberComposers &&
                        oldItem.numberTracks == newItem.numberTracks &&
                        oldItem.totalDuration == newItem.totalDuration &&
                        oldItem.hashedCovertArtSignature == newItem.hashedCovertArtSignature &&
                        oldItem.uriImage == newItem.uriImage
        }
    }
}