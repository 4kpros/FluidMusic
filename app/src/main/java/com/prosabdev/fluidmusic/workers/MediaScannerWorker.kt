package com.prosabdev.fluidmusic.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.AudioInfoExtractorUtils
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.*


class MediaScannerWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    private val mScanCounter : Array<Int> = Array(3) { 0 }
    private val mSongList : ArrayList<SongItem> = ArrayList()

    override suspend fun doWork(): Result {
        val updateMethod = inputData.getString(ConstantValues.MEDIA_SCANNER_WORKER_SCAN_METHOD)
        return withContext(Dispatchers.IO) {
            try {
                mScanCounter[0] = 0
                mScanCounter[1] = 0
                mScanCounter[2] = 0
                scanDeviceFolderUriTrees(applicationContext, updateMethod)
                if(mSongList.size > 0){
                    launch {
                        for(i in 0 until (mSongList.size)){
                            mSongList[i].id = i.toLong()
                            AppDatabase.getDatabase(applicationContext).songItemDao().insert(mSongList[i])
                        }
                    }
                }
                Log.i(ConstantValues.TAG, "Load finished ${mSongList.size}")
                Result.success(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT to mScanCounter))
            } catch (error: Throwable) {
                Log.i(ConstantValues.TAG, "Error loading... ${error.stackTrace}")
                Log.i(ConstantValues.TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }
    private suspend fun scanDeviceFolderUriTrees(
        context: Context,
        updateMethod : String?
    ) = withContext(Dispatchers.IO){
        var tempFolderSelected: List<FolderUriTree>? = null
        tempFolderSelected = AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAll("id").value
        if (tempFolderSelected == null || tempFolderSelected.isEmpty()) {
            MainScope().launch {
                Toast.makeText(context, "Please select folders to scan !", Toast.LENGTH_LONG)
                    .show()
            }
            return@withContext
        }
        if (updateMethod != null && updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL) {
            Log.i(
                ConstantValues.TAG,
                "MEDIA SCANNER WORKER ---> Cleaning up old songs from database..."
            )
            AppDatabase.getDatabase(applicationContext).songItemDao().deleteAll()
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao()
                .resetAllFolderUriTreesLastModified()
        }
        for (i in tempFolderSelected.indices) {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(context, Uri.parse(tempFolderSelected[i].uriTree))
            scanDocumentUriContent(
                context,
                tempDocFile?.uri,
                tempFolderSelected[i].id
            )
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
                                    var tempSong : SongItem? = null
                                    withContext(Dispatchers.IO){
                                        tempSong = AudioInfoExtractorUtils.extractAudioInfoFromUri(applicationContext, tempFFF.uri)
                                    }
                                    withContext(Dispatchers.Default){
                                        if(tempSong != null){
                                            tempSong?.uriTreeId = folderUriTreeId ?: -1
                                            mSongList.add(tempSong!!)
                                            mScanCounter[1] = mSongList.size
                                        }
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