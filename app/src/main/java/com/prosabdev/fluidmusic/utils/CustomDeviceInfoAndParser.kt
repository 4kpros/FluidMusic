package com.prosabdev.fluidmusic.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.provider.DocumentsContractCompat
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


abstract class CustomDeviceInfoAndParser {
    companion object {
        private const val STORAGE_ID_PRIMARY = "primary"
        private const val STORAGE_ID_DATA = "data"

        private fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                first.uppercaseChar().toString() + s.substring(1)
            }
        }

        fun formatAndReturnFolderUriSAF(context: Context, uri: Uri): FolderUriTree {
            val documentFile = DocumentFile.fromTreeUri(context, uri)

            val tempFolderUriTree = FolderUriTree()
            tempFolderUriTree.uriTree = documentFile?.uri.toString()
            tempFolderUriTree.lastPathSegment = uri.lastPathSegment
            tempFolderUriTree.pathTree = uri.path.toString().trim()
            tempFolderUriTree.normalizeScheme = uri.normalizeScheme().toString()
            tempFolderUriTree.path = "/${(uri.lastPathSegment ?: "").substringAfter(":")}"
            tempFolderUriTree.deviceName =
                if ((uri.lastPathSegment ?: "").substringBefore(":") == STORAGE_ID_PRIMARY)
                    getDeviceName()
                else
                    getDeviceName()
            return tempFolderUriTree
        }

        fun getSampleRateForAudio(assetFileDescriptor: AssetFileDescriptor?) {
            if(assetFileDescriptor == null)
                return
            var extractor : MediaExtractor? = MediaExtractor()
            if(extractor == null)
                return
            extractor.setDataSource(assetFileDescriptor.fileDescriptor)
            val numTracks : Int = extractor.trackCount
            for (i in 0 until numTracks) {
                val format : MediaFormat = extractor.getTrackFormat(i)

                val mime : String = format.getString(MediaFormat.KEY_MIME) ?: ""
                val duration : Long = format.getLong(MediaFormat.KEY_DURATION)
                val max_input_size : String = format.getString(MediaFormat.KEY_MAX_INPUT_SIZE) ?: ""

                val sample_rate : Int = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val channel_count : Int = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                val channel_mask : String = format.getString(MediaFormat.KEY_CHANNEL_MASK) ?: ""
                val is_adts : String = format.getString(MediaFormat.KEY_IS_ADTS) ?: ""

                Log.i(ConstantValues.TAG, "-------------------------------------------->")
                Log.i(ConstantValues.TAG, "TRACK mime : ${mime}")
                Log.i(ConstantValues.TAG, "TRACK duration : ${duration}")
                Log.i(ConstantValues.TAG, "TRACK max_input_size : ${max_input_size}")
                Log.i(ConstantValues.TAG, "TRACK sample_rate : ${sample_rate}")
                Log.i(ConstantValues.TAG, "TRACK channel_count : ${channel_count}")
                Log.i(ConstantValues.TAG, "TRACK channel_mask : ${channel_mask}")
                Log.i(ConstantValues.TAG, "TRACK is_adts : ${is_adts}")
                extractor.selectTrack(i)
            }
            val inputBuffer : ByteBuffer = ByteBuffer.allocate(100)
            while (extractor.readSampleData(inputBuffer, 0) >= 0) {
                val trackIndex : Int = extractor.sampleTrackIndex
                val presentationTimeUs : Long = extractor.sampleTime
                Log.i(ConstantValues.TAG, "TRACK trackIndex : ${trackIndex}")
                Log.i(ConstantValues.TAG, "TRACK presentationTimeUs : ${presentationTimeUs}")
                extractor.advance()
            }

            extractor.release()
            extractor = null
        }

        private fun searchDirectoryEntries(context: Context, uri: Uri) {
            val contentResolver: ContentResolver = context.applicationContext.contentResolver
            val childrenUri: Uri? = DocumentsContractCompat.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContractCompat.getTreeDocumentId(uri).toString()
            )
            Log.i(ConstantValues.TAG, "childrenUri = ${childrenUri?.path.toString()}")
            val projection = arrayOf<String>(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE
            )
            val selection: String = DocumentsContract.Document.COLUMN_MIME_TYPE +
                    " == ?"
            val selectionArgs = arrayOf<String>(
                "audio/flac"
            )
            val sortOrder: String = DocumentsContract.Document.COLUMN_DISPLAY_NAME + " ASC"
            contentResolver.query(
                childrenUri ?: Uri.EMPTY,
                projection,
                selection,
                selectionArgs,
                sortOrder
            ).use {
                if (it == null)
                    return
                val displayNameIndex =
                    it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeTypeIndex: Int =
                    it.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                while (it.moveToNext()) {
                    val tempMimeType: String = it.getString(mimeTypeIndex)
                    if(tempMimeType.startsWith("audio")){
                        Log.i(
                            ConstantValues.TAG,
                            "Display name: ${it.getString(displayNameIndex)} -----> Mime type: ${
                                it.getString(mimeTypeIndex)
                            }")
                    }
                }
            }
        }

        private fun extractAbsolutePathFromUri(context: Context, uri: Uri): String {
            val basePath: String = (uri.lastPathSegment ?: "").substringAfter(":")
            val storageId: String = (uri.lastPathSegment ?: "").substringBefore(":")
            val result = if (storageId.isEmpty())
                ""
            else
                when (storageId) {
                    STORAGE_ID_PRIMARY -> {
                        "${Environment.getExternalStorageDirectory().absolutePath}/$basePath".trimEnd(
                            '/'
                        )
                    }
                    STORAGE_ID_DATA -> {
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

        private fun getFileInfoFromMediaStore(context: Context, uriTree: Uri) {
            val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val projection = arrayOf<String>(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.SIZE,
            )
            val selection: String = MediaStore.Audio.Media.DURATION +
                    " >= ?"
            val selectionArgs = arrayOf<String>(
                java.lang.String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
            )
            val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"
            context.applicationContext.contentResolver.query(
//                Uri.parse("content://media/external/audio/media"),
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            ).use { cursor ->
                val displayNameIndex =
                    cursor?.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME) ?: -1
                val sizeIndex: Int = cursor?.getColumnIndex(MediaStore.Audio.Media.SIZE) ?: -1
                if (cursor != null) {
                    while (cursor.moveToNext()) {
//                        Log.i(ConstantValues.TAG, "Collection: ${collection}")
                        Log.i(
                            ConstantValues.TAG,
                            "Display Name: ${cursor.getString(displayNameIndex)}"
                        )
                        Log.i(ConstantValues.TAG, "sizeIndex: ${cursor.getString(sizeIndex)}")
                    }
                }
            }
        }

        private suspend fun scanAudioFilesWithMediaStoreData (
            context: Context
        ) = coroutineScope {
            launch(context = Dispatchers.IO) {
                val tempSongList: ArrayList<SongItem> = ArrayList()
                val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val projection = arrayOf<String>(
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.MIME_TYPE
                )
                val selection: String = MediaStore.Audio.Media.DURATION +
                        " >= ?"
                val selectionArgs = arrayOf<String>(
                    java.lang.String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                )
                val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"
                context.applicationContext.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                ).use { cursor ->
                    val absolutePathColumn: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA) ?: -1
                    val idColumn: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) ?: -1
                    val nameColumn: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME) ?: -1
                    val durationColumn: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) ?: -1
                    val mimeTypeColumn: Int =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE) ?: -1

                    if(cursor != null){
                        while (cursor.moveToNext()) {
                            Log.i(ConstantValues.TAG, "File found --> ${cursor.getString(nameColumn)}")
                        }
                    }
                }
            }
        }
    }
}