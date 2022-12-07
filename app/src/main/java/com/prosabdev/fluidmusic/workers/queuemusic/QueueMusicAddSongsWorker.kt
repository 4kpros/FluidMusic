package com.prosabdev.fluidmusic.workers.queuemusic

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueMusicAddSongsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try{
                Result.success()
            } catch (error: Throwable) {
                Log.i(ConstantValues.TAG, "Error loading... ${error.stackTrace}")
                Log.i(ConstantValues.TAG, "Error loading... ${error.message}")
                Result.failure()
            }
        }
    }

//    private fun getQueueMusicListFromParams(
//        itemsModelType: String?,
//        itemsList: Array<String>?,
//        itemsListOrderBy: String?,
//        itemsListIsInverted: Boolean
//    ): List<QueueMusicItem> {
//        //Check model type to add on queue music
//        if(itemsModelType == SongItem.TAG){
//            //Add directly to queue music
//        }else{
//            if(itemsModelType == AlbumItem.TAG){
//                //
//            }else if(itemsModelType == AlbumItem.TAG){
//                //
//            }
//        }
//    }

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