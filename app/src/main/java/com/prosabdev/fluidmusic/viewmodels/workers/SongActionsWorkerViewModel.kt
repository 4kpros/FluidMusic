package com.prosabdev.fluidmusic.viewmodels.workers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import com.prosabdev.fluidmusic.workers.delete.DeleteSongsWorker

class SongActionsWorkerViewModel(app: Application) : AndroidViewModel(app) {
    private val outputWorkInfoItems : LiveData<List<WorkInfo>>
    private val workManager : WorkManager = WorkManager.getInstance(app.applicationContext)
    init {
        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(DeleteSongsWorker.TAG)
    }
    fun deleteSongs(
        modelType: String,
        itemList: Array<String>,
        orderBy: String,
        whereClause: String,
        indexColumn: String,
    ){
        val deleteSongsWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DeleteSongsWorker>()
            .setInputData(
                workDataOf(
                    WorkerConstantValues.ITEM_LIST_MODEL_TYPE to modelType,
                    WorkerConstantValues.ITEM_LIST to itemList,
                    WorkerConstantValues.ITEM_LIST_ORDER_BY to orderBy,
                    WorkerConstantValues.ITEM_LIST_WHERE to whereClause,
                    WorkerConstantValues.INDEX_COLUM to indexColumn
                )
            )
            .addTag(DeleteSongsWorker.TAG)
            .build()

        workManager.beginUniqueWork(
            DeleteSongsWorker.TAG,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            deleteSongsWorkRequest
        ).enqueue()
    }

    fun getOutputWorkInfoList(): LiveData<List<WorkInfo>> {
        return outputWorkInfoItems
    }
}