package com.prosabdev.fluidmusic.workers.mediascanner

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.prosabdev.fluidmusic.workers.playlist.AddSongsToPlaylistWorker
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
        val scannedFolders: ConcurrentHashMap<String, String> = ConcurrentHashMap()
        val scannedSongs: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
        val scannedPlaylists: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
        return withContext(Dispatchers.IO) {
            try {
                Log.i(AddSongsToPlaylistWorker.TAG, "WORKER $TAG : started")

                //Start to scan folders
                scanSongsAndPlaylists(scannedFolders, scannedSongs, scannedPlaylists)

                Log.i(AddSongsToPlaylistWorker.TAG, "WORKER $TAG : ended")

                Result.success(
                    workDataOf(
                        OUTPUT_FOLDER_COUNT to scannedFolders.size,
                        OUTPUT_SONG_COUNT to scannedSongs.size,
                        OUTPUT_PLAYLIST_COUNT to scannedPlaylists.size,
                    )
                )
            } catch (error: Throwable) {
                Log.i(AddSongsToPlaylistWorker.TAG, "Error loading... ${error.stackTrace}")
                Log.i(AddSongsToPlaylistWorker.TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }
    private suspend fun scanSongsAndPlaylists(
        scannedFolders: ConcurrentHashMap<String, String>,
        scannedSongs: ConcurrentLinkedQueue<String>,
        scannedPlaylists: ConcurrentLinkedQueue<String>
    ) {
        //Get all folder uri trees
        val tempFolderSelected: List<FolderUriTree>? = AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAllDirectly()
        if (tempFolderSelected == null || tempFolderSelected.isEmpty()) return
        //Fetch song and playlists

        for (i in tempFolderSelected.indices) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(applicationContext, Uri.parse(tempFolderSelected[i].uriTree ?: ""))
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
                                        val tempSong = AudioInfoExtractorUtils.extractAudioInfoFromUri(
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
                                            Log.i(TAG, "SONG FOUND : ${tempSong.fileName}")
                                            saveSongToDatabase(tempSong)
                                        }
                                    }
                                }
                            }else{
                                scannedPlaylists.add(tempFFF.name)
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
    private fun saveSongToDatabase(songItem: SongItem) {
        if(songItem.uri?.isEmpty() ?: return) return
        val updateResult = AppDatabase.getDatabase(applicationContext).songItemDao().updateAtUri(
            songItem.uri,
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
            songItem.author,
            songItem.diskNumber,
            songItem.writer,
            songItem.cdTrackNumber,
            songItem.numberTracks,
            songItem.comments,
            songItem.rating,
            songItem.hashedCovertArtSignature,
            songItem.isValid
        )
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