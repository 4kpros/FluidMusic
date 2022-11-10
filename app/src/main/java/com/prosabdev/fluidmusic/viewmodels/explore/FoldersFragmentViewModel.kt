package com.prosabdev.fluidmusic.viewmodels.explore

import android.app.Activity
import android.util.Log
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.generic.GenericDataListFetcherViewModel
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FoldersFragmentViewModel : GenericSongItemDataListViewModel() {

    override fun requestLoadDataAsync(
        activity: Activity,
        startCursor: Int,
        maxDataCount: Int
    ) {
        super.requestLoadDataAsync(activity, startCursor, maxDataCount)

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD DATA FROM EXPLORE FOLDERS")

        //First set is loading and is loading in background to true
        setIsLoading(true)
        setIsLoadingInBackground(true)

        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesOnDevice(
            activity,
            this@FoldersFragmentViewModel as GenericSongItemDataListViewModel,
            startCursor,
            maxDataCount
        )
    }
}