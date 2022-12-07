package com.prosabdev.fluidmusic.workers.queuemusic

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.queuemusic.QueueMusicItem
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueMusicAddSongsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    private var mIsUpdatingQueueMusic: Boolean = false
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            //Init flag to say that update playlist is not finished
            mIsUpdatingQueueMusic = true

            //Extract worker params
            val itemsModelType = inputData.getString(ITEM_LIST_MODEL_TYPE)
            val itemsListOrderBy = inputData.getString(ITEM_LIST_ORDER_BY)
            val itemsListIsInverted = inputData.getBoolean(ITEM_LIST_IS_INVERTED, false)
            val itemsList = inputData.getStringArray(ITEM_LIST_TO_ADD)
            val addMethod = inputData.getString(ADD_METHOD)

            //Cast list of items to add into queue music item
            val queueMusicItemList: List<QueueMusicItem> =
                getQueueMusicListFromParams(
                    itemsModelType,
                    itemsList,
                    itemsListOrderBy,
                    itemsListIsInverted
                )
            if(addMethod == ADD_METHOD_CLEAR){
                AppDatabase.getDatabase(applicationContext).queueMusicItemDao().deleteAll()
                AppDatabase.getDatabase(applicationContext).queueMusicItemDao().insertMultiple()
                //Set flag to say update have been finished
                //Exit
            }else{
                //Init flag to say that update playlist is not finished
                //Add new one
                //Set flag to say update have been finished
                //Exit
            }
            try {
                mSongList.clear()
                mScanCounter[0] = 0
                mScanCounter[1] = 0
                mScanCounter[2] = 0
                scanDeviceFolderUriTrees(applicationContext, updateMethod)
                mScanCounter[1] = mSongList.size
                if(mSongList.size > 0){
                    launch {
                        AppDatabase.getDatabase(applicationContext).songItemDao().insertMultiple(mSongList)
                    }
                }
                Log.i(ConstantValues.TAG, "Load finished ${mSongList.size}")
                Result.success(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT to mScanCounter))
            } catch (error: Throwable) {
                Log.i(ConstantValues.TAG, "Error loading... ${error.stackTrace}")
                Log.i(ConstantValues.TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }

    private fun getQueueMusicListFromParams(
        itemsModelType: String?,
        itemsList: Array<String>?,
        itemsListOrderBy: String?,
        itemsListIsInverted: Boolean
    ): List<QueueMusicItem> {
        //Check model type to add on queue music
        if(itemsModelType == SongItem.TAG){
            //Add directly to queue music
        }else{
            if(itemsModelType == AlbumItem.TAG){
                //
            }else if(itemsModelType == AlbumItem.TAG){
                //
            }
        }
    }

    companion object {
        const val TAG = "QueueMusicAddSongsWorker"

        const val ITEM_LIST_MODEL_TYPE = "ITEM_LIST_MODEL_TYPE"
        const val ITEM_LIST_ORDER_BY = "ITEM_LIST_ORDER_BY"
        const val ITEM_LIST_IS_INVERTED = "ITEM_LIST_IS_INVERTED"
        const val ITEM_LIST_TO_ADD = "ITEM_LIST_TO_ADD"

        const val ADD_METHOD = "ADD_METHOD"
        const val ADD_METHOD_CLEAR = "ADD_METHOD_CLEAR"
        const val ADD_METHOD_APPEND = "ADD_METHOD_APPEND"

    }
}