package com.prosabdev.fluidmusic.services.worker

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModel
import kotlinx.coroutines.*

class MediaScannerWorker(
    private val mContext: Context,
    private val mFolderUriTreeViewModel: FolderUriTreeViewModel,
    private val mSongItemViewModel: SongItemViewModel? = null,
    workerParams: WorkerParameters
) : CoroutineWorker(mContext, workerParams) {

    override suspend fun doWork(): Result {
        val updateMethod = inputData.getString(ConstantValues.MEDIA_SCANNER_WORKER_METHOD)
        return try {
            if(updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE){
                val tempPosition = inputData.getInt(ConstantValues.MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE_POSITION, -1)
                removeFolderUriTreeAndAllDataFromDatabase(mContext, tempPosition, mFolderUriTreeViewModel)
            }else{
                scanDeviceFolderUriTrees(mContext, mFolderUriTreeViewModel, mSongItemViewModel!!, updateMethod)
            }

            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    private fun removeFolderUriTreeAndAllDataFromDatabase(context: Context, position : Int, folderUriTreeViewModel: FolderUriTreeViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            folderUriTreeViewModel.deleteItemAtPosition(position)
        }
    }

    private suspend fun scanDeviceFolderUriTrees(
        context: Context,
        folderUriTreeViewModel: FolderUriTreeViewModel,
        songItemViewModel: SongItemViewModel,
        updateMethod : String?,
    ) = coroutineScope {
        val tempFolderSelected: ArrayList<FolderUriTree> =
            folderUriTreeViewModel.getAllFolderUriTreesDirectly() as ArrayList<FolderUriTree>

        if (tempFolderSelected.isEmpty()){
            MainScope().launch {
                Toast.makeText(context, "Please select folders to scan", Toast.LENGTH_LONG).show()
            }
            return@coroutineScope
        }
        if(updateMethod != null && updateMethod == ConstantValues.MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL) {
            songItemViewModel.deleteAllFromSongs()
            folderUriTreeViewModel.resetAllFolderUriTreesLastModified()
        }
        launch {
            for (i in 0 until tempFolderSelected.size){
                val tempDocFile: DocumentFile? =
                    DocumentFile.fromTreeUri(context, Uri.parse(tempFolderSelected[i].uriTree))

                scanDocumentUriContent(
                    context,
                    tempDocFile?.uri,
                    songItemViewModel,
                )
            }
        }
    }
    private suspend fun scanDocumentUriContent(
        context: Context,
        uriTree: Uri?,
        songItemViewModel: SongItemViewModel
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
                            scanDocumentUriContent(context, tempRecursiveUri, songItemViewModel)
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