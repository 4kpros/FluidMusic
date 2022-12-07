package com.prosabdev.fluidmusic.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRemoveWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        var dataResult: Int = 0
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Extract worker params
                val playlistIdArray = inputData.getIntArray(PLAYLIST_ID_ARRAY)
                if(playlistIdArray != null && playlistIdArray.isNotEmpty()){
                    for(i in playlistIdArray.indices){
                        if(playlistIdArray[i] > 0){
                            //Remove playlist from database
                            dataResult = AppDatabase.getDatabase(applicationContext).playlistItemDao().deleteAtId(playlistIdArray[i].toLong())
                        }
                    }
                }

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

    companion object {
        const val TAG = "PlaylistAddWorker"

        const val PLAYLIST_ID_ARRAY = "PLAYLIST_ID_ARRAY"

    }
}