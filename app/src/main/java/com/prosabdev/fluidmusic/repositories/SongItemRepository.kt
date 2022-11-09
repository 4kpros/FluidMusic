package com.prosabdev.fluidmusic.repositories

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.roomdatabase.SongItemDao


class SongItemRepository(mContext: Context) {
    private var mSongItemDao: SongItemDao? = null
    private var mSongList: LiveData<ArrayList<SongItem>>? = null

    //methods for database operations :-
    fun insert(songItem: SongItem?) {
        //
    }

    fun insertMultiple(songs: ArrayList<SongItem?>?) {
        //
    }

    fun update(songModel: SongItem?) {
        //
    }

    fun delete(songModel: SongItem?) {
        //
    }

    fun deleteAllSongs() {
        //
    }

    fun getAllSongs(): LiveData<List<SongItem>>? {
        return null
    }
}