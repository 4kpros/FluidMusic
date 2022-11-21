package com.prosabdev.fluidmusic.workers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModel
import kotlinx.coroutines.*

class MediaScannerWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    private var mFoldersCount : Int = 0
    private var mSongsCount : Int = 0
    private var mPlaylistsCount : Int = 0

    override suspend fun doWork(): Result {
        val updateMethod = inputData.getString(ConstantValues.MEDIA_SCANNER_WORKER_SCAN_METHOD)
        return withContext(Dispatchers.IO) {
            try {
                mFoldersCount = 0
                mSongsCount = 0
                mPlaylistsCount = 0
                val result : Array<Int> = Array(3) { 0 }

                Log.i(ConstantValues.TAG, "MEDIA SCANNER WORKER ---> On start to scan, checking requirements...")
                scanDeviceFolderUriTrees(applicationContext, updateMethod)

                result[0] = mFoldersCount
                result[1] = mSongsCount
                result[2] = mPlaylistsCount

                Result.success(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT to result))
            } catch (error: Throwable) {
                Result.failure()
            }
        }
    }
    var mSupervisorJob : Job ? = null
    private suspend fun scanDeviceFolderUriTrees(
        context: Context,
        updateMethod : String?
    ) {
        val tempFolderSelected: ArrayList<FolderUriTree> =
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAllFolderUriTreesDirectly() as ArrayList<FolderUriTree>

        if (tempFolderSelected.isEmpty()){
            MainScope().launch {
                Log.i(ConstantValues.TAG, "MEDIA SCANNER WORKER ---> Please select folders to scan !")
                Toast.makeText(context, "Please select folders to scan !", Toast.LENGTH_LONG).show()
            }
            return
        }
        if(updateMethod != null && updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL) {
            Log.i(ConstantValues.TAG, "MEDIA SCANNER WORKER ---> Cleaning up old songs from database...")
            AppDatabase.getDatabase(applicationContext).songItemDao().deleteAllFromSongs()
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao().resetAllFolderUriTreesLastModified()
        }
        Log.i(ConstantValues.TAG, "MEDIA SCANNER WORKER ---> Starting scan !")
        withContext(Dispatchers.IO) {
            supervisorScope {
                for (i in 0 until tempFolderSelected.size){
                    val tempDocFile: DocumentFile? =
                        DocumentFile.fromTreeUri(context, Uri.parse(tempFolderSelected[i].uriTree))

                    scanDocumentUriContent(
                        context,
                        tempDocFile?.uri,
                    )
                }
            }
        }
    }
    private suspend fun scanDocumentUriContent(
        context: Context,
        uriTree: Uri?
    ) {
        if(uriTree == null)
            return
        supervisorScope {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(context, uriTree)
            if(tempDocFile != null){
                val foreachEnd = tempDocFile.listFiles().size
                for (i in 0 until foreachEnd) {
                    if (tempDocFile.listFiles()[i].isDirectory) {
                        launch {
                            mFoldersCount++
                            val tempRecursiveUri = tempDocFile.listFiles()[i].uri
                            scanDocumentUriContent(context, tempRecursiveUri)
                        }
                    }else{
                        launch {
                            val tempFFF = tempDocFile.listFiles()[i]
                            val tempMimeType: String = tempFFF.type ?: ""
                            if(tempMimeType.contains("audio/") && !tempMimeType.contains("x-mpegurl")) {
                                context.contentResolver.openAssetFileDescriptor(tempFFF.uri, "r").use {
                                    val mediaMetadataRetriever = MediaMetadataRetriever()
                                    try {
                                        mediaMetadataRetriever.setDataSource(it?.fileDescriptor)
                                        mSongsCount++
                                        Log.i(
                                            ConstantValues.TAG,
                                            "MEDIA SCANNER WORKER ---> Meta data title : ${
                                                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                            }"
                                        )
                                    }catch (error : Throwable){
                                        Log.i(
                                            ConstantValues.TAG,
                                            "MEDIA SCANNER WORKER ---> error when retrieving file : ${tempFFF.name}"
                                        )
                                        return@use
                                    }
                                }
                            }else if(tempMimeType.contains("x-mpegurl")){
                                mPlaylistsCount++
                            }
                        }
                    }
                }
            }
        }
    }
}