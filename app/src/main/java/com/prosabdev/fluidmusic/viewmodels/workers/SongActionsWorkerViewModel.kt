package com.prosabdev.fluidmusic.viewmodels.workers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.common.workers.delete.DeleteSongsWorker

class SongActionsWorkerViewModel(app: Application) : AndroidViewModel(app) {
    private val outputWorkInfoItems : LiveData<List<WorkInfo>>
    private val workManager : WorkManager = WorkManager.getInstance(app.applicationContext)
    init {
        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(DeleteSongsWorker.TAG)
    }

    fun deleteSongs(
        modelType: String,
        itemsList: MutableCollection<String>?,
        whereClause: String,
        whereColumn: String
    ){
        if(itemsList == null) return
        val deleteSongsWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DeleteSongsWorker>()
            .setInputData(
                workDataOf(
                    WorkManagerConst.ITEM_LIST_MODEL_TYPE to modelType,
                    WorkManagerConst.ITEM_LIST to itemsList.toTypedArray(),
                    WorkManagerConst.ITEM_LIST_WHERE to whereClause,
                    WorkManagerConst.WHERE_COLUMN_INDEX to whereColumn
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