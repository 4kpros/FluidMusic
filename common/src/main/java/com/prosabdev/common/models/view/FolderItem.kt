package com.prosabdev.common.models.view

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView
import com.prosabdev.common.R
import com.prosabdev.common.models.generic.GenericItemListGrid
import com.prosabdev.common.utils.FormattersAndParsers

@DatabaseView(
    "SELECT songItem.folder as name, " +
            "folderUriTree.deviceName as deviceName, " +
            "MAX(songItem.folderParent) as parentFolder, " +
            "MAX(songItem.year) as year, " +
            "MAX(songItem.lastUpdateDate) as lastUpdateDate, " +
            "MAX(songItem.lastAddedDateToLibrary) as lastAddedDateToLibrary, " +
            "COUNT(DISTINCT songItem.artist) as numberArtists, " +
            "COUNT(DISTINCT songItem.album) as numberAlbums, " +
            "COUNT(DISTINCT songItem.albumArtist) as numberAlbumArtists, " +
            "COUNT(DISTINCT songItem.composer) as numberComposers, " +
            "COUNT(songItem.id) as numberTracks, " +
            "SUM(songItem.duration) as totalDuration, " +
            "(SELECT tempSI.hashedCovertArtSignature FROM SongItem as tempSI WHERE tempSI.folder = songItem.folder AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.folder,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as hashedCovertArtSignature," +
            "(SELECT tempSI.uri FROM SongItem as tempSI WHERE tempSI.folder = songItem.folder AND tempSI.hashedCovertArtSignature > -1 " +
            "ORDER BY COALESCE(NULLIF(tempSI.folder,''), tempSI.fileName) COLLATE NOCASE ASC LIMIT 1" +
            ") as uriImage " +
            "FROM SongItem as songItem " +
            "INNER JOIN FolderUriTree as folderUriTree ON songItem.uriTreeId = folderUriTree.id " +
            "GROUP BY SongItem.folder ORDER BY SongItem.folder"
)
data class FolderItem (
    var name: String? = "",
    var deviceName: String? = "",
    var parentFolder: String? = "",
    var year: String? = "",
    var lastUpdateDate: Long = 0,
    var lastAddedDateToLibrary: Long = 0,
    var numberArtists: Int = 0,
    var numberAlbums: Int = 0,
    var numberAlbumArtists: Int = 0,
    var numberComposers: Int = 0,
    var numberTracks: Int = 0,
    var totalDuration: Long = 0,
    var hashedCovertArtSignature: Int = -1,
    var uriImage: String? = ""
){
    companion object {
        const val TAG = "FolderItem"
        const val DEFAULT_INDEX = "name"
        const val INDEX_COLUM_TO_SONG_ITEM = "folder"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is FolderItem) {
                return dataItem.name?.ifEmpty { "" } ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is FolderItem) {
                return dataItem.name?.ifEmpty { "#" } ?: "#"
            }
            return "#"
        }

        fun castDataItemToGeneric(ctx: Context, dataItem: Any?, setAllText: Boolean = false): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is FolderItem) {
                tempResult = GenericItemListGrid()
                val tempTitle : String = dataItem.name?.ifEmpty { ctx.getString(R.string.unknown_folder) } ?: ctx.getString(R.string.unknown_folder)
                val tempSubtitle : String = dataItem.parentFolder?.ifEmpty { dataItem.deviceName ?: "/" } ?: dataItem.deviceName ?: "/"
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
                tempResult.title = "/${tempTitle}"
                tempResult.subtitle = if(tempSubtitle == dataItem.deviceName) tempSubtitle else "/${tempSubtitle}"
                tempResult.details = tempDetails
                if(dataItem.uriImage?.isNotEmpty() == true){
                    tempResult.imageUri = Uri.parse(dataItem.uriImage)
                }
                tempResult.imageHashedSignature = dataItem.hashedCovertArtSignature
            }
            return tempResult
        }

        val diffCallback = object : DiffUtil.ItemCallback<FolderItem>() {
            override fun areItemsTheSame(
                oldItem: FolderItem,
                newItem: FolderItem
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: FolderItem, newItem: FolderItem) =
                oldItem.name == newItem.name &&
                        oldItem.parentFolder == newItem.parentFolder &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                        oldItem.numberArtists == newItem.numberArtists &&
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