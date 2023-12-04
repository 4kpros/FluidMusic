package com.prosabdev.common.workers.delete

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.songitem.SongItemUri
import com.prosabdev.common.roomdatabase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteSongsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Worker $TAG started")

                //Extract worker params
                val modelType = inputData.getString(WorkManagerConst.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WorkManagerConst.ITEM_LIST)
                val whereClause = inputData.getString(WorkManagerConst.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkManagerConst.WHERE_COLUMN_INDEX)
                //Delete songs from database and on device
                val dataResult = tryToDeleteSongsFromSource(
                    modelType,
                    itemsList,
                    whereClause,
                    whereColumn
                )
                Log.i(TAG, "Worker $TAG result 1 -> ${dataResult[0]}")
                Log.i(TAG, "Worker $TAG result 2 -> ${dataResult[1]}")

                Log.i(TAG, "Worker $TAG ended")

                Result.success(
                    workDataOf(
                        WorkManagerConst.WORKER_OUTPUT_DATA to dataResult,
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
     * Delete songs form on database and device. Return an [Array] of [Int] who is the counter of deleted songs on database at index 0 and deleted songs on device at index 1
     */
    private fun tryToDeleteSongsFromSource(
        modelType: String?,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): Array<Int> {
        val resultList = Array(2){0}
        if(itemsList.isNullOrEmpty() || modelType.isNullOrEmpty()) return resultList

        if(modelType == SongItem.TAG){
            resultList[0] = deleteSongsFromDatabase(itemsList)
            resultList[1] = deleteSongsFromDevice(itemsList)
        }else{
            if(whereClause.isNullOrEmpty() || whereColumn.isNullOrEmpty()) return resultList

            for (i in itemsList.indices){
                val tempFieldValue = itemsList[i]
                val tempSongUriList =
                    when (whereClause) {
                        //If it is standard content explorer, then get all songs uri directly
                        WorkManagerConst.ITEM_LIST_WHERE_EQUAL ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereEqual(
                                whereColumn,
                                tempFieldValue
                            )
                        //If it is from search view, get songs uri directly with "where like" clause
                        WorkManagerConst.ITEM_LIST_WHERE_LIKE ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereLike(
                                whereColumn,
                                tempFieldValue
                            )
                        else -> null
                    }
                if(!tempSongUriList.isNullOrEmpty()){
                    resultList[0] += deleteSongsFromDatabase(tempSongUriList)
                    resultList[1] += deleteSongsFromDevice(tempSongUriList)
                    return resultList
                }
            }
        }
        return resultList
    }

    /**
     * Delete songs from device by passing [Array] of [String] as song list
     */
    private fun deleteSongsFromDevice(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0

        var count = 0
        for (i in stringUriList.indices){
            count += deleteSongItemAtUriOnDevice(stringUriList[i])
        }
        return count
    }

    /**
     * Delete songs from device by passing [Array] of [SongItemUri] as song list
     */
    private fun deleteSongsFromDevice(stringUriList: List<SongItemUri>?): Int {
        if(stringUriList == null) return 0

        var count = 0
        for (i in stringUriList.indices){
            count += deleteSongItemAtUriOnDevice(stringUriList[i].uri)
        }
        return count
    }

    /**
     * Delete song form [Uri] as [String] on device and return 1 if true or 0 else
     */
    private fun deleteSongItemAtUriOnDevice(uri: String?): Int {
        if(uri.isNullOrEmpty()) return 0

        val uriTree = Uri.parse(uri)
        val tempDocFile: DocumentFile? =
            DocumentFile.fromTreeUri(applicationContext, uriTree)
        val count = tempDocFile?.delete() ?: false
        return if(count) 1 else 0
    }

    /**
     * Delete songs form [Array] of [String] as song list on database and return 1 or more if true or 0 else
     */
    private fun deleteSongsFromDatabase(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0
        var count = 0
        for (i in stringUriList.indices){
            count += deleteSongItemAtUriOnDatabase(stringUriList[i])
        }
        return count
    }

    /**
     * Delete songs form [Array] of [SongItemUri] as song list on database and return 1 or more if true or 0 else
     */
    private fun deleteSongsFromDatabase(songUriList: List<SongItemUri>?): Int {
        if(songUriList == null) return 0
        var count = 0
        for (i in songUriList.indices){
            count += deleteSongItemAtUriOnDatabase(songUriList[i].uri)
        }
        return count
    }

    /**
     * Delete song form [Uri] as [String] on database and return 1 if true or 0 else
     */
    private fun deleteSongItemAtUriOnDatabase(uri: String?): Int {
        if(uri.isNullOrEmpty()) return 0
        return AppDatabase.getDatabase(applicationContext).songItemDao().deleteAtUri(uri)
    }

    companion object {
        const val TAG = "DeleteSongsWorker"
    }
}