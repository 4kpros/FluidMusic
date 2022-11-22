package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.TagException
import org.jaudiotagger.tag.images.Artwork
import java.io.File
import java.io.FileDescriptor
import java.io.IOException


abstract class CustomAudioInfoExtractor {
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
                                val mdr = MediaMetadataRetriever()
                                mdr.setDataSource(afd.fileDescriptor)
                                result = mdr.embeddedPicture
                            }
                        }

                    }finally {

                    }

                }
                return@withContext result
            }
        }

        fun getAudioInfo(absolutePath: String?): SongItem {
            val songItem = SongItem()
            if (absolutePath != null && absolutePath.isNotEmpty()) {
                val tempFile = File(absolutePath)
                try {
                    val audioFile = AudioFileIO.read(tempFile)!!

                    // First get audio header tagger
                    val audioHeader = audioFile.audioHeader
                    val format = audioHeader.format
                    val bitrate = audioHeader.bitRateAsNumber.toDouble()
                    val sampleRate = audioHeader.sampleRateAsNumber / 1000

                    // After get audio content tagger
                    val tag: Tag = audioFile.tagOrCreateAndSetDefault

                    //Retrieve covert art
                    val artwork: Artwork? = tag.firstArtwork
                    val title: String = tag.getFirst(FieldKey.TITLE)
                    val artist: String = tag.getFirst(FieldKey.ARTIST)
                    val album: String = tag.getFirst(FieldKey.ALBUM)
                    val genre: String = tag.getFirst(FieldKey.GENRE)
                    val year: String = tag.getFirst(FieldKey.YEAR)
//                    songItem.covertArt = artwork
//                    songItem.covertArtUrl = artwork?.imageUrl
                    songItem.title = title
                    songItem.artist = artist
                    songItem.album = album
                    songItem.genre = genre
                    songItem.year = year
                    songItem.sampleRate = sampleRate
                    songItem.typeMime = format
                    songItem.bitrate = bitrate
                    Log.i(ConstantValues.TAG, "file title = $title")
                    Log.i(ConstantValues.TAG, "file artist = $artist")
                } catch (e: CannotReadException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: TagException) {
                    e.printStackTrace()
                } catch (e: ReadOnlyFileException) {
                    e.printStackTrace()
                } catch (e: InvalidAudioFrameException) {
                    e.printStackTrace()
                }
            }
            return songItem
        }

        //Extract bitmap audio artwork from path
        fun getBitmapAudioArtwork(binaryDataImage: ByteArray?, width: Int = 100, height: Int = 100): Bitmap? {
            if (binaryDataImage != null && binaryDataImage.isNotEmpty()) {
                var bitmap: Bitmap? = null
                val options: BitmapFactory.Options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(binaryDataImage, 0, binaryDataImage.size, options)

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, width, height)

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                try {
                    bitmap = BitmapFactory.decodeByteArray(
                        binaryDataImage,
                        0,
                        binaryDataImage.size,
                        options
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (bitmap != null)
                    return bitmap
            }
//            BitmapFactory.decodeResource(
//                context.resources,
//                R.drawable.fluid_music_image
//            )
            return null
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val height: Int = options.outHeight
            val width: Int = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while (halfHeight / inSampleSize >= reqHeight
                    && halfWidth / inSampleSize >= reqWidth
                ) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }

        private fun getAudioBinaryDataArtwork(absolutePath: String?): ByteArray {
            var binaryDataArtwork = ByteArray(0)
            if (absolutePath != null && absolutePath.isNotEmpty()) {
                val tempFile = File(absolutePath)
                try {
                    val audioFile = AudioFileIO.read(tempFile)!!
                    // After get audio content tagger
                    val tag: Tag = audioFile.tagOrCreateAndSetDefault!!
                    binaryDataArtwork = tag.firstArtwork.binaryData
                } catch (e: CannotReadException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: TagException) {
                    e.printStackTrace()
                } catch (e: ReadOnlyFileException) {
                    e.printStackTrace()
                } catch (e: InvalidAudioFrameException) {
                    e.printStackTrace()
                }
            }
            return binaryDataArtwork
        }

        fun getAudioArtwork(absolutePath: String?): Artwork? {
            var artwork: Artwork? = null
            if (absolutePath != null && absolutePath.isNotEmpty()) {
                val tempFile = File(absolutePath)
                try {
                    val audioFile = AudioFileIO.read(tempFile)!!
                    // After get audio content tagger
                    val tag: Tag = audioFile.tagOrCreateAndSetDefault!!
                    artwork = tag.firstArtwork
                } catch (e: CannotReadException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: TagException) {
                    e.printStackTrace()
                } catch (e: ReadOnlyFileException) {
                    e.printStackTrace()
                } catch (e: InvalidAudioFrameException) {
                    e.printStackTrace()
                }
            }
            return artwork
        }
    }
}