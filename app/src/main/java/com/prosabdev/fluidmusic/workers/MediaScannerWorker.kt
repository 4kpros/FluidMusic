package com.prosabdev.fluidmusic.workers

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.*

class MediaScannerWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    private val mScanCounter : Array<Int> = Array(3) { 0 }
    override suspend fun doWork(): Result {
        val updateMethod = inputData.getString(ConstantValues.MEDIA_SCANNER_WORKER_SCAN_METHOD)
        return withContext(Dispatchers.IO) {
            try {
                mScanCounter[0] = 0
                mScanCounter[1] = 0
                mScanCounter[2] = 0
                scanDeviceFolderUriTrees(applicationContext, updateMethod)

                Result.success(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT to mScanCounter))
            } catch (error: Throwable) {
                Result.failure()
            }
        }
    }
    private suspend fun scanDeviceFolderUriTrees(
        context: Context,
        updateMethod : String?
    ) = coroutineScope{
        val tempFolderSelected: ArrayList<FolderUriTree> =
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao()
                .getAllFolderUriTreesDirectly() as ArrayList<FolderUriTree>

        if (tempFolderSelected.isEmpty()) {
            MainScope().launch {
                Toast.makeText(context, "Please select folders to scan !", Toast.LENGTH_LONG)
                    .show()
            }
            return@coroutineScope
        }
        if (updateMethod != null && updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL) {
            Log.i(
                ConstantValues.TAG,
                "MEDIA SCANNER WORKER ---> Cleaning up old songs from database..."
            )
            AppDatabase.getDatabase(applicationContext).songItemDao().deleteAllFromSongs()
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao()
                .resetAllFolderUriTreesLastModified()
        }
        for (i in 0 until tempFolderSelected.size) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(context, Uri.parse(tempFolderSelected[i].uriTree))
            launch {
                scanDocumentUriContent(
                    context,
                    tempDocFile?.uri,
                    tempFolderSelected[i].id
                )
            }
        }
    }
    private suspend fun scanDocumentUriContent(
        context: Context,
        uriTree: Uri?,
        folderUriTreeId : Long?
    ) :Unit = coroutineScope {
        if(uriTree != null) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(context, uriTree)
            if (tempDocFile != null) {
                val foreachEnd = tempDocFile.listFiles().size
                for (i in 0 until foreachEnd) {
                    if (tempDocFile.listFiles()[i].isDirectory) {
                        launch {
                            val tempRecursiveUri = tempDocFile.listFiles()[i].uri
                            mScanCounter[0]++
                            scanDocumentUriContent(context, tempRecursiveUri, folderUriTreeId)
                        }
                    } else {
                        launch {
                            val tempFFF = tempDocFile.listFiles()[i]
                            val tempMimeType: String = tempFFF.type ?: ""
                            if (tempMimeType.contains("audio/") && !tempMimeType.contains("x-mpegurl")) {
                                context.contentResolver.openAssetFileDescriptor(
                                    tempFFF.uri,
                                    "r"
                                ).use {
                                    val tempSong : SongItem = SongItem()
                                    try {
                                        val mdr = MediaMetadataRetriever()
                                        mdr.setDataSource(it?.fileDescriptor)
                                        tempSong.uri = tempFFF.uri.toString()
                                        tempSong.uriTreeId = folderUriTreeId
                                        tempSong.fileName = tempFFF.name
                                        tempSong.title = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                        tempSong.artist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                                        tempSong.composer = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)
                                        tempSong.album = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                                        tempSong.albumArtist = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                                        tempSong.genre = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                                        tempSong.relativePath = "${tempFFF.parentFile?.name}/${tempFFF.name}"
                                        tempSong.folder = tempFFF.parentFile?.name
                                        tempSong.folderUri = tempFFF.parentFile?.uri.toString()
                                        tempSong.year = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                                        tempSong.duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
                                        tempSong.typeMime = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                                        tempSong.bitrate = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toDouble() ?: 0.0
                                        tempSong.lastUpdateDate = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                                        tempSong.lastAddedDateToLibrary = ""

                                        val extractor : MediaExtractor = MediaExtractor()
                                        extractor.setDataSource(it?.fileDescriptor!!)
                                        val numTracks : Int = extractor.trackCount
                                        for (i in 0 until numTracks) {
                                            val format : MediaFormat = extractor.getTrackFormat(i)
                                            tempSong.sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                                            tempSong.language = format.getString(MediaFormat.KEY_LANGUAGE)
                                        }
                                        extractor.release()
                                    } catch (error: Throwable) {
                                        Log.i(
                                            ConstantValues.TAG,
                                            "MEDIA SCANNER WORKER ---> error when retrieving file : ${error}"
                                        )
                                    }finally {
                                        Log.i(
                                            ConstantValues.TAG,
                                            "MEDIA SCANNER WORKER ---> : ${tempSong.fileName}"
                                        )
                                        AppDatabase.getDatabase(applicationContext).songItemDao().Insert(tempSong)
                                        mScanCounter[1]++
                                    }
                                }
                            } else if (tempMimeType.contains("x-mpegurl")) {
                                //
                            }
                        }
                    }
                }
            }

        }
    }
}