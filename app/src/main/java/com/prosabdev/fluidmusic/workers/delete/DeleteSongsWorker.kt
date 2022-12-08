package com.prosabdev.fluidmusic.workers.delete

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.songitem.SongItemUri
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteSongsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val modelType = inputData.getString(WorkerConstantValues.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WorkerConstantValues.ITEM_LIST)
                val orderBy = inputData.getString(WorkerConstantValues.ITEM_LIST_ORDER_BY)
                val whereClause = inputData.getString(WorkerConstantValues.ITEM_LIST_WHERE)
                val indexColum = inputData.getString(WorkerConstantValues.INDEX_COLUM)
                //Delete songs from database and on device
                val dataResult = tryToDeleteSongsFromSource(
                    modelType,
                    itemsList,
                    indexColum,
                    orderBy,
                    whereClause
                )
                Log.i(TAG, "WORKER $TAG : DELETED SONGS ON DATABASE : ${dataResult[0]} ")
                Log.i(TAG, "WORKER $TAG : DELETED SONGS ON DEVICE : ${dataResult[1]}")

                Log.i(TAG, "WORKER $TAG : ended")

                Result.success(
                    workDataOf(
                        WorkerConstantValues.WORKER_OUTPUT_DATA to dataResult,
                    )
                )
            } catch (error: Throwable) {
                Log.i(TAG, "Error loading... ${error.stackTrace}")
                Log.i(TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }

    private fun deleteSongsFromDevice(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0
        var deleteResult = 0
        for (i in stringUriList.indices){
            deleteResult += deleteSongItemAtUriOnDevice(stringUriList[i])
        }
        return deleteResult
    }
    private fun deleteSongsFromDevice(stringUriList: List<SongItemUri>?): Int {
        if(stringUriList == null) return 0
        var deleteResult = 0
        for (i in stringUriList.indices){
            deleteResult += deleteSongItemAtUriOnDevice(stringUriList[i].uri)
        }
        return deleteResult
    }
    private fun deleteSongItemAtUriOnDevice(uri: String?): Int {
        if(uri == null || uri.isEmpty()) return 0
        val uriTree = Uri.parse(uri)
        val tempDocFile: DocumentFile? =
            DocumentFile.fromTreeUri(applicationContext, uriTree)
        val deleteResult = tempDocFile?.delete() ?: false
        return if(deleteResult) 1 else 0
    }

    private fun deleteSongsFromDatabase(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0
        var deleteResult = 0
        for (i in stringUriList.indices){
            deleteResult += deleteSongItemAtUriOnDatabase(stringUriList[i])
        }
        return deleteResult
    }
    private fun deleteSongsFromDatabase(songUriList: List<SongItemUri>?): Int {
        if(songUriList == null) return 0
        var deleteResult = 0
        for (i in songUriList.indices){
            deleteResult += deleteSongItemAtUriOnDatabase(songUriList[i].uri)
        }
        return deleteResult
    }
    private fun deleteSongItemAtUriOnDatabase(uri: String?): Int {
        if(uri == null || uri.isEmpty()) return 0
        return AppDatabase.getDatabase(applicationContext).songItemDao().deleteAtUri(uri)
    }

    private fun tryToDeleteSongsFromSource(
        modelType: String?,
        itemsList: Array<String>?,
        indexColum: String?,
        orderBy: String?,
        whereClause: String?
    ): Array<Int> {
        val resultList = Array(2){0}
        if(
            itemsList == null || itemsList.isEmpty() ||
            modelType == null || modelType.isEmpty()
        ) return resultList

        if(modelType == SongItem.TAG){
            resultList[0] = deleteSongsFromDatabase(itemsList)
            resultList[1] = deleteSongsFromDevice(itemsList)
        }else{
            if(
                indexColum == null || indexColum.isEmpty() ||
                orderBy == null || orderBy.isEmpty() ||
                whereClause == null || whereClause.isEmpty()
            ) return resultList

            for (i in itemsList.indices){
                val tempSongUriList =
                    when (whereClause) {
                        //If it is standard content explorer, then get all songs uri directly
                        WorkerConstantValues.ITEM_LIST_WHERE_EQUAL ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereEqual(
                                indexColum,
                                itemsList[i],
                                orderBy
                            )
                        //If it is from search view, get songs uri directly with "where like" clause
                        WorkerConstantValues.ITEM_LIST_WHERE_LIKE ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereLike(
                                indexColum,
                                itemsList[i],
                                orderBy
                            )
                        else -> null
                    }
                if(tempSongUriList != null && tempSongUriList.isNotEmpty()){
                    resultList[0] += deleteSongsFromDatabase(tempSongUriList)
                    resultList[1] += deleteSongsFromDevice(tempSongUriList)
                    return resultList
                }
            }
        }
        return resultList
    }

    companion object {
        const val TAG = "DeleteSongsWorker"
    }
}