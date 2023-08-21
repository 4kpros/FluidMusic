package com.prosabdev.common.workers.queuemusic

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.models.queuemusic.QueueMusicItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.songitem.SongItemUri
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.utils.SystemSettingsUtils
import com.prosabdev.common.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddSongsToQueueMusicWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val modelType = inputData.getString(WorkerConstantValues.ITEM_LIST_MODEL_TYPE)
                val addMethod = inputData.getString(ADD_METHOD)
                val addAtPosition = inputData.getInt(ADD_AT_ORDER, 1)
                val itemsList = inputData.getStringArray(WorkerConstantValues.ITEM_LIST)
                val whereClause = inputData.getString(WorkerConstantValues.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkerConstantValues.WHERE_COLUMN_INDEX)
                Log.i(TAG, "WORKER $TAG : modelType $modelType")
                Log.i(TAG, "WORKER $TAG : addMethod $addMethod")
                Log.i(TAG, "WORKER $TAG : addAtPosition $addAtPosition")
                Log.i(TAG, "WORKER $TAG : itemsList size ${itemsList?.size}")
                Log.i(TAG, "WORKER $TAG : itemsList ${itemsList?.get(0)}")
                Log.i(TAG, "WORKER $TAG : whereClause $whereClause")
                Log.i(TAG, "WORKER $TAG : whereColumn $whereColumn")
                //Add songs to queue music
                val addedSongs = tryToAddSongsToQueueMusic(
                    modelType,
                    addMethod,
                    addAtPosition,
                    itemsList,
                    whereClause,
                    whereColumn
                )
                Log.i(TAG, "WORKER $TAG : ADDED SONGS TO QUEUE MUSIC : $addedSongs ")

                Log.i(TAG, "WORKER $TAG : ended")

                Result.success(
                    workDataOf(
                        WorkerConstantValues.WORKER_OUTPUT_DATA to addedSongs,
                    )
                )
            } catch (error: Throwable) {
                Log.i(TAG, "Error loading... ${error.stackTrace}")
                Log.i(TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }

    private fun tryToAddSongsToQueueMusic(
        modelType: String?,
        addMethod: String?,
        addAtPosition: Int,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): Int {
        if(
            modelType == null || modelType.isEmpty() ||
            addMethod == null || addMethod.isEmpty() ||
            itemsList == null || itemsList.isEmpty()
        ) return 0

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
            if(
                whereClause == null || whereClause.isEmpty() ||
                whereColumn == null || whereColumn.isEmpty()
            ) return 0

            //Get all song uri
            val songUriList = ArrayList<SongItemUri>()
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
    private fun addSongsToQueueMusicDatabase(addMethod: String, stringList: Array<String>?, songUriList: List<SongItemUri>?, addAtPlayOrder: Int, maxPlayOrder: Int): Int {
        if((stringList == null || stringList.isEmpty()) && (songUriList == null || songUriList.isEmpty())) return 0
        var tempResult = 0
        val listSize = (stringList?.size ?: (songUriList?.size ?: 0))
        Log.i(TAG, "WORKER $TAG : songUriList size $listSize")
        when (addMethod) {
            ADD_METHOD_ADD_AT_POSITION -> {
                //Update orders of current queue music from add at position to max play order
                if(maxPlayOrder > 0) {
                    for (i in addAtPlayOrder until maxPlayOrder) {
                        updatePlayOrderOfQueueMusic(i, i + maxPlayOrder)
                    }
                }
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(songUri != null && songUri.isNotEmpty()){
                        val insertResult = insertSongAtPlayOrderOfQueueMusic(songUri, addAtPlayOrder + i)
                        if (insertResult > 0) {
                            tempResult++
                        }
                    }
                }
            }
            ADD_METHOD_ADD_TO_TOP -> {
                if(maxPlayOrder > 0){
                    for(i in 0 .. maxPlayOrder){
                        updatePlayOrderOfQueueMusic(i, i+maxPlayOrder)
                    }
                }
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(songUri != null && songUri.isNotEmpty()){
                        val insertResult = insertSongAtPlayOrderOfQueueMusic(songUri, (maxPlayOrder + i))
                        if (insertResult > 0) {
                            tempResult++
                        }
                    }
                }
            }
            else -> {
                for (i in 0 until listSize){
                    val songUri = stringList?.get(i) ?: (songUriList?.get(i)?.uri)
                    if(songUri != null && songUri.isNotEmpty()){
                        val insertResult = insertSongToEndOfQueueMusic(songUri, (maxPlayOrder + i) + 1)
                        if (insertResult > 0) {
                            tempResult++
                        }
                    }
                }
            }
        }
        Log.i(TAG, "WORKER $TAG : inserted $tempResult")
        return tempResult
    }
    private fun insertSongToEndOfQueueMusic(songUri: String, maxPlayOrder: Int): Long {
        val queueMusicItem = QueueMusicItem()
        queueMusicItem.songUri = songUri
        queueMusicItem.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
        queueMusicItem.playOrder = maxPlayOrder
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .insert(queueMusicItem)
    }
    private fun insertSongAtPlayOrderOfQueueMusic(songUri: String, playOrder: Int): Long {
        val queueMusicItem = QueueMusicItem()
        queueMusicItem.songUri = songUri
        queueMusicItem.addedDate = SystemSettingsUtils.getCurrentDateInMilli()
        queueMusicItem.playOrder = playOrder
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .insert(queueMusicItem)
    }
    private fun updatePlayOrderOfQueueMusic(oldPlayOrder: Int, newPlayOrder: Int): Int {
        return AppDatabase.getDatabase(applicationContext).queueMusicItemDao()
            .updatePlayOrder(oldPlayOrder, newPlayOrder)
    }

    companion object {
        const val TAG = "AddSongsToQMW"

        const val ADD_METHOD = "ADD_METHOD"
        const val ADD_METHOD_ADD_AT_POSITION = "ADD_METHOD_ADD_AT_POSITION"
        const val ADD_METHOD_ADD_TO_TOP = "ADD_METHOD_ADD_TO_TOP"
        const val ADD_METHOD_ADD_TO_BOTTOM = "ADD_METHOD_ADD_TO_BOTTOM"

        const val ADD_AT_ORDER = "ADD_AT_ORDER"
    }
}