package com.prosabdev.fluidmusic.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


abstract class MediaFileScanner {
    companion object {
        fun scanAudioFilesOnDevice(
            activity: Activity,
            viewModel : GenericSongItemDataListViewModel,
            startCursor: Int = 0,
            maxDataCount: Int = 50
        ){
            MainScope().launch{
                scanAudioFilesWithMediaStoreData(activity, viewModel, startCursor, maxDataCount)
            }
        }
        private suspend fun scanAudioFilesWithMediaStoreData (
            activity: Activity,
            viewModel : GenericSongItemDataListViewModel,
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
                val sortOrder: String = MediaStore.Audio.Media.DISPLAY_NAME + " ASC"
                activity.applicationContext.contentResolver.query(
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