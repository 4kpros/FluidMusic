package com.prosabdev.fluidmusic.models.view

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils

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
    var name: String = ""
    var year: String = ""
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var numberArtists: Int = 0
    var numberAlbums: Int = 0
    var numberAlbumArtists: Int = 0
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var hashedCovertArtSignature: Int = -1
    var uriImage: String = ""

    companion object {
        const val TAG = "ComposerItem"
        const val INDEX_COLUM_TO_SONG_ITEM = "composer"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is ComposerItem) {
                return dataItem.name.ifEmpty { "" }
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is ComposerItem) {
                return dataItem.name.ifEmpty { "#" }
            }
            return "#"
        }

        fun castDataItemToGeneric(ctx: Context, dataItem: Any): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is ComposerItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name.ifEmpty { ctx.getString(R.string.unknown_composer) }
                val tempSubtitle = ""
                val tempDetails = ""
                tempResult.title = tempTitle
                tempResult.subtitle = tempSubtitle
                tempResult.details = tempDetails
                tempResult.imageUri = Uri.parse(dataItem.uriImage)
                tempResult.imageHashedSignature = dataItem.hashedCovertArtSignature
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