package com.prosabdev.fluidmusic.viewmodels.explore

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.generic.GenericDataListFetcherViewModel
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AllSongsFragmentViewModel : GenericSongItemDataListViewModel() {
    fun getDataListSongs(): LiveData<ArrayList<SongItem>> {
        return super.getDataList() as LiveData<ArrayList<SongItem>>
    }
    fun setDataListSongs(songList : ArrayList<SongItem>) {
        super.setDataList(songList as ArrayList<Any>)
    }

    override fun requestLoadDataAsync(activity: Activity, startCursor: Int, maxDataCount: Int) {
        super.requestLoadDataAsync(activity, startCursor, maxDataCount)

        Log.i(ConstantValues.TAG, "ON REQUEST LOAD DATA FROM EXPLORE SONGS")

        //First set is loading and is loading in background to true
        setIsLoading(true)
        setIsLoadingInBackground(true)

        //Else load songs from MediaFileScanner
        MediaFileScanner.scanAudioFilesOnDevice(
            activity,
            this@AllSongsFragmentViewModel as GenericSongItemDataListViewModel,
            startCursor,
            maxDataCount
        )
    }
}