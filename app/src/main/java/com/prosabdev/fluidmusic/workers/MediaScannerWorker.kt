package com.prosabdev.fluidmusic.workers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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

    override suspend fun doWork(): Result {
        val updateMethod = inputData.getString(ConstantValues.MEDIA_SCANNER_WORKER_SCAN_METHOD)
        return withContext(Dispatchers.IO) {
            try {
                scanDeviceFolderUriTrees(applicationContext, updateMethod)
                Log.i(ConstantValues.TAG, "Scan finished !")
                Result.success()
            } catch (error: Throwable) {
                Result.failure()
            }
        }
    }

    private fun removeFolderUriTreeAndAllDataFromDatabase(context: Context, position : Int, folderUriTreeViewModel: FolderUriTreeViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            folderUriTreeViewModel.deleteItemAtPosition(position)
        }
    }

    private suspend fun scanDeviceFolderUriTrees(
        context: Context,
        updateMethod : String?
    ) {
        val tempFolderSelected: ArrayList<FolderUriTree> =
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAllFolderUriTreesDirectly() as ArrayList<FolderUriTree>

        if (tempFolderSelected.isEmpty()){
            MainScope().launch {
                Toast.makeText(context, "Please select folders to scan !", Toast.LENGTH_LONG).show()
            }
            return
        }
        if(updateMethod != null && updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL) {
            Log.i(ConstantValues.TAG, "Cleaning up old database ...")
            AppDatabase.getDatabase(applicationContext).songItemDao().deleteAllFromSongs()
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao().resetAllFolderUriTreesLastModified()
        }
        Log.i(ConstantValues.TAG, "Starting scanning")
        coroutineScope {
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
    private suspend fun scanDocumentUriContent(
        context: Context,
        uriTree: Uri?
    ): Unit = coroutineScope {
        if(uriTree == null)
            return@coroutineScope
        launch {
            val tempDocFile: DocumentFile? =
                DocumentFile.fromTreeUri(context, uriTree)
            if(tempDocFile != null){
                val foreachEnd = tempDocFile.listFiles().size
                for (i in 0 until foreachEnd) {
                    if (tempDocFile.listFiles()[i].isDirectory) {
                        launch{
                            val tempRecursiveUri = tempDocFile.listFiles()[i].uri
                            scanDocumentUriContent(context, tempRecursiveUri)
                        }
                    }else{
                        launch{
                            val tempFFF = tempDocFile.listFiles()[i]
                            val tempMimeType: String? = tempFFF.type
                            if(tempMimeType?.contains("audio/") == true && !tempMimeType.contains("x-mpegurl")) {
                                context.contentResolver.openAssetFileDescriptor(tempFFF.uri, "r").use {
                                    val mediaMetadataRetriever = MediaMetadataRetriever()
                                    try {
                                        mediaMetadataRetriever.setDataSource(it?.fileDescriptor)
                                        Log.i(
                                            ConstantValues.TAG,
                                            "Meta data title : ${
                                                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                            }"
                                        )
                                    }finally {
                                        //
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}