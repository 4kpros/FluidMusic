package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.workers.DatabaseUpdateEntriesWorker
import com.prosabdev.fluidmusic.workers.MediaScannerWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MediaScannerActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val mMutableIsLoadingInBackground = MutableLiveData<Boolean>(false)
    private val mMutableFoldersCounter = MutableLiveData<Int>(0)
    private val mMutableSongsCounter = MutableLiveData<Int>(0)
    private val mMutablePlaylistsCounter = MutableLiveData<Int>(0)
    private val mMutableEmptyFolderUriCounter = MutableLiveData<Int>(0)

    private val mIsLoadingInBackground: LiveData<Boolean> get() = mMutableIsLoadingInBackground
    private val mFoldersCounter: LiveData<Int> get() = mMutableFoldersCounter
    private val mSongsCounter: LiveData<Int> get() = mMutableSongsCounter
    private val mPlaylistsCounter: LiveData<Int> get() = mMutablePlaylistsCounter
    private val mEmptyFolderUriCounter: LiveData<Int> get() = mMutableEmptyFolderUriCounter

    private val outputWorkInfoItems : LiveData<List<WorkInfo>>
    private val workManager : WorkManager = WorkManager.getInstance(application)
    init {
        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT)
    }
    internal fun scanDevice(scannerMethod : String = ConstantValues.MEDIA_SCANNER_WORKER_METHOD_ADD_NEW){

        val scanDeviceWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<MediaScannerWorker>()
                .setInputData(workDataOf(ConstantValues.MEDIA_SCANNER_WORKER_SCAN_METHOD to scannerMethod))
                .addTag(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT)
                .build()

        val updateDatabaseEntriesWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequest.from(DatabaseUpdateEntriesWorker::class.java)

        workManager
            .beginUniqueWork(
                ConstantValues.MEDIA_SCANNER_WORKER_NAME,
                ExistingWorkPolicy.REPLACE,
                scanDeviceWorkRequest
            ).enqueue()
    }

    fun updateWorkInfoData(workInfo : WorkInfo){
        val outputData = workInfo.outputData.getIntArray(ConstantValues.MEDIA_SCANNER_WORKER_OUTPUT)
        if(outputData != null && outputData.isNotEmpty()){
            setFoldersCounter(outputData[0])
            setSongsCounter(outputData[1])
            setPlaylistsCounter(outputData[2])
        }
    }
    fun getOutputWorkInfoList(): LiveData<List<WorkInfo>> {
        return outputWorkInfoItems
    }
    fun setIsLoadingInBackground(isLoading : Boolean) {
        MainScope().launch {
            mMutableIsLoadingInBackground.value = isLoading
        }
    }
    fun getIsLoadingInBackground(): LiveData<Boolean> {
        return mIsLoadingInBackground
    }
    //
    fun getFoldersCounter(): LiveData<Int> {
        return mFoldersCounter
    }
    fun setFoldersCounter(value : Int) {
        MainScope().launch {
            mMutableFoldersCounter.value = value
        }
    }
    //
    fun getSongsCounter(): LiveData<Int> {
        return mSongsCounter
    }
    fun setSongsCounter(value : Int) {
        MainScope().launch {
            mMutableSongsCounter.value = value
        }
    }
    //
    fun getPlaylistsCounter(): LiveData<Int> {
        return mPlaylistsCounter
    }
    fun setPlaylistsCounter(value : Int) {
        MainScope().launch {
            mMutablePlaylistsCounter.value = value
        }
    }
    //
    fun getEmptyFolderUriCounter(): LiveData<Int> {
        return mEmptyFolderUriCounter
    }
    fun setIncrementEmptyFolderUriCounter() {
        MainScope().launch {
            var oldValue : Int = mEmptyFolderUriCounter.value ?: 0
            oldValue++
            mMutableEmptyFolderUriCounter.value = oldValue
        }
    }
}