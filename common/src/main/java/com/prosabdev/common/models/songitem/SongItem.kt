package com.prosabdev.common.models.songitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.common.R
import com.prosabdev.common.models.FolderUriTree
import com.prosabdev.common.models.generic.GenericItemListGrid
import com.prosabdev.common.utils.AudioInfoExtractor
import com.prosabdev.common.utils.FormattersAndParsers
import java.lang.NumberFormatException

@Entity(
    foreignKeys = [
        ForeignKey(entity = FolderUriTree::class, parentColumns = ["id"], childColumns = ["uriTreeId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["uri"], unique = true), Index(value = ["uriTreeId"])]
)
data class SongItem (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var uriTreeId: Long = 0,
    var uri: String? = "",
    var fileName: String? = "",
    var title: String? = "",
    var artist: String? = "",
    var albumArtist: String? = "",
    var composer: String? = "",
    var album: String? = "",
    var genre: String? = "",
    var uriPath: String? = "",
    var folder: String? = "",
    var folderParent: String? = "",
    var folderUri: String? = "",
    var year: String? = "",
    var duration: Long = 0,
    var language: String? = "",

    var typeMime: String? = "",
    var sampleRate: Int = 0,
    var bitrate: Double = 0.0,

    var size: Long = 0,

    var channelCount: Int = 0,
    var fileExtension: String? = "",
    var bitPerSample: String? = "",

    var lastUpdateDate: Long = 0,
    var lastAddedDateToLibrary: Long = 0,

    var author: String? = "",
    var diskNumber: String? = "",
    var writer: String? = "",
    var cdTrackNumber: String? = "",
    var numberTracks: String? = "",

    var comments: String? = "",
    var lyrics: String? = "",

    var rating: Int = 0,
    var playCount: Int = 0,
    var lastPlayed: Long = 0,

    var hashedCovertArtSignature: Int = -1,
    var isValid: Boolean = true,

    @Ignore
    var position: Int = -1
){
    companion object {
        const val TAG = "SongItem"
        const val DEFAULT_INDEX = "title"

        const val EXTRAS_MEDIA_URI = "EXTRAS_MEDIA_URI"
        const val EXTRAS_DURATION = "EXTRAS_DURATION"
        const val EXTRAS_LYRICS = "EXTRAS_LYRICS"
        const val EXTRAS_IMAGE_SIGNATURE = "EXTRAS_IMAGE_SIGNATURE"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is SongItem) {
                return dataItem.uri ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is SongItem) {
                return dataItem.title?.ifEmpty { dataItem.fileName } ?: ""
            }
            return "#"
        }
         fun castDataItemToGeneric(ctx: Context, dataItem: Any?): GenericItemListGrid? {
            var tempResult : GenericItemListGrid? = null
            if(dataItem is SongItem) {
                tempResult = GenericItemListGrid()
                tempResult.title = dataItem.title?.ifEmpty { dataItem.fileName ?: ctx.getString(R.string.unknown_title) } ?: dataItem.fileName ?: ctx.getString(R.string.unknown_title)
                tempResult.subtitle = dataItem.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)
                tempResult.details = ctx.getString(
                    R.string.item_song_card_text_details,
                    FormattersAndParsers.formatSongDurationToString(dataItem.duration),
                    dataItem.fileExtension
                )
                if(dataItem.uri?.isNotEmpty() == true){
                    tempResult.mediaUri = Uri.parse(dataItem.uri)
                }
                tempResult.hashedCovertArtSignature = dataItem.hashedCovertArtSignature
            }else if(dataItem is MediaItem) {
                tempResult = GenericItemListGrid()
                tempResult.title = (dataItem.mediaMetadata.title?.ifEmpty { ctx.getString(R.string.unknown_title) } ?: ctx.getString(R.string.unknown_title)).toString()
                tempResult.subtitle = (dataItem.mediaMetadata.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist)).toString()
                tempResult.details = dataItem.mediaMetadata.description.toString()
                tempResult.mediaUri = Uri.parse(dataItem.mediaMetadata.extras?.getString(
                    EXTRAS_MEDIA_URI))
                tempResult.hashedCovertArtSignature = dataItem.mediaMetadata.extras?.getInt(EXTRAS_IMAGE_SIGNATURE) ?: -1
            }
            return tempResult
        }

        fun castSongItemToMediaItem(ctx: Context, item: SongItem): MediaItem {
            var tempDiskNumber = 0
            try {
                tempDiskNumber = item.diskNumber?.toInt() ?: 0
            }catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            var tempYear = 0
            try {
                tempYear = item.year?.toInt() ?: 0
            }catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            var tempUri = Uri.EMPTY
            try {
                tempUri = Uri.parse(item.uri.toString())
            }catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            val extra = Bundle()
            extra.putString(EXTRAS_MEDIA_URI, item.uri)
            extra.putLong(EXTRAS_DURATION, item.duration)
            extra.putString(EXTRAS_LYRICS, item.lyrics)
            extra.putInt(EXTRAS_IMAGE_SIGNATURE, item.hashedCovertArtSignature)

            return if(item.hashedCovertArtSignature >= 0){
                MediaItem.Builder()
                    .setUri(tempUri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                            .setTitle(item.title?.ifEmpty { item.fileName ?: ctx.getString(R.string.unknown_title) } ?: item.fileName ?: ctx.getString(R.string.unknown_title))
                            .setArtist(item.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist))
                            .setAlbumTitle(item.album)
                            .setAlbumArtist(item.albumArtist)
                            .setGenre(item.genre)
                            .setDescription(
                                ctx.getString(
                                    R.string.item_song_card_text_details,
                                    FormattersAndParsers.formatSongDurationToString(item.duration),
                                    item.fileExtension
                                )
                            )
                            .setComposer(item.composer)
                            .setDiscNumber(tempDiskNumber)
                            .setWriter(item.writer)
                            .setReleaseYear(tempYear)
                            .setIsPlayable(true)
                            .setIsBrowsable(false)
                            .setExtras(extra)
                            .build()
                    )
                    .build()
            }else{
                MediaItem.Builder()
                    .setUri(tempUri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                            .setTitle(item.title?.ifEmpty { item.fileName ?: ctx.getString(R.string.unknown_title) } ?: item.fileName ?: ctx.getString(R.string.unknown_title))
                            .setArtist(item.artist?.ifEmpty { ctx.getString(R.string.unknown_artist) } ?: ctx.getString(R.string.unknown_artist))
                            .setAlbumTitle(item.album)
                            .setAlbumArtist(item.albumArtist)
                            .setGenre(item.genre)
                            .setDescription(
                                ctx.getString(
                                    R.string.item_song_card_text_details,
                                    FormattersAndParsers.formatSongDurationToString(item.duration),
                                    item.fileExtension
                                )
                            )
                            .setComposer(item.composer)
                            .setDiscNumber(tempDiskNumber)
                            .setWriter(item.writer)
                            .setReleaseYear(tempYear)
                            .setArtworkUri(null)
                            .setArtworkData(
                                null,
                                MediaMetadata.PICTURE_TYPE_MEDIA
                            )
                            .maybeSetArtworkData(ByteArray(0), MediaMetadata.PICTURE_TYPE_MEDIA)
                            .setIsPlayable(true)
                            .setIsBrowsable(false)
                            .setExtras(extra)
                            .build()
                    )
                    .build()
            }
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

        val diffCallbackViewPagerSongItem = object : DiffUtil.ItemCallback<SongItem>() {
            override fun areItemsTheSame(
                oldItem: SongItem,
                newItem: SongItem
            ): Boolean =
                false
            override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem) =
                oldItem == newItem
        }

        val diffCallbackMediaItem = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(
                oldItem: MediaItem,
                newItem: MediaItem
            ): Boolean = false

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem) =
                oldItem.mediaId == newItem.mediaId &&
                        oldItem.mediaMetadata.title.toString() == newItem.mediaMetadata.title.toString() &&
                        oldItem.mediaMetadata.artist.toString() == newItem.mediaMetadata.artist.toString() &&
                        oldItem.mediaMetadata.mediaType.toString() == newItem.mediaMetadata.mediaType.toString()
        }
    }
}