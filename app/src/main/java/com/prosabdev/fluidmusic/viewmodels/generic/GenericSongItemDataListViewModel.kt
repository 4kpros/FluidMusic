package com.prosabdev.fluidmusic.viewmodels.generic

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues

open class GenericSongItemDataListViewModel : GenericDataListFetcherViewModel() {

    fun getSongList(): LiveData<ArrayList<SongItem>> {
        return super.getDataList() as LiveData<ArrayList<SongItem>>
    }
    fun setSongList(songList: ArrayList<SongItem>)  {
        super.setDataList(songList as ArrayList<Any>)
    }
}