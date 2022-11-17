package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.collections.SongItem
import com.prosabdev.fluidmusic.roomdatabase.SongItemDao
import kotlinx.coroutines.flow.Flow

class SongItemViewModel(private val mSongItemDao: SongItemDao) : ViewModel() {

    suspend fun getAllSongs(orderBy: String?, ascDescMode : String?): Flow<List<SongItem>> = mSongItemDao.getSongsList(orderBy, ascDescMode)
    suspend fun insertSongIntoDatabase(songItem : SongItem){
        mSongItemDao.Insert(songItem)
    }
    suspend fun deleteAllFromSongs(){
        mSongItemDao.deleteAllFromSongs()
    }
}