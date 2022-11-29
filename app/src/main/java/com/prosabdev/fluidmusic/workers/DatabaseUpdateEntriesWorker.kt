package com.prosabdev.fluidmusic.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseUpdateEntriesWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker (ctx, params){

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
//                AppDatabase.getDatabase(applicationContext).songItemDao().getSongsList().collect {
//                    for (i in it.indices) {
//                        val uriExist: Boolean = checkIfUriExist(it[i].uri)
//                        if (!uriExist)
//                            deleteSongFromDatabase(it[i])
//                    }
//                }
                Result.success()
            } catch (error: Throwable) {
                Result.failure()
            }
        }
    }

    private fun deleteSongFromDatabase(songItem: SongItem) {
        AppDatabase.getDatabase(applicationContext).songItemDao().delete(songItem)
    }

    private fun checkIfUriExist(uri: String?): Boolean {
        return false
    }
}