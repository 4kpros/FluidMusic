package com.prosabdev.common.models.view

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView
import com.prosabdev.common.R
import com.prosabdev.common.models.generic.GenericItemListGrid
import com.prosabdev.common.utils.FormattersAndParsers

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
            "(SELECT tempSI.hashedCovertArtSignature FROM SongItem as tempSI WHERE tempSI.composer = songItem.composer AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.composer,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as hashedCovertArtSignature," +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.composer = songItem.composer AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.composer,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.composer ORDER BY SongItem.composer"
)
data class ComposerItem (
    var name: String? = "",
    var year: String? = "",
    var lastUpdateDate: Long = 0,
    var lastAddedDateToLibrary: Long = 0,
    var numberArtists: Int = 0,
    var numberAlbums: Int = 0,
    var numberAlbumArtists: Int = 0,
    var numberTracks: Int = 0,
    var totalDuration: Long = 0,
    var hashedCovertArtSignature: Int = -1,
    var uriImage: String? = ""
){
    companion object {
        const val TAG = "ComposerItem"
        const val DEFAULT_INDEX = "name"
        const val INDEX_COLUM_TO_SONG_ITEM = "composer"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is ComposerItem) {
                return dataItem.name?.ifEmpty { "" } ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is ComposerItem) {
                return dataItem.name?.ifEmpty { "#" } ?: "#"
            }
            return "#"
        }

        fun castDataItemToGeneric(ctx: Context, dataItem: Any?, setAllText: Boolean = false): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is ComposerItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name?.ifEmpty { ctx.getString(R.string.unknown_composer) } ?: ctx.getString(R.string.unknown_composer)
                val tempSubtitle =
                    ctx.getString(
                        R.string.item_content_explore_text_subtitle,
                        dataItem.numberArtists.toString(),
                        dataItem.numberAlbums.toString()
                    )
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