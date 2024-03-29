package com.prosabdev.common.utils

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.common.models.songitem.SongItem
import java.io.FileDescriptor

/**
 * Singleton class to extract audio information
 */
object AudioInfoExtractor {

    /**
     * This method extract audio information from [Uri] and return a [SongItem]
     */
    fun extractAudioInfoFromUri(
        ctx: Context,
        tempFFF: DocumentFile?,
        fileDescriptor: FileDescriptor?
    ): SongItem? {
        if(tempFFF == null || tempFFF.uri == Uri.EMPTY || fileDescriptor == null) return null

        val tempSong = SongItem()
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()

            mediaMetadataRetriever.setDataSource(fileDescriptor)
            mediaMetadataRetriever.use { mdr ->
                tempSong.uri = tempFFF.uri.toString()
                tempSong.fileName = tempFFF.name ?: ""
                tempSong.uriPath = tempFFF.uri.lastPathSegment ?: ""
                val extension: String =
                    tempFFF.uri.lastPathSegment.toString().substringAfterLast(".").uppercase()
                tempSong.fileExtension = extension
                tempSong.size = tempFFF.length()
                tempSong.title = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
                tempSong.title = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
                tempSong.artist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
                tempSong.composer = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: ""
                tempSong.album = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
                tempSong.albumArtist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: ""
                tempSong.genre = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""

                tempSong.year = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: ""
                tempSong.duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
                tempSong.typeMime = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
                tempSong.bitrate = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toDouble() ?: 0.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    tempSong.bitPerSample = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE) ?: ""
                }
                tempSong.author = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR) ?: ""
                tempSong.diskNumber = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER) ?: ""
                tempSong.cdTrackNumber = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER) ?: ""
                tempSong.writer = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: ""
                tempSong.numberTracks = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS) ?: ""

                val tempUpdatedDate: Long = tempFFF.lastModified()
                tempSong.lastUpdateDate = tempUpdatedDate
                tempSong.lastAddedDateToLibrary = SystemSettings.getCurrentDateInMillis()

                val tempBinary : ByteArray? = extractImageBinaryDataFromAudioUri(ctx,
                    tempFFF.uri
                )
                if(tempBinary != null){
                    val tempHashedImage: Int = tempBinary.decodeToString().hashCode()
                    tempSong.hashedCovertArtSignature = if (tempHashedImage < 0) tempHashedImage * -1 else tempHashedImage
                }else{
                    tempSong.hashedCovertArtSignature = -1
                }
                val mediaExtractor = MediaExtractor()
                mediaExtractor.setDataSource(fileDescriptor)
                val numTracks : Int = mediaExtractor.trackCount
                for (i in 0 until numTracks) {
                    val format : MediaFormat = mediaExtractor.getTrackFormat(i)
                    tempSong.sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    tempSong.language = format.getString(MediaFormat.KEY_LANGUAGE) ?: ""
                    tempSong.channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        try {
                            tempSong.bitPerSample = "${format.getInteger("bits-per-sample")}bit"
                        }catch (error: Throwable) {
                            error.printStackTrace()
                        }
                    }
                }
            }
        } catch (error: Throwable) {
            error.printStackTrace()
        }
        return tempSong
    }

    /**
     * This method extract image binary data from [Uri]. The return response is [ByteArray] who is binary data of extracted image
     */
    fun extractImageBinaryDataFromAudioUri(ctx: Context, uri : Uri?): ByteArray? {
        var result : ByteArray? = null
        if (uri != null && uri.toString().isNotEmpty()){
            try {
                ctx.contentResolver.openAssetFileDescriptor(uri, "r").use { afd ->
                    if (afd != null) {
                        val mdr = MediaMetadataRetriever()
                        mdr.setDataSource(afd.fileDescriptor)
                        result = mdr.embeddedPicture
                    }
                }

            }finally {

            }
        }
        return result
    }

    /**
     * Return the absolute path of [Uri]. The response is [String] who is absolute path
     */
    private fun getAbsolutePathFromUri(context: Context, uri: Uri): String {
        val basePath: String = (uri.lastPathSegment ?: "").substringAfter(":")
        val storageId: String = (uri.lastPathSegment ?: "").substringBefore(":")
        val result = if (storageId.isEmpty())
            ""
        else
            when (storageId) {
                DeviceInfo.STORAGE_ID_PRIMARY -> {
                    "${Environment.getExternalStorageDirectory().absolutePath}/$basePath".trimEnd(
                        '/'
                    )
                }
                DeviceInfo.STORAGE_ID_DATA -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        "${context.dataDir.path}/$basePath".trimEnd('/')
                    } else {
                        "${
                            (context.filesDir.path).toString().substringBeforeLast('/')
                        }/$basePath".trimEnd('/')
                    }
                }
                else -> {
                    "/storage/$storageId/$basePath".trimEnd('/')
                }
            }
        return result
    }
}