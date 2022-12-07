package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.models.songitem.SongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileDescriptor


abstract class AudioInfoExtractorUtils {
    companion object {

        suspend fun extractImageBitmapFromAudioUri(ctx: Context, uri : Uri?): Bitmap? {
            return withContext(Dispatchers.IO) {
                var result : Bitmap? = null
                if (uri != null && uri.toString().isNotEmpty()){
                    try {
                        ctx.contentResolver.openAssetFileDescriptor(uri, "r").use { afd ->
                            if (afd != null) {
                                val fd: FileDescriptor = afd.fileDescriptor
                                result = BitmapFactory.decodeFileDescriptor(fd)
                            }
                        }

                    }finally {

                    }
                }
                return@withContext result
            }
        }
        suspend fun extractImageBinaryDataFromAudioUri(ctx: Context, uri : Uri?): ByteArray? {
            return withContext(Dispatchers.IO) {
                var result : ByteArray? = null
                if (uri != null && uri.toString().isNotEmpty()){
                    try {
                        ctx.contentResolver.openAssetFileDescriptor(uri, "r").use { afd ->
                            if (afd != null) {
                                val mmdr = MediaMetadataRetriever()
                                mmdr.setDataSource(afd.fileDescriptor)
                                result = mmdr.embeddedPicture
                            }
                        }

                    }finally {

                    }

                }
                return@withContext result
            }
        }

        suspend fun extractAudioInfoFromUri(ctx : Context, tempFFF : DocumentFile?): SongItem? {
            if(tempFFF == null || tempFFF.uri == Uri.EMPTY) return null

            val tempSong = SongItem()
            ctx.contentResolver.openAssetFileDescriptor(
                tempFFF.uri,
                "r"
            ).use { afD ->
                try {
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    val fileDescriptor = afD?.fileDescriptor ?: return null
                    mediaMetadataRetriever.setDataSource(fileDescriptor)
                    mediaMetadataRetriever.use { mdr ->
                        tempSong.uri = tempFFF.uri.toString()
                        tempSong.fileName = tempFFF.name
                        tempSong.uriPath = tempFFF.uri.lastPathSegment
                        val extension: String =
                            tempFFF.uri.lastPathSegment.toString().substringAfterLast(".").uppercase()
                        tempSong.fileExtension = extension
                        tempSong.size = tempFFF.length() ?: 0
                        tempSong.title = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        tempSong.artist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        tempSong.composer = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
                        tempSong.album = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                        tempSong.albumArtist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                        tempSong.genre = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

                        tempSong.year = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                        tempSong.duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
                        tempSong.typeMime = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                        tempSong.bitrate = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toDouble() ?: 0.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //For versions under 31, checkout at the end of this block
                            tempSong.bitPerSample = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE)
                        }
                        tempSong.author = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
                        tempSong.diskNumber = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)
                        tempSong.cdTrackNumber = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                        tempSong.writer = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)
                        tempSong.numberTracks = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)

                        val tempUpdatedDate: Long = tempFFF.lastModified()
                        tempSong.lastUpdateDate = tempUpdatedDate
                        tempSong.lastAddedDateToLibrary = SystemSettingsUtils.getCurrentDateInMilli()

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
                            tempSong.language = format.getString(MediaFormat.KEY_LANGUAGE)
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
            }
            return tempSong
        }

        private fun getAbsolutePathFromUri(context: Context, uri: Uri): String {
            val basePath: String = (uri.lastPathSegment ?: "").substringAfter(":")
            val storageId: String = (uri.lastPathSegment ?: "").substringBefore(":")
            val result = if (storageId.isEmpty())
                ""
            else
                when (storageId) {
                    DeviceInfoUtils.STORAGE_ID_PRIMARY -> {
                        "${Environment.getExternalStorageDirectory().absolutePath}/$basePath".trimEnd(
                            '/'
                        )
                    }
                    DeviceInfoUtils.STORAGE_ID_DATA -> {
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
}