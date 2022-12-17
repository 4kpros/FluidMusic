package com.prosabdev.fluidmusic.models.view

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils

@DatabaseView(
    "SELECT songItem.album as name, " +
            "MAX(songItem.artist) as artist, " +
            "MAX(songItem.albumArtist) as albumArtist, " +
            "MAX(songItem.year) as year, " +
            "MAX(songItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(songItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.composer) as numberComposers, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "(SELECT tempSI.hashedCovertArtSignature FROM SongItem as tempSI WHERE tempSI.album = songItem.album AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.album,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as hashedCovertArtSignature," +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.album = songItem.album AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.album,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.album ORDER BY SongItem.album"
)
class AlbumItem {
    var name: String? = ""
    var artist: String? = ""
    var albumArtist: String? = ""
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
        const val TAG = "AlbumItem"
        const val DEFAULT_INDEX = "name"
        const val INDEX_COLUM_TO_SONG_ITEM = "album"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is AlbumItem) {
                return dataItem.name?.ifEmpty { "" } ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is AlbumItem) {
                return dataItem.name?.ifEmpty { "#" } ?: ""
            }
            return "#"
        }

        fun castDataItemToGeneric(ctx: Context, dataItem: Any?, setAllText: Boolean = false): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is AlbumItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name?.ifEmpty { ctx.getString(R.string.unknown_album) } ?: ctx.getString(R.string.unknown_album)
                val tempSubtitle : String = dataItem.artist?.ifEmpty { ctx.getString(R.string.unknown_artists) } ?: ctx.getString(R.string.unknown_artists)
                val tempDetails =
                    if(setAllText)
                        ctx.getString(
                            R.string.item_content_explore_text_details,
                            FormattersAndParsersUtils.formatSongDurationToString(dataItem.totalDuration),
                            dataItem.numberTracks.toString()
                        )
                    else
                        ""
                tempResult.name = dataItem.name
                tempResult.title = tempTitle
                tempResult.title = tempTitle
                tempResult.subtitle =
                    if (tempTitle != ctx.getString(R.string.unknown_album))
                        if (dataItem.numberArtists <= 1)
                            tempSubtitle
                        else
                            "${dataItem.numberArtists} artist(s)"
                    else
                        if(tempSubtitle == ctx.getString(R.string.unknown_artists))
                            tempSubtitle
                        else
                            "${dataItem.numberArtists} artist(s)"
                tempResult.details = tempDetails
                if(dataItem.uriImage?.isNotEmpty() == true){
                    tempResult.imageUri = Uri.parse(dataItem.uriImage)
                }
                tempResult.imageHashedSignature = dataItem.hashedCovertArtSignature
            }
            return tempResult
        }

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
                        oldItem.numberArtists == newItem.numberArtists &&
                        oldItem.numberComposers == newItem.numberComposers &&
                        oldItem.numberTracks == newItem.numberTracks &&
                        oldItem.totalDuration == newItem.totalDuration &&
                        oldItem.hashedCovertArtSignature == newItem.hashedCovertArtSignature &&
                        oldItem.uriImage == newItem.uriImage
        }
    }
}