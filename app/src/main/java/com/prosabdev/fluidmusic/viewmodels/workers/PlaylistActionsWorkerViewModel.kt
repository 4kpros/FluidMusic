package com.prosabdev.fluidmusic.viewmodels.workers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.common.workers.playlist.AddSongsToPlaylistWorker
import com.prosabdev.common.workers.playlist.RemoveSongsFromPlaylistWorker

class PlaylistActionsWorkerViewModel(app: Application) : AndroidViewModel(app) {
    private val outputWorkInfoAddSongsToPlaylist : LiveData<List<WorkInfo>>
    private val outputWorkInfoRemoveSongFromPlaylist : LiveData<List<WorkInfo>>

    private val workManager : WorkManager = WorkManager.getInstance(app.applicationContext)
    init {
        outputWorkInfoAddSongsToPlaylist = workManager.getWorkInfosByTagLiveData(AddSongsToPlaylistWorker.TAG)
        outputWorkInfoRemoveSongFromPlaylist = workManager.getWorkInfosByTagLiveData(
            RemoveSongsFromPlaylistWorker.TAG)
    }
    fun addSongsToPlaylist(
        playlistId: Long,
        modelType: String,
        itemList: Array<String>,
        whereClause: String,
        indexColumn: String,
    ){
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<AddSongsToPlaylistWorker>()
            .setInputData(
                workDataOf(
                    AddSongsToPlaylistWorker.PLAYLIST_ID to playlistId,
                    WorkManagerConst.ITEM_LIST_MODEL_TYPE to modelType,
                    WorkManagerConst.ITEM_LIST to itemList,
                    WorkManagerConst.ITEM_LIST_WHERE to whereClause,
                    WorkManagerConst.WHERE_COLUMN_INDEX to indexColumn
                )
            )
            .addTag(AddSongsToPlaylistWorker.TAG)
            .build()

        workManager.beginUniqueWork(
            AddSongsToPlaylistWorker.TAG,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        ).enqueue()
    }
    fun removeSongsFromPlaylist(
        songUriArray: Array<String>
    ){
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RemoveSongsFromPlaylistWorker>()
            .setInputData(
                workDataOf(
                    RemoveSongsFromPlaylistWorker.SONG_URI_ARRAY to songUriArray
                )
            )
            .addTag(RemoveSongsFromPlaylistWorker.TAG)
            .build()

        workManager.beginUniqueWork(
            RemoveSongsFromPlaylistWorker.TAG,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        ).enqueue()
    }

    fun getOutputWorkInfoAddSongsToPlaylist(): LiveData<List<WorkInfo>> {
        return outputWorkInfoAddSongsToPlaylist
    }
    fun getOutputWorkInfoRemoveSongFromPlaylist(): LiveData<List<WorkInfo>> {
        return outputWorkInfoRemoveSongFromPlaylist
    }
}