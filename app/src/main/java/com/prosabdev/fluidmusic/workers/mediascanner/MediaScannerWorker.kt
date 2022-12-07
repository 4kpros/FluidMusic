package com.prosabdev.fluidmusic.workers.mediascanner

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.AudioInfoExtractorUtils
import com.prosabdev.fluidmusic.utils.SystemSettingsUtils
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import com.prosabdev.fluidmusic.workers.playlist.PlaylistAddSongsWorker
import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue


class MediaScannerWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        var isUpdating: Boolean = false
        val scannedFolders: LinkedBlockingQueue<String> = LinkedBlockingQueue()
        val dataResultCount: ConcurrentHashMap<String, Int> = ConcurrentHashMap(3)
        dataResultCount[OUTPUT_FOLDER_COUNT] = 0
        dataResultCount[OUTPUT_SONG_COUNT] = 0
        dataResultCount[OUTPUT_PLAYLIST_COUNT] = 0
        return withContext(Dispatchers.IO) {
            try {
                Log.i(PlaylistAddSongsWorker.TAG, "WORKER $TAG : started")

                //Start to scan folders
                scanSongsAndPlaylists(scannedFolders, dataResultCount)

                isUpdating = false
                Log.i(PlaylistAddSongsWorker.TAG, "WORKER $TAG : ended")

                Result.success(
                    workDataOf(
                        WorkerConstantValues.WORKER_OUTPUT_IS_UPDATING to isUpdating,
                        WorkerConstantValues.WORKER_OUTPUT_DATA to dataResultCount
                    )
                )
            } catch (error: Throwable) {
                Log.i(PlaylistAddSongsWorker.TAG, "Error loading... ${error.stackTrace}")
                Log.i(PlaylistAddSongsWorker.TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }
    private suspend fun scanSongsAndPlaylists(
        scannedFolders: LinkedBlockingQueue<String>,
        dataResultCount: ConcurrentHashMap<String, Int>
    ) = withContext(Dispatchers.IO){
        //Get all folder uri trees
        val tempFolderSelected: List<FolderUriTree>? = AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAllDirectly()
        if (tempFolderSelected == null || tempFolderSelected.isEmpty()) return@withContext
        //Fetch song and playlists
        for (i in tempFolderSelected.indices) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(applicationContext, Uri.parse(tempFolderSelected[i].uriTree ?: ""))
            scanDocumentUriContent(
                scannedFolders,
                dataResultCount,
                tempDocFile?.uri,
                tempFolderSelected[i].id,
                null,
            )
        }
    }
    private suspend fun scanDocumentUriContent(
        scannedFolders: LinkedBlockingQueue<String>,
        dataResultCount: ConcurrentHashMap<String, Int>,
        uriTree: Uri?,
        folderUriTreeId : Long?,
        parentFolder : String? = null
    ) : Unit = supervisorScope {
        if(uriTree != null) {
            launch mainScope@ {
                val tempDocFile: DocumentFile? =
                    DocumentFile.fromTreeUri(applicationContext, uriTree)
                if (tempDocFile != null) {
                    val foreachEnd = tempDocFile.listFiles().size
                    for (i in 0 until foreachEnd) {
                        if (tempDocFile.listFiles()[i].isDirectory) {
                            launch folderScanScope@ {
                                val tempRecursiveUri = tempDocFile.listFiles()[i].uri
                                scanDocumentUriContent(
                                    scannedFolders,
                                    dataResultCount,
                                    tempRecursiveUri,
                                    folderUriTreeId,
                                    tempDocFile.name
                                )
                            }
                        } else {
                            launch fileInfoExtractorScope@ {
                                val tempFFF = tempDocFile.listFiles()[i]
                                val tempMimeType: String = tempFFF.type ?: ""
                                if (tempMimeType.contains("audio/") && !tempMimeType.contains("x-mpegurl")) {
                                    applicationContext.contentResolver.openAssetFileDescriptor(
                                        tempFFF.uri,
                                        "r"
                                    ).use {
                                        if(!scannedFolders.contains(tempDocFile.name)){
                                            scannedFolders.put(tempDocFile.name)
                                            dataResultCount[OUTPUT_FOLDER_COUNT] = scannedFolders.size
                                        }
                                        val tempSong : SongItem? = AudioInfoExtractorUtils.extractAudioInfoFromUri(applicationContext, tempFFF)
                                        tempSong?.let {
                                            it.folder = tempDocFile.name
                                            it.folderUri = uriTree.toString()
                                            it.folderParent = parentFolder
                                            it.uriTreeId = folderUriTreeId ?: -1
                                            dataResultCount[OUTPUT_SONG_COUNT] = (dataResultCount[OUTPUT_SONG_COUNT] ?: 0) + 1
                                            saveSongToDatabase(it)
                                        }
                                    }
                                } else if (tempMimeType.contains("x-mpegurl")) {
                                    dataResultCount[OUTPUT_PLAYLIST_COUNT] = (dataResultCount[OUTPUT_PLAYLIST_COUNT] ?: 0) + 1
                                    val playlistItem = PlaylistItem()
                                    playlistItem.isRealFile = true
                                    playlistItem.lastAddedDateToLibrary = SystemSettingsUtils.getCurrentDateInMilli()
                                    playlistItem.lastUpdateDate = tempFFF.lastModified()
                                    playlistItem.uri = tempFFF.uri.toString()
                                    savePlaylistToDatabase(playlistItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun saveSongToDatabase(songItem: SongItem) {
        val updateResult = AppDatabase.getDatabase(applicationContext).songItemDao().updateAtUri(
            songItem.uri ?: return,
            songItem.uriTreeId,
            songItem.fileName,
            songItem.title,
            songItem.artist,
            songItem.albumArtist,
            songItem.composer,
            songItem.album,
            songItem.genre,
            songItem.uriPath,
            songItem.folder,
            songItem.folderParent,
            songItem.folderUri,
            songItem.year,
            songItem.duration,
            songItem.language,
            songItem.typeMime,
            songItem.sampleRate,
            songItem.bitrate,
            songItem.size,
            songItem.channelCount,
            songItem.fileExtension,
            songItem.bitPerSample,
            songItem.lastUpdateDate,
            songItem.lastAddedDateToLibrary,
            songItem.author,
            songItem.diskNumber,
            songItem.writer,
            songItem.cdTrackNumber,
            songItem.numberTracks,
            songItem.comments,
            songItem.rating,
            songItem.playCount,
            songItem.lastPlayed,
            songItem.hashedCovertArtSignature,
            songItem.isValid
        )
        Log.i(TAG, "UPDATE AT URI RESULT $updateResult")
        if(updateResult <= 0){
            AppDatabase.getDatabase(applicationContext).songItemDao().insert(songItem)
        }
    }
    private fun savePlaylistToDatabase(playlistItem: PlaylistItem) {
//        val updateResult = AppDatabase.getDatabase(applicationContext).playlistItemDao().updateAtUri(
//
//        )
//        Log.i(TAG, "UPDATE PLAYLIST WITH URI ${playlistItem.uri} RESULT $updateResult")
//        if(updateResult <= 0){
//            AppDatabase.getDatabase(applicationContext).playlistItemDao().insert(playlistItem)
//        }
    }

    companion object {
        const val TAG = "MediaScannerWorker"

        const val OUTPUT_FOLDER_COUNT = "FOLDER_COUNT"
        const val OUTPUT_SONG_COUNT = "SONG_COUNT"
        const val OUTPUT_PLAYLIST_COUNT = "PLAYLIST_COUNT"
    }
}