package com.prosabdev.fluidmusic.workers.queuemusic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.songitem.SongItemUri
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoveSongsFromQueueMusicWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    @SuppressLint("LongLogTag")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val modelType = inputData.getString(WorkerConstantValues.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WorkerConstantValues.ITEM_LIST)
                val whereClause = inputData.getString(WorkerConstantValues.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkerConstantValues.WHERE_COLUMN_INDEX)
                //Delete songs from queue music
                val dataResult = tryToDeleteSongsFromSource(
                    modelType,
                    itemsList,
                    whereClause,
                    whereColumn
                )
                Log.i(TAG, "WORKER $TAG : DELETED SONGS ON QUEUE MUSIC : ${dataResult} ")

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

    private fun removeSongsFromQueueMusic(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0
        var deleteResult = 0
        for (i in stringUriList.indices){
            deleteResult += deleteQueueMusicItemAtUri(stringUriList[i])
        }
        return deleteResult
    }
    private fun removeSongsFromQueueMusic(songUriList: List<SongItemUri>?): Int {
        if(songUriList == null || songUriList.isEmpty()) return 0
        var deleteResult = 0
        for (i in songUriList.indices){
            deleteResult += deleteQueueMusicItemAtUri(songUriList[i].uri)
        }
        return deleteResult
    }
    private fun deleteQueueMusicItemAtUri(uri: String?): Int {
        if(uri == null || uri.isEmpty()) return 0
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao().deleteAtSongUri(uri)
    }

    private fun tryToDeleteSongsFromSource(
        modelType: String?,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): Int {
        var resultList = 0
        if(
            itemsList == null || itemsList.isEmpty() ||
            modelType == null || modelType.isEmpty()
        ) return 0

        if(modelType == SongItem.TAG){
            resultList = removeSongsFromQueueMusic(itemsList)
        }else{
            if(
                whereClause == null || whereClause.isEmpty() ||
                whereColumn == null || whereColumn.isEmpty()
            ) return 0

            for (i in itemsList.indices){
                val tempFieldValue = itemsList[i]
                val tempSongUriList =
                    when (whereClause) {
                        //If it is standard content explorer, then get all songs uri directly
                        WorkerConstantValues.ITEM_LIST_WHERE_EQUAL ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereEqual(
                                whereColumn,
                                tempFieldValue
                            )
                        //If it is from search view, get songs uri directly with "where like" clause
                        WorkerConstantValues.ITEM_LIST_WHERE_LIKE ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereLike(
                                whereColumn,
                                tempFieldValue
                            )
                        else -> null
                    }
                if(tempSongUriList != null && tempSongUriList.isNotEmpty()){
                    resultList += removeSongsFromQueueMusic(tempSongUriList)
                    return resultList
                }
            }
        }
        return resultList
    }

    companion object {
        const val TAG = "QueueMusicRemoveSongsWorker"
    }
}