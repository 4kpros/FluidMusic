package com.prosabdev.common.workers.playlist

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.prosabdev.common.components.WMConstants
import com.prosabdev.common.roomdatabase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoveSongsFromPlaylistWorker (
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Worker $TAG started")

                //Extract worker params
                val songUriArray = inputData.getStringArray(SONG_URI_ARRAY)
                //Do work : remove songs from playlist
                var count = 0
                if(!songUriArray.isNullOrEmpty()){
                    for(i in songUriArray.indices){
                        if(songUriArray[i] != null && songUriArray[i].isNotEmpty()){
                            //Also remove on database
                            count = AppDatabase.getDatabase(applicationContext).playlistSongItemDao().deleteAtSongUri(songUriArray[i])
                        }
                    }
                }

                Log.i(TAG, "Worker $TAG ended")

                Result.success(
                    workDataOf(
                        WMConstants.WORKER_OUTPUT_DATA to count,
                    )
                )
            } catch (error: Throwable) {
                Log.i(TAG, "Error stack trace -> ${error.stackTrace}")
                Log.i(TAG, "Error message -> ${error.message}")
                Result.failure()
            }
        }
    }
    companion object {
        const val TAG = "PlaylistAddWorker"

        const val SONG_URI_ARRAY = "SONG_URI_ARRAY"
    }
}