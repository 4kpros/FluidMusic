package com.prosabdev.fluidmusic.utils

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.provider.DocumentsContractCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.collections.SongItem
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.views.activities.MediaScannerActivityViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


abstract class MediaFileScanner {
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

        private var mFolderCounter = 0
        private var mSongsCounter = 0
        private var mPlaylistsCounter = 0
        suspend fun scanAudioFilesOnDevice(
            context: Context,
            folderUriTreeViewModel: FolderUriTreeViewModel,
            songItemViewModel: SongItemViewModel,
            mediaScannerActivityViewModel: MediaScannerActivityViewModel? = null
        ) = coroutineScope {
            mFolderCounter = 0
            mSongsCounter = 0
            mPlaylistsCounter = 0

            val tempFolderSelected: ArrayList<FolderUriTree> =
                folderUriTreeViewModel.getAllFolderUriTreesDirectly() as ArrayList<FolderUriTree>
            if (tempFolderSelected.isEmpty()){
                mediaScannerActivityViewModel?.setIsLoadingInBackground(false)
                mediaScannerActivityViewModel?.setIncrementEmptyFolderUriCounter()
                MainScope().launch {
                    Toast.makeText(context, "Please select folders to scan", Toast.LENGTH_LONG).show()
                }
                return@coroutineScope
            }

            withContext(Dispatchers.IO) {
                for (i in 0 until tempFolderSelected.size){
                    val uriTree: Uri = Uri.parse(tempFolderSelected[i].uriTree)
                    scanAudioFilesManually(
                        context,
                        uriTree,
                        songItemViewModel,
                        mediaScannerActivityViewModel
                    )
                }
            }
            MainScope().launch {
                Toast.makeText(context, "Scan finished", Toast.LENGTH_LONG).show()
            }
            mediaScannerActivityViewModel?.setIsLoadingInBackground(false)
        }

        private suspend fun scanAudioFilesManually(
            context: Context,
            uriTree: Uri,
            songItemViewModel: SongItemViewModel,
            mediaScannerActivityViewModel: MediaScannerActivityViewModel? = null
        ): Unit = coroutineScope {
            launch {
                val tempDocFile: DocumentFile? =
                    DocumentFile.fromTreeUri(context, uriTree)
                if(tempDocFile != null){
                    val foreachEnd = tempDocFile.listFiles().size
                    for (i in 0 until foreachEnd) {
                        if (tempDocFile.listFiles()[i].isDirectory) {
                            launch{
                                mFolderCounter++
                                Log.i(ConstantValues.TAG, "FOLDERS : $mFolderCounter")
                                mediaScannerActivityViewModel?.setFoldersCounter(mFolderCounter)
                                val tempRecursiveUri = tempDocFile.listFiles()[i].uri
                                scanAudioFilesManually(context, tempRecursiveUri, songItemViewModel, mediaScannerActivityViewModel)
                            }
                        }else{
                            launch{
                                val tempFFF = tempDocFile.listFiles()[i]
                                val tempMimeType: String? = tempFFF.type
                                if(tempMimeType?.contains("audio/") == true && !tempMimeType.contains("x-mpegurl")) {
                                    mSongsCounter++
                                    Log.i(ConstantValues.TAG, "SONGS : $mSongsCounter")
                                    mediaScannerActivityViewModel?.setSongsCounter(mSongsCounter)
                                    context.contentResolver.openAssetFileDescriptor(tempFFF.uri, "r").use {
                                        val mediaMetadataRetriever = MediaMetadataRetriever()
                                        mediaMetadataRetriever.setDataSource(it?.fileDescriptor)
                                        Log.i(
                                            ConstantValues.TAG,
                                            "Meta data title : ${
                                                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                            }"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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