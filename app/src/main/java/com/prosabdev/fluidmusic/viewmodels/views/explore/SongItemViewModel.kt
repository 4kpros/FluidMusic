package com.prosabdev.fluidmusic.viewmodels.views.explore

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.SongItemDao
import kotlinx.coroutines.flow.Flow

class SongItemViewModel(private val mSongItemDao: SongItemDao) : ViewModel() {

    suspend fun getDirectlyAllSongsFromSource(
        whereColumn: String? = null,
        columnValue: String? = null,
        orderBy: String = "title",
        ascDescMode : String = "ASC"
    ) : List<SongItem> {
        return if(whereColumn == null || whereColumn.isEmpty())
            mSongItemDao.getDirectlyAllSongsFrom(orderBy, ascDescMode)
        else
            mSongItemDao.getDirectlyAllSongsFromWithWhereClause(whereColumn, columnValue, orderBy, ascDescMode)
    }
    suspend fun getAllSongs(orderBy: String = "title", ascDescMode : String = "ASC"): Flow<List<SongItem>> = mSongItemDao.getSongsList(orderBy, ascDescMode)
    suspend fun insertSongIntoDatabase(songItem : SongItem){
        mSongItemDao.Insert(songItem)
    }
    suspend fun deleteAllFromSongs(){
        mSongItemDao.deleteAllFromSongs()
    }
}