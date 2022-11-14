package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.models.FolderSAF
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.viewmodels.MediaScannerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


abstract class MediaFileScanner {
    companion object {
        fun getDeviceName(): String {
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
        suspend fun scanAudioFilesOnDevice(
            context: Context,
            viewModel : MediaScannerActivityViewModel,
            startCursor: Int = 0,
            maxDataCount: Int = 50
        ) {
            val tempFolderSelected : List<FolderSAF>? = SharedPreferenceManager.loadSelectionFolderFromSAF(context)
            if(tempFolderSelected == null || tempFolderSelected.isEmpty())
                return
            val finalTempFolderSelected = tempFolderSelected as ArrayList<FolderSAF>
            val uriTree : Uri = Uri.parse(finalTempFolderSelected[0].uriTree)
//            scanAudioFilesWithMediaStoreData(context, viewModel, startCursor, maxDataCount)
            scanAudioFilesManually(context, viewModel, startCursor, maxDataCount, uriTree)
            readFiles(context, uriTree)
        }
        private suspend fun scanAudioFilesManually(
            context: Context,
            viewModel: MediaScannerActivityViewModel,
            startCursor: Int = 0,
            maxDataCount: Int = 50,
            uriTree: Uri
        ) = coroutineScope {
            val tempDocFile: DocumentFile = DocumentFile.fromTreeUri(context, uriTree) ?: return@coroutineScope
            val tempSongList: ArrayList<SongItem> = ArrayList()
            if(tempDocFile.listFiles().isNotEmpty()){
                MainScope().launch {
                    viewModel.setFoldersCounter(0)
                    viewModel.setSongsCounter(0)
                    viewModel.setPlaylistsCounter(0)
                }
                launch(context = Dispatchers.IO) {
                    var tempFoldersCount = 0
                    var tempSongsCount = 0
                    var tempPlaylistsCount = 0
                    for (i in 0 until tempDocFile.listFiles().size) {
                        if (tempDocFile.listFiles()[i].isDirectory) {
                            tempFoldersCount++
                            MainScope().launch {
                                viewModel.setFoldersCounter(tempFoldersCount)
                            }
                        } else if (tempDocFile.listFiles()[i].isFile) {
                            tempSongsCount++
                            MainScope().launch {
                                viewModel.setSongsCounter(tempSongsCount)
                            }
                        } else {
                            tempPlaylistsCount++
                            MainScope().launch {
                                viewModel.setPlaylistsCounter(tempPlaylistsCount)
                            }
                        }
                        Log.i(
                            ConstantValues.TAG,
                            "File = ${tempDocFile.listFiles()[i].name}, -----> Type = ${tempDocFile.listFiles()[i].type}"
                        )
                    }
                    MainScope().launch {
                        viewModel.setSongList(tempSongList)
                        viewModel.setDataRequestCounter(1)
                        viewModel.setIsLoading(false)
                        viewModel.setIsLoadingInBackground(false)
                    }
                }
            }else{
                MainScope().launch {
                    viewModel.setSongList(tempSongList)
                    viewModel.setDataRequestCounter(1)
                    viewModel.setIsLoading(false)
                    viewModel.setIsLoadingInBackground(false)
                }
            }
        }
        private fun readFiles(context: Context, uriTree: Uri): List<Uri> {
            val uriList: MutableList<Uri> = ArrayList()
            val uriFolder: Uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uriTree,
                DocumentsContract.getTreeDocumentId(uriTree)
            )
            Log.i(ConstantValues.TAG, "Tree document ID : $uriFolder")
            var cursor: Cursor? = null
            try {
                val selection: String = MediaStore.Audio.Media.DURATION +
                        " >= ?"
                val selectionArgs = arrayOf<String>(
                    java.lang.String.valueOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                )
                val sortOrder: String = MediaStore.Audio.Media.TITLE + " ASC"
                // let's query the files
                cursor = context.contentResolver.query(
                    uriFolder, arrayOf(
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                        DocumentsContract.Document.COLUMN_SIZE
                    ),
                    selection, selectionArgs, sortOrder
                )
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        // build the uri for the file
                        val uriFile: Uri = DocumentsContract.buildDocumentUriUsingTree(
                            uriTree,
                            cursor.getString(0)
                        )
                        //add to the list
                        uriList.add(uriFile)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                // TODO: handle error
            } finally {
                cursor?.close()
            }

            //return the list
            return uriList
        }

        private suspend fun scanAudioFilesWithMediaStoreData (
            context: Context,
            viewModel: GenericSongItemDataListViewModel,
            startCursor: Int = 0,
            maxDataCount: Int = 50
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

                    var maxDataCounter = 0
                    if(cursor?.moveToPosition(startCursor) == true){
                        ++maxDataCounter
                        addSongToTempData(cursor, tempSongList, absolutePathColumn, idColumn, nameColumn, durationColumn, mimeTypeColumn)
                        if(maxDataCount > maxDataCounter) {
                            while (cursor.moveToNext() && maxDataCount > maxDataCounter) {
                                addSongToTempData(cursor, tempSongList, absolutePathColumn, idColumn, nameColumn, durationColumn, mimeTypeColumn)
                                ++maxDataCounter
                            }
                        }
                    }
                    MainScope().launch {
                        viewModel.setSongList(tempSongList)
                        viewModel.setDataRequestCounter(1)
                        viewModel.setIsLoading(false)
                        viewModel.setIsLoadingInBackground(false)
                    }
                }
            }
        }

        private fun addSongToTempData(
            cursor: Cursor,
            tempSongList: ArrayList<SongItem>,
            absolutePathColumn: Int,
            idColumn: Int,
            nameColumn: Int,
            durationColumn: Int,
            mimeTypeColumn: Int
        ) {
            val absolutePath: String = cursor.getString(absolutePathColumn) ?: ""
            val id: Long = (cursor.getInt(idColumn)).toLong()
            val name: String = cursor.getString(nameColumn) ?: ""
            val duration: Long = cursor.getLong(durationColumn)
            val mimeType: String = cursor.getString(mimeTypeColumn) ?: ""

            Log.i(ConstantValues.TAG, "SONG --> ${absolutePath} ")
            val songItem: SongItem =
                AudioFileInfoExtractor.getAudioInfo(absolutePath)
            songItem.id = id
            songItem.absolutePath = absolutePath
            songItem.fileName = name
            songItem.duration = duration
            songItem.typeMime = mimeType
            tempSongList.add(songItem)
        }
    }
}