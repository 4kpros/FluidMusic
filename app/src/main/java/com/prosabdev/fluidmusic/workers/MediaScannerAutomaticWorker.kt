package com.prosabdev.fluidmusic.workers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaScannerAutomaticWorker(
    ctx : Context,
    params : WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {

                scanDeviceFolderUriTrees(applicationContext)
                Log.i(ConstantValues.TAG, "Scan finished !")

                Result.success()
            }catch (error : Throwable){
                Result.failure()
            }
        }
    }

    private suspend fun scanDeviceFolderUriTrees(
        context: Context
    ) {
        val tempFolderSelected: ArrayList<FolderUriTree>? =
            AppDatabase.getDatabase(applicationContext).folderUriTreeDao().getAll("id")?.value as ArrayList<FolderUriTree>?

        if (tempFolderSelected == null || tempFolderSelected.isEmpty()){
            Log.i(ConstantValues.TAG, "No folder uri tree to scan !")
            Log.i(ConstantValues.TAG, "Break and stay calm !")
            return
        }
        Log.i(ConstantValues.TAG, "Starting auto scanning in background")
        coroutineScope {
            for (i in 0 until tempFolderSelected.size){
                val tempDocFile: DocumentFile? =
                    DocumentFile.fromTreeUri(context, Uri.parse(tempFolderSelected[i].uriTree ?: ""))

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
                                                mediaMetadataRetriever.extractMetadata(
                                                    MediaMetadataRetriever.METADATA_KEY_TITLE)
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