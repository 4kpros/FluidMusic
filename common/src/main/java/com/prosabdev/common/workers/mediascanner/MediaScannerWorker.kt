package com.prosabdev.common.workers.mediascanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.models.FolderUriTree
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.utils.AudioInfoExtractor
import com.prosabdev.common.utils.SystemSettings
import com.prosabdev.common.workers.playlist.AddSongsToPlaylistWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


class MediaScannerWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(AddSongsToPlaylistWorker.TAG, "Worker $TAG started")

                val scannedFolders: ConcurrentHashMap<String, String> = ConcurrentHashMap()
                val scannedSongs: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
                val scannedPlaylists: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()

                //Start to scan folders
                scanSongsAndPlaylists(scannedFolders, scannedSongs, scannedPlaylists)

                Log.i(AddSongsToPlaylistWorker.TAG, "Worker $TAG ended")

                Result.success(
                    workDataOf(
                        OUTPUT_FOLDER_COUNT to scannedFolders.size,
                        OUTPUT_SONG_COUNT to scannedSongs.size,
                        OUTPUT_PLAYLIST_COUNT to scannedPlaylists.size,
                    )
                )
            } catch (error: Throwable) {
                Log.i(TAG, "Error stack trace -> ${error.stackTrace}")
                Log.i(TAG, "Error message -> ${error.message}")
                Result.failure()
            }
        }
    }

    /**
     * Scan songs and playlists from device
     */
    private suspend fun scanSongsAndPlaylists(
        scannedFolders: ConcurrentHashMap<String, String>,
        scannedSongs: ConcurrentLinkedQueue<String>,
        scannedPlaylists: ConcurrentLinkedQueue<String>
    ) {
        //Get all folder uri trees
        val tempFolderSelected: List<FolderUriTree>? = AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAllDirectly()
        if (tempFolderSelected.isNullOrEmpty()) return
        //Fetch song and playlists

        for (i in tempFolderSelected.indices) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(applicationContext, Uri.parse(tempFolderSelected[i].uriTree))
            scanDocumentUriContent(
                scannedFolders,
                scannedSongs,
                scannedPlaylists,
                tempDocFile?.uri,
                tempFolderSelected[i].id,
                null,
            )
        }
    }

    /**
     * Method used to scan content of document [Uri]
     */
    private suspend fun scanDocumentUriContent(
        scannedFolders: ConcurrentHashMap<String, String>,
        scannedSongs: ConcurrentLinkedQueue<String>,
        scannedPlaylists: ConcurrentLinkedQueue<String>,
        uriTree: Uri?,
        folderUriTreeId : Long?,
        parentFolder : String? = null
    ) : Unit = supervisorScope{
        if(uriTree == null || uriTree == Uri.EMPTY) return@supervisorScope

        val tempDocFile: DocumentFile? =
            DocumentFile.fromTreeUri(applicationContext, uriTree)
        if (tempDocFile != null) {
            val foreachEnd = tempDocFile.listFiles().size
            for (i in 0 until foreachEnd) {
                if (tempDocFile.listFiles()[i].isDirectory) {
                    launch {
                        val tempNextUri = tempDocFile.listFiles()[i].uri
                        scanDocumentUriContent(
                            scannedFolders,
                            scannedSongs,
                            scannedPlaylists,
                            tempNextUri,
                            folderUriTreeId,
                            tempDocFile.name
                        )
                    }
                } else {
                    launch {
                        val tempFFF = tempDocFile.listFiles()[i]
                        val tempMimeType: String = tempFFF.type ?: ""
                        if (tempMimeType.startsWith("audio/")) {
                            if(!tempMimeType.contains("x-mpegurl")){
                                applicationContext.contentResolver.openAssetFileDescriptor(
                                    tempFFF.uri,
                                    "r"
                                ).use { afd ->
                                    if(afd != null){
                                        scannedFolders[tempDocFile.uri.toString()] = tempDocFile.name ?: ""
                                        val tempSong = AudioInfoExtractor.extractAudioInfoFromUri(
                                            applicationContext,
                                            tempFFF,
                                            afd.fileDescriptor
                                        )
                                        if(tempSong != null){
                                            scannedSongs.add(tempFFF.name)
                                            tempSong.folder = tempDocFile.name ?: ""
                                            tempSong.folderUri = uriTree.toString()
                                            tempSong.folderParent = parentFolder ?: ""
                                            tempSong.uriTreeId = folderUriTreeId ?: -1
                                            saveSongToDatabase(tempSong)
                                        }
                                    }
                                }
                            }else{
                                scannedPlaylists.add(tempFFF.name)
                                val playlistItem = PlaylistItem()
                                playlistItem.isRealFile = true
                                playlistItem.lastAddedDateToLibrary = SystemSettings.getCurrentDateInMillis()
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

    /**
     * Save song to database and return an [Int] 1 if the song is saved or 0 else
     */
    private fun saveSongToDatabase(songItem: SongItem): Long {
        if(songItem.uri?.isEmpty() ?: return 0) return 0
        var updateCount = -1
        try {
            updateCount = AppDatabase.getDatabase(applicationContext).songItemDao().updateAtUri(
                songItem.uri!!,
                songItem.uriTreeId,
                songItem.fileName!!,
                songItem.title!!,
                songItem.artist!!,
                songItem.albumArtist!!,
                songItem.composer!!,
                songItem.album!!,
                songItem.genre!!,
                songItem.uriPath!!,
                songItem.folder!!,
                songItem.folderParent!!,
                songItem.folderUri!!,
                songItem.year!!,
                songItem.duration,
                songItem.language!!,
                songItem.typeMime!!,
                songItem.sampleRate,
                songItem.bitrate,
                songItem.size,
                songItem.channelCount,
                songItem.fileExtension!!,
                songItem.bitPerSample!!,
                songItem.lastUpdateDate,
                songItem.author!!,
                songItem.diskNumber!!,
                songItem.writer!!,
                songItem.cdTrackNumber!!,
                songItem.numberTracks!!,
                songItem.comments!!,
                songItem.rating,
                songItem.hashedCovertArtSignature,
                songItem.isValid
            )
        }catch (error: Throwable){
            error.printStackTrace()
        }
        return if(updateCount <= 0) AppDatabase.getDatabase(applicationContext).songItemDao().insert(songItem)
        else updateCount.toLong()
    }

    /**
     * Save playlist to database
     */
    private fun savePlaylistToDatabase(playlistItem: PlaylistItem) {
        var updateResult = -1
        try {
            updateResult = AppDatabase.getDatabase(applicationContext).playlistItemDao().updateAtUri(
                playlistItem.uri!!,
                playlistItem.name!!,
                playlistItem.isRealFile
            )
        }catch (error: Throwable) {
            error.printStackTrace()
        }
        Log.i(TAG, "UPDATE PLAYLIST WITH URI ${playlistItem.uri} RESULT $updateResult")
        if(updateResult <= 0){
            AppDatabase.getDatabase(applicationContext).playlistItemDao().insert(playlistItem)
        }
    }

    companion object {
        const val TAG = "MediaScannerWorker"

        const val OUTPUT_FOLDER_COUNT = "FOLDER_COUNT"
        const val OUTPUT_SONG_COUNT = "SONG_COUNT"
        const val OUTPUT_PLAYLIST_COUNT = "PLAYLIST_COUNT"
    }
}