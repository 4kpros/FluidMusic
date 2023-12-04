package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.prosabdev.common.constants.MainConst
import com.prosabdev.common.workers.mediascanner.MediaScannerWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MediaScannerActivityViewModel(app: Application) : AndroidViewModel(app) {

    val isLoadingInBackground = MutableLiveData<Boolean>(false)
    val foldersCounter = MutableLiveData<Int>(0)
    val songsCounter = MutableLiveData<Int>(0)
    val playlistsCounter = MutableLiveData<Int>(0)
    val emptyFolderUriCounter = MutableLiveData<Int>(0)
    val outputWorkInfoItems : LiveData<List<WorkInfo>>

    private val mWorkManager : WorkManager = WorkManager.getInstance(app.applicationContext)

    init {
        outputWorkInfoItems = mWorkManager.getWorkInfosByTagLiveData(MediaScannerWorker.TAG)
    }

    internal fun scanDevice(){
        val scanDeviceWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<MediaScannerWorker>()
            .addTag(MediaScannerWorker.TAG)
            .build()
        mWorkManager.beginUniqueWork(
                MediaScannerWorker.TAG,
                ExistingWorkPolicy.REPLACE,
                scanDeviceWorkRequest
            ).enqueue()
    }

    fun updateWorkInfoData(workInfo : WorkInfo){
        val outputFolderCount = workInfo.outputData.getInt(MediaScannerWorker.OUTPUT_FOLDER_COUNT, 0)
        val outputSongCount = workInfo.outputData.getInt(MediaScannerWorker.OUTPUT_SONG_COUNT, 0)
        val outputPlaylistCount = workInfo.outputData.getInt(MediaScannerWorker.OUTPUT_PLAYLIST_COUNT, 0)

        foldersCounter.value = outputFolderCount
        songsCounter.value = outputSongCount
        playlistsCounter.value = outputPlaylistCount
    }
}