package com.prosabdev.fluidmusic.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderUriTreeTimeSpanUpdateCheckWorker(
    ctx: Context,
    params : WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Default) {
            try {

                //Operate here

                Result.success()
            } catch (error: Throwable) {
                Result.failure()
            }
        }
    }
}