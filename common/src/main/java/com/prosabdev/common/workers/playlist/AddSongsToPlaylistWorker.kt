package com.prosabdev.common.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.models.playlist.PlaylistSongItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.utils.SystemSettingsUtils
import com.prosabdev.common.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddSongsToPlaylistWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        var dataResult: List<Long>?
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val playlistId = inputData.getInt(PLAYLIST_ID, -1)
                val modelType = inputData.getString(WorkerConstantValues.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WorkerConstantValues.ITEM_LIST)
                val whereClause = inputData.getString(WorkerConstantValues.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WorkerConstantValues.WHERE_COLUMN_INDEX)
                //Retrieve song list from items list of worker params
                val playlistItemList: List<PlaylistSongItem>? =
                    getPlayListItemsFromParams(
                        playlistId,
                        modelType,
                        itemsList,
                        whereClause,
                        whereColumn
                    )
                //Insert to database
                dataResult = AppDatabase.getDatabase(applicationContext).playlistSongItemDao().insertMultiple(playlistItemList)

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

    private fun getPlayListItemsFromParams(
        playlistId: Int,
        modelType: String?,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): List<PlaylistSongItem>? {
        if(
            itemsList == null || itemsList.isEmpty() ||
            modelType == null || modelType.isEmpty() ||
            playlistId <= 0
        ) return null

        val playlistSongs: ArrayList<PlaylistSongItem> = ArrayList()
        //Check model type to add on queue music
        if(modelType == SongItem.TAG){
            //Add directly if it's an array of songs
            for (i in itemsList.indices){
                val tempPlaylistSongItem = PlaylistSongItem()
                tempPlaylistSongItem.playlistId = playlistId.toLong()
                tempPlaylistSongItem.songUri = itemsList[i]
                tempPlaylistSongItem.lastAddedDateToLibrary = SystemSettingsUtils.getCurrentDateInMilli()
                playlistSongs.add(tempPlaylistSongItem)
            }
        }else{
            if(
                whereClause == null || whereClause.isEmpty() ||
                whereColumn == null || whereColumn.isEmpty()
            ) return null

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
                    for (j in tempSongUriList.indices){
                        val tempPlaylistSongItem = PlaylistSongItem()
                        tempPlaylistSongItem.playlistId = playlistId.toLong()
                        tempPlaylistSongItem.songUri = itemsList[i]
                        tempPlaylistSongItem.lastAddedDateToLibrary = SystemSettingsUtils.getCurrentDateInMilli()
                        playlistSongs.add(tempPlaylistSongItem)
                    }
                }
            }
        }
        return playlistSongs
    }

    companion object {
        const val TAG = "PlaylistAddSongsWorker"

        const val PLAYLIST_ID = "PLAYLIST_ID" //ARGS for playlist id
    }
}