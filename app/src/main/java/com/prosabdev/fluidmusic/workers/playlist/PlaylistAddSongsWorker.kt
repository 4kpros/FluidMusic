package com.prosabdev.fluidmusic.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.SystemSettingsUtils
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistAddSongsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        var dataResult: List<Long> = ArrayList()
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val playlistId = inputData.getInt(PLAYLIST_ID, -1)
                val modelType = inputData.getString(ITEM_LIST_MODEL_TYPE)
                val itemsList = inputData.getStringArray(ITEM_LIST_TO_ADD)
                val orderBy = inputData.getString(ITEM_LIST_ORDER_BY)
                val whereClause = inputData.getString(ITEM_LIST_WHERE)
                val indexColum = inputData.getString(INDEX_COLUM)
                val indexColumValue = inputData.getString(INDEX_COLUM_VALUE)
                val isInverted = inputData.getBoolean(ITEM_LIST_IS_INVERTED, false)
                //Retrieve song list from items list of worker params
                val playlistItemList: List<PlaylistSongItem>? =
                    getPlayListItemsFromParams(
                        playlistId,
                        modelType,
                        itemsList,
                        whereClause,
                        indexColum,
                        indexColumValue,
                        orderBy,
                        isInverted
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
        itemsModelType: String?,
        itemsList: Array<String>?,
        indexColum: String?,
        indexColumValue: String?,
        itemsListOrderBy: String?,
        whereClause: String? = null,
        itemsListIsInverted: Boolean = false
    ): List<PlaylistSongItem>? {
        if(
            itemsList == null || itemsList.isEmpty() ||
            itemsModelType == null || itemsModelType.isEmpty() ||
            playlistId <= 0
        ) return null

        val playlistSongs: ArrayList<PlaylistSongItem> = ArrayList()
        //Check model type to add on queue music
        if(itemsModelType == SongItem.TAG){
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
                indexColum == null || indexColum.isEmpty() ||
                indexColumValue == null || indexColumValue.isEmpty() ||
                itemsListOrderBy == null || itemsListOrderBy.isEmpty() ||
                whereClause == null || whereClause.isEmpty()
            ) return null

            for (i in itemsList.indices){
                val tempSongUriList =
                    when (whereClause) {
                        //If it is standard content explorer, then get all songs uri directly
                        ITEM_LIST_WHERE_EQUAL ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereEqual(
                            indexColum,
                            indexColumValue,
                            itemsListOrderBy
                        )
                        //If it is from search view, get songs uri directly with "where like" clause
                        ITEM_LIST_WHERE_LIKE ->
                            AppDatabase.getDatabase(applicationContext).songItemDao().getAllOnlyUriDirectlyWhereLike(
                            indexColum,
                            indexColumValue,
                            itemsListOrderBy
                        )
                        else -> null
                    }
                if(tempSongUriList != null && tempSongUriList.isNotEmpty()){
                    if(itemsListIsInverted){
                        for (j in tempSongUriList.size-1 .. 0){
                            val tempPlaylistSongItem = PlaylistSongItem()
                            tempPlaylistSongItem.playlistId = playlistId.toLong()
                            tempPlaylistSongItem.songUri = itemsList[i]
                            tempPlaylistSongItem.lastAddedDateToLibrary = SystemSettingsUtils.getCurrentDateInMilli()
                            playlistSongs.add(tempPlaylistSongItem)
                        }
                    }else{
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
        }
        return playlistSongs
    }

    companion object {
        const val TAG = "PlaylistAddSongsWorker"

        const val PLAYLIST_ID = "PLAYLIST_ID" //ARGS for playlist id
        const val ITEM_LIST_TO_ADD = "ITEM_LIST_TO_ADD" //ARGS for list of string to add
        const val ITEM_LIST_MODEL_TYPE = "ITEM_LIST_MODEL_TYPE" //ARGS for the model TAG to be checked
        const val INDEX_COLUM = "INDEX_COLUM" //ARGS for the index column of songs to match in order where the model type is not a song (Eg. Folder, Album, Artist)
        const val INDEX_COLUM_VALUE = "INDEX_COLUM_VALUE" //ARGS for the value of index column to match (Eg. For Folder model we match by the column name)
        const val ITEM_LIST_ORDER_BY = "ITEM_LIST_ORDER_BY" //ARGS for the order of SQL request to get data (only necessary if model type is not a song)
        const val ITEM_LIST_WHERE = "ITEM_LIST_WHERE" //ARGS for where clause. It can be where like(generally for search results) or where equal(generally for content explorer)
        const val ITEM_LIST_WHERE_EQUAL = "ITEM_LIST_WHERE_EQUAL" //ARGS for where equal clause.
        const val ITEM_LIST_WHERE_LIKE = "ITEM_LIST_WHERE_LIKE" //ARGS for where like
        const val ITEM_LIST_IS_INVERTED = "ITEM_LIST_IS_INVERTED" //ARGS to check if the list is inverted
    }
}