package com.prosabdev.fluidmusic.utils

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.viewmodels.GenericDataListFetcherViewModel
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


abstract class MediaFileScanner {
    companion object {
        fun refreshGallery(activity: Activity) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            scanIntent.data = Uri.fromFile(File("temp_file.txt"))
            activity.sendBroadcast(scanIntent)
            val pathsArray = arrayOf<String>(
                Environment.getExternalStorageDirectory().toString()
            )
            MediaScannerConnection.scanFile(
                activity,
                pathsArray,
                null,
                object : MediaScannerConnection.OnScanCompletedListener {
                    override fun onScanCompleted(path: String?, uri: Uri?) {
                        Log.i(ConstantValues.TAG, "onScanCompleted !")
                        Toast.makeText(activity, "onScanCompleted !", Toast.LENGTH_SHORT).show()
                    }

                    override fun toString(): String {
                        return super.toString()
                    }
                }
            )
        }

        fun scanAudioFilesWithMediaStore(
            activity: Activity,
            genericDataListFetcherViewModel : GenericDataListFetcherViewModel,
            minToShow: Int
        ) {
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
                val idColumn: Int = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) ?: -1
                val nameColumn: Int =
                    cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME) ?: -1
                val durationColumn: Int =
                    cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) ?: -1
                val mimeTypeColumn: Int =
                    cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE) ?: -1

                var itemsCount = 0
                while (cursor?.moveToNext() == true) {
                    itemsCount++
                    // Get values of columns for a given audio.
                    val absolutePath: String = cursor.getString(absolutePathColumn) ?: ""
                    val id: Int = cursor.getInt(idColumn) ?: -1
                    val name: String = cursor.getString(nameColumn) ?: ""
                    val duration: Long = cursor.getLong(durationColumn) ?: -1
                    val mimeType: String = cursor.getString(mimeTypeColumn) ?: ""

                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    val songItem : SongItem = AudioFileInfoExtractor.getAudioInfo(absolutePath)
                    songItem.absolutePath = absolutePath
                    songItem.fileName = name
                    songItem.duration = duration
                    songItem.typeMime = mimeType
                    //Now save on database
                    tempSongList.add(songItem)
                    if(itemsCount > 0 && itemsCount == minToShow){
                        itemsCount = 0
                        genericDataListFetcherViewModel.setDataList(tempSongList as ArrayList<Any>)
                        genericDataListFetcherViewModel.setIsLoading(false)
                    }
                }
                genericDataListFetcherViewModel.setDataList(tempSongList as ArrayList<Any>)
                genericDataListFetcherViewModel.setIsLoading(false)
                genericDataListFetcherViewModel.setIsLoadingInBackground(false)
                genericDataListFetcherViewModel.setDataRequestCounter(1)
            }
        }
    }
}