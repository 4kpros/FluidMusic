package com.prosabdev.common.workers.queuemusic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.common.models.queuemusic.QueueMusicItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.songitem.SongItemUri
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.utils.SystemSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddSongsToQueueMusicWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    @SuppressLint("LongLogTag")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Worker $TAG started")

                //Extract worker params
                val modelType = inputData.getString(WorkManagerConst.ITEM_LIST_MODEL_TYPE)
                val addMethod = inputData.getString(ADD_METHOD)
                val addAtPosition = inputData.getInt(ADD_AT_ORDER, 1)
                val itemsList = inputData.getStringArray(WorkManagerConst.ITEM_LIST)
                val whereClause = inputData.getString(WorkManagerConst.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkManagerConst.WHERE_COLUMN_INDEX)
                //Add songs to queue music
                val count = tryToAddSongsToQueueMusic(
                    modelType,
                    addMethod,
                    addAtPosition,
                    itemsList,
                    whereClause,
                    whereColumn
                )
                Log.i(TAG, "Worker $TAG ended")
                Log.i(TAG, "Worker $TAG result -> $count")

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
     * Method used to add songs to queue music. It return an [Int] who is the number of added songs
     */
    private fun tryToAddSongsToQueueMusic(
        modelType: String?,
        addMethod: String?,
        addAtPosition: Int,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): Int {
        if(modelType.isNullOrEmpty() || addMethod.isNullOrEmpty() || itemsList.isNullOrEmpty()) return 0

        val maxPlayOrder = AppDatabase.getDatabase(applicationContext).queueMusicItemDao().getMaxPlayOrderAtId()
        if(modelType == SongItem.TAG){
            return addSongsToQueueMusicDatabase(
                addMethod,
                itemsList,
                null,
                addAtPosition,
                maxPlayOrder
            )
        }else{
            if(whereClause.isNullOrEmpty() || whereColumn.isNullOrEmpty()) return 0

            //Get all songs
            val songUriList = ArrayList<SongItemUri>()
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
                tempSongUriList?.let {
                    songUriList.addAll(it)
                }
            }
            return addSongsToQueueMusicDatabase(
                addMethod,
                null,
                songUriList,
                addAtPosition,
                maxPlayOrder
            )
        }
    }

    /**
     * Method used to add songs to queue music on SQLIte database. It return an [Int] who is the number of inserted items
     */
    private fun addSongsToQueueMusicDatabase(
        addMethod: String,
        stringList: Array<String>?,
        songUriList: List<SongItemUri>?,
        addAtPlayOrder: Int,
        maxPlayOrder: Int
    ): Int {
        if(stringList.isNullOrEmpty() && songUriList.isNullOrEmpty()) return 0

        var count = 0
        val listSize = (stringList?.size ?: (songUriList?.size ?: 0))
        when (addMethod) {
            //If add method is ADD SONG AT SPECIFIC POSITION IN QUEUE MUSIC
            ADD_METHOD_ADD_AT_POSITION -> {
                if(maxPlayOrder > 0) {
                    for (i in addAtPlayOrder until maxPlayOrder) {
                        updatePlayOrderOfQueueMusic(i, i + maxPlayOrder)
                    }
                }
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(!songUri.isNullOrEmpty()){
                        val insertResult = insertSongAtPlayOrderOfQueueMusic(songUri, addAtPlayOrder + i)
                        if (insertResult > 0) {
                            count++
                        }
                    }
                }
            }
            //If add method is ADD SONG TO TOP OF QUEUE MUSIC
            ADD_METHOD_ADD_TO_TOP -> {
                if(maxPlayOrder > 0){
                    for(i in 0 .. maxPlayOrder){
                        updatePlayOrderOfQueueMusic(i, i+maxPlayOrder)
                    }
                }
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(!songUri.isNullOrEmpty()){
                        val insertResult = insertSongAtPlayOrderOfQueueMusic(songUri, (maxPlayOrder + i))
                        if (insertResult > 0) {
                            count++
                        }
                    }
                }
            }
            //Else ADD SONG AT BOTTOM OF QUEUE MUSIC
            else -> {
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(!songUri.isNullOrEmpty()){
                        val insertResult = insertSongToEndOfQueueMusic(songUri, (maxPlayOrder + i) + 1)
                        if (insertResult > 0) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    /**
     *
     */
    private fun insertSongToEndOfQueueMusic(songUri: String, maxPlayOrder: Int): Long {
        val queueMusicItem = QueueMusicItem()
        queueMusicItem.songUri = songUri
        queueMusicItem.addedDate = SystemSettings.getCurrentDateInMillis()
        queueMusicItem.playOrder = maxPlayOrder
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .insert(queueMusicItem)
    }
    private fun insertSongAtPlayOrderOfQueueMusic(songUri: String, playOrder: Int): Long {
        val queueMusicItem = QueueMusicItem()
        queueMusicItem.songUri = songUri
        queueMusicItem.addedDate = SystemSettings.getCurrentDateInMillis()
        queueMusicItem.playOrder = playOrder
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .insert(queueMusicItem)
    }
    private fun updatePlayOrderOfQueueMusic(oldPlayOrder: Int, newPlayOrder: Int): Int {
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .updatePlayOrder(oldPlayOrder, newPlayOrder)
    }

    companion object {
        const val TAG = "AddSongsToQueueMusicWorker"

        const val ADD_METHOD = "ADD_METHOD"
        const val ADD_METHOD_ADD_AT_POSITION = "ADD_METHOD_ADD_AT_POSITION"
        const val ADD_METHOD_ADD_TO_TOP = "ADD_METHOD_ADD_TO_TOP"
        const val ADD_METHOD_ADD_TO_BOTTOM = "ADD_METHOD_ADD_TO_BOTTOM"

        const val ADD_AT_ORDER = "ADD_AT_ORDER"
    }
}