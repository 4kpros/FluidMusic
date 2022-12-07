package com.prosabdev.fluidmusic.models.songitem

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.generic.GenericItemListGrid
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils

@Entity(
    foreignKeys = [
        ForeignKey(entity = FolderUriTree::class, parentColumns = ["id"], childColumns = ["uriTreeId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["uri"], unique = true)]
)
class SongItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var uriTreeId: Long = 0
    var uri: String? = null
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var composer: String? = null
    var album: String? = null
    var genre: String? = null
    var uriPath: String? = null
    var folder: String? = null
    var folderParent: String? = null
    var folderUri: String? = null
    var year: String? = null
    var duration: Long = 0
    var language: String? = null

    var typeMime: String? = null
    var sampleRate: Int = 0
    var bitrate: Double = 0.0

    var size: Long = 0

    var channelCount: Int = 0
    var fileExtension: String? = null
    var bitPerSample: String? = null

    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0

    var author: String? = null
    var diskNumber: String? = null
    var writer: String? = null
    var cdTrackNumber: String? = null
    var numberTracks: String? = null

    var comments: String? = null

    var rating: Int = 0
    var playCount: Int = 0
    var lastPlayed: Long = 0

    var hashedCovertArtSignature: Int = -1
    var isValid: Boolean = true

    @Ignore
    var position: Int = -1

    companion object {
        const val TAG = "SongItem"

        fun getStringIndexRequestFastScroller(ctx: Context, dataItem: Any): String {
            if(dataItem is SongItem) {
                return dataItem.title ?: dataItem.fileName ?: "#"
            }
            return "#"
        }
        fun castDataItemToGeneric(ctx: Context, dataItem: Any): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is SongItem) {
                tempResult = GenericItemListGrid()
                tempResult.title = dataItem.title ?: dataItem.fileName ?: ctx.getString(
                    com.prosabdev.fluidmusic.R.string.unknown_title)
                tempResult.subtitle = dataItem.artist ?: ctx.getString(com.prosabdev.fluidmusic.R.string.unknown_artist)
                tempResult.details = ctx.getString(
                    com.prosabdev.fluidmusic.R.string.item_song_card_text_details,
                    FormattersAndParsersUtils.formatSongDurationToString(dataItem.duration),
                    dataItem.fileExtension
                )
                tempResult.imageUri = Uri.parse(dataItem.uri)
                tempResult.imageHashedSignature = dataItem.hashedCovertArtSignature
            }
            return tempResult
        }

        val diffCallback = object : DiffUtil.ItemCallback<SongItem>() {
            override fun areItemsTheSame(
                oldItem: SongItem,
                newItem: SongItem
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem) =
                oldItem.id == newItem.id &&
                oldItem.uriTreeId == newItem.uriTreeId &&
                oldItem.uri == newItem.uri
        }
        val diffCallbackViewPager = object : DiffUtil.ItemCallback<SongItem>() {
            override fun areItemsTheSame(
                oldItem: SongItem,
                newItem: SongItem
            ): Boolean =
                false

            override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem) =
                oldItem == newItem
        }
    }
}