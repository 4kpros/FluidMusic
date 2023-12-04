package com.prosabdev.common.workers.queuemusic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.songitem.SongItemUri
import com.prosabdev.common.roomdatabase.AppDatabase
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
                Log.d(TAG, "Worker $TAG started")

                //Extract worker params
                val modelType = inputData.getString(WorkManagerConst.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WorkManagerConst.ITEM_LIST)
                val whereClause = inputData.getString(WorkManagerConst.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkManagerConst.WHERE_COLUMN_INDEX)
                //Delete songs from queue music
                val count = tryToDeleteSongsFromSource(
                    modelType,
                    itemsList,
                    whereClause,
                    whereColumn
                )

                Log.i(AddSongsToQueueMusicWorker.TAG, "Worker $TAG ended")
                Log.i(AddSongsToQueueMusicWorker.TAG, "Worker $TAG result -> $count")

                Result.success(
                    workDataOf(
                        WorkManagerConst.WORKER_OUTPUT_DATA to count,
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
     * Remove songs from queue music of type [Array] of [String]. Return an [Int] who is the number of item who get removed
     */
    private fun removeSongsFromQueueMusic(stringUriList: Array<String>?): Int {
        if(stringUriList == null) return 0
        var count = 0
        for (i in stringUriList.indices){
            count += deleteQueueMusicItemAtUri(stringUriList[i])
        }
        return count
    }

    /**
     * Remove songs from queue music of type [Array] of [SongItemUri]. Return an [Int] who is the number of item who get removed
     */
    private fun removeSongsFromQueueMusic(songUriList: List<SongItemUri>?): Int {
        if(songUriList.isNullOrEmpty()) return 0
        var count = 0
        for (i in songUriList.indices){
            count += deleteQueueMusicItemAtUri(songUriList[i].uri)
        }
        return count
    }

    /**
     * Delete specific song from [String] uri. Return 1 if the item is deleted and 0 else
     */
    private fun deleteQueueMusicItemAtUri(uri: String?): Int {
        if(uri.isNullOrEmpty()) return 0
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao().deleteAtSongUri(uri)
    }

    /**
     * Delete song from array list(who is the source). Return an [Int] who is the number of item who get deleted
     */
    private fun tryToDeleteSongsFromSource(
        modelType: String?,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): Int {
        var count = 0
        if(itemsList.isNullOrEmpty() || modelType.isNullOrEmpty()) return 0

        if(modelType == SongItem.TAG){
            count = removeSongsFromQueueMusic(itemsList)
        }else{
            if(whereClause.isNullOrEmpty() || whereColumn.isNullOrEmpty()) return 0

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
                    count += removeSongsFromQueueMusic(tempSongUriList)
                    return count
                }
            }
        }
        return count
    }

    companion object {
        const val TAG = "QueueMusicRemoveSongsWorker"
    }
}