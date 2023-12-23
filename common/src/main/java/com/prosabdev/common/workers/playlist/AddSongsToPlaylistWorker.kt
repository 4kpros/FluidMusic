package com.prosabdev.common.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.components.WMConstants
import com.prosabdev.common.models.playlist.PlaylistSongItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.utils.SystemSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddSongsToPlaylistWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Worker $TAG started")

                //Extract worker params
                val playlistId = inputData.getInt(PLAYLIST_ID, -1)
                val modelType = inputData.getString(WMConstants.ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(WMConstants.ITEM_LIST)
                val whereClause = inputData.getString(WMConstants.ITEM_LIST_WHERE)
                val whereColumn = inputData.getString(WMConstants.WHERE_COLUMN_INDEX)
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
                var indexList = listOf<Long>()
                try {
                    indexList = AppDatabase.getDatabase(applicationContext).playlistSongItemDao().insertMultiple(playlistItemList!!)
                }catch (error: Throwable) {
                    error.printStackTrace()
                }

                Log.i(TAG, "Worker $TAG ended")

                Result.success(
                    workDataOf(
                        WMConstants.WORKER_OUTPUT_DATA to indexList,
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
     * Return a playlist of type [List] of [PlaylistSongItem]
     */
    private fun getPlayListItemsFromParams(
        playlistId: Int,
        modelType: String?,
        itemsList: Array<String>?,
        whereClause: String?,
        whereColumn: String?
    ): List<PlaylistSongItem>? {
        if(itemsList.isNullOrEmpty() || modelType.isNullOrEmpty() || playlistId <= 0) return null

        val playlistSongs: ArrayList<PlaylistSongItem> = ArrayList()
        //Check model type to add on queue music
        if(modelType == SongItem.TAG){
            //Add directly if it's an array of songs
            for (i in itemsList.indices){
                val tempPlaylistSongItem = PlaylistSongItem()
                tempPlaylistSongItem.playlistId = playlistId.toLong()
                tempPlaylistSongItem.songUri = itemsList[i]
                tempPlaylistSongItem.lastAddedDateToLibrary = SystemSettings.getCurrentDateInMillis()
                playlistSongs.add(tempPlaylistSongItem)
            }
        }else{
            if(whereClause.isNullOrEmpty() || whereColumn.isNullOrEmpty()) return null

            for (i in itemsList.indices){
                val tempFieldValue = itemsList[i]
                val tempSongUriList =
                    when (whereClause) {
                        //If it is standard content explorer, then get all songs uri directly
                        WMConstants.ITEM_LIST_WHERE_EQUAL ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereEqual(
                                whereColumn,
                                tempFieldValue
                        )
                        //If it is from search view, get songs uri directly with "where like" clause
                        WMConstants.ITEM_LIST_WHERE_LIKE ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereLike(
                                whereColumn,
                                tempFieldValue
                        )
                        else -> null
                    }
                if(!tempSongUriList.isNullOrEmpty()){
                    for (j in tempSongUriList.indices){
                        val tempPlaylistSongItem = PlaylistSongItem()
                        tempPlaylistSongItem.playlistId = playlistId.toLong()
                        tempPlaylistSongItem.songUri = itemsList[i]
                        tempPlaylistSongItem.lastAddedDateToLibrary = SystemSettings.getCurrentDateInMillis()
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