package com.prosabdev.fluidmusic.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRemoveSongsWorker (
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        var isUpdating: Boolean?
        var dataResult: List<Long>?
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "WORKER $TAG : started")

                //Init output results
                isUpdating = true
                dataResult = ArrayList()
                //Extract worker params
                val songUriArray = inputData.getStringArray(SONG_URI_ARRAY)
                if(songUriArray != null && songUriArray.isNotEmpty()){
                    for(i in songUriArray.indices){
                        if(songUriArray[i] != null && songUriArray[i].isNotEmpty()){
                            //Remove songs from playlist from database
                            val deleteResult = AppDatabase.getDatabase(applicationContext).playlistSongItemDao().deleteAtSongUri(songUriArray[i])
                            (dataResult as ArrayList<Long>).add(deleteResult)
                        }
                    }
                }

                isUpdating = false
                Log.i(TAG, "WORKER $TAG : ended")

                Result.success(
                    workDataOf(
                        WorkerConstantValues.WORKER_OUTPUT_IS_UPDATING to isUpdating,
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

        const val SONG_URI_ARRAY = "SONG_URI_ARRAY"
    }
}