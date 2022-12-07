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
            "MAX(songItem.hashedCovertArtSignature) as hashedCovertArtSignature, " +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.hashedCovertArtSignature = songItem.hashedCovertArtSignature LIMIT 1) as uriImage " +
            "FROM SongItem as songItem " +
            "GROUP BY SongItem.album ORDER BY SongItem.album"
)
class AlbumItem {
    var name: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var year: String? = null
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var numberArtists: Int = 0
    var numberComposers: Int = 0
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var hashedCovertArtSignature: Int = -1
    var uriImage: String? = null

    companion object {
        const val TAG = "AlbumItem"
        const val DEFAULT_INDEX = "name"
        const val INDEX_COLUM_TO_SONG_ITEM = "album"

        fun getStringIndexRequestFastScroller(ctx: Context, dataItem: Any): String {
            if(dataItem is AlbumItem) {
                return dataItem.name ?: ctx.getString(R.string.unknown_album)
            }
            return "#"
        }
        fun castDataItemToGeneric(ctx: Context, dataItem: Any): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is AlbumItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name ?: ctx.getString(R.string.unknown_album)
                val tempSubtitle : String = dataItem.artist ?: ctx.getString(R.string.unknown_artists)
                val tempDetails : String = "${dataItem.numberTracks} song(s) | ${FormattersAndParsersUtils.formatSongDurationToString(dataItem.totalDuration)} min"
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
                tempResult.imageUri = Uri.parse(dataItem.uriImage)
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