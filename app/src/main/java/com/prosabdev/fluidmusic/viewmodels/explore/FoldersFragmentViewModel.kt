package com.prosabdev.fluidmusic.viewmodels.explore

import android.app.Activity
import android.util.Log
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.GenericDataListFetcherViewModel

class FoldersFragmentViewModel : GenericDataListFetcherViewModel() {

    override fun requestLoadDataAsync(
        activity: Activity,
        minToUpdateDataList: Int
    ) {
        super.requestLoadDataAsync(activity, minToUpdateDataList)

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD DATA FROM EXPLORE FOLDERS")

        //First set is loading and is loading in background to true
        setIsLoading(true)
        setIsLoadingInBackground(true)

        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesWithMediaStore(
            activity,
            this as GenericDataListFetcherViewModel,
            10
        )
    }
}