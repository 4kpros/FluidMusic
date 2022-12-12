package com.prosabdev.fluidmusic.viewmodels.workers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.prosabdev.fluidmusic.workers.WorkerConstantValues
import com.prosabdev.fluidmusic.workers.queuemusic.AddSongsToQueueMusicWorker
import com.prosabdev.fluidmusic.workers.queuemusic.RemoveSongsFromQueueMusicWorker

class QueueMusicActionsWorkerViewModel(app: Application) : AndroidViewModel(app) {
    private val outputWorkInfoAddSongsToQueueMusic : LiveData<List<WorkInfo>>
    private val outputWorkInfoRemoveSongFromQueueMusic : LiveData<List<WorkInfo>>

    private val workManager : WorkManager = WorkManager.getInstance(app.applicationContext)
    init {
        outputWorkInfoAddSongsToQueueMusic = workManager.getWorkInfosByTagLiveData(
            AddSongsToQueueMusicWorker.TAG)
        outputWorkInfoRemoveSongFromQueueMusic = workManager.getWorkInfosByTagLiveData(
            RemoveSongsFromQueueMusicWorker.TAG)
    }

    fun addSongsToQueueMusic(
        modelType: String,
        addMethod: String,
        addAtOrder: Int,
        itemList: Array<String>,
        orderBy: String,
        whereClause: String,
        indexColumn: String,
    ){
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<AddSongsToQueueMusicWorker>()
            .setInputData(
                workDataOf(
                    WorkerConstantValues.ITEM_LIST_MODEL_TYPE to modelType,
                    AddSongsToQueueMusicWorker.ADD_METHOD to addMethod,
                    AddSongsToQueueMusicWorker.ADD_AT_ORDER to addAtOrder,
                    WorkerConstantValues.ITEM_LIST to itemList,
                    WorkerConstantValues.ITEM_LIST_ORDER_BY to orderBy,
                    WorkerConstantValues.ITEM_LIST_WHERE to whereClause,
                    WorkerConstantValues.INDEX_COLUM to indexColumn
                )
            )
            .addTag(AddSongsToQueueMusicWorker.TAG)
            .build()

        workManager.beginUniqueWork(
            AddSongsToQueueMusicWorker.TAG,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        ).enqueue()
    }
    fun removeSongsFromQueueMusic(
        modelType: String,
        itemList: Array<String>,
        orderBy: String,
        whereClause: String,
        indexColumn: String,
    ){
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RemoveSongsFromQueueMusicWorker>()
            .setInputData(
                workDataOf(
                    WorkerConstantValues.ITEM_LIST_MODEL_TYPE to modelType,
                    WorkerConstantValues.ITEM_LIST to itemList,
                    WorkerConstantValues.ITEM_LIST_ORDER_BY to orderBy,
                    WorkerConstantValues.ITEM_LIST_WHERE to whereClause,
                    WorkerConstantValues.INDEX_COLUM to indexColumn
                )
            )
            .addTag(RemoveSongsFromQueueMusicWorker.TAG)
            .build()

        workManager.beginUniqueWork(
            RemoveSongsFromQueueMusicWorker.TAG,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        ).enqueue()
    }

    fun getOutputWorkInfoAddSongsToQueueMusic(): LiveData<List<WorkInfo>> {
        return outputWorkInfoAddSongsToQueueMusic
    }
    fun getOutputWorkInfoRemoveSongFromQueueMusic(): LiveData<List<WorkInfo>> {
        return outputWorkInfoRemoveSongFromQueueMusic
    }
}