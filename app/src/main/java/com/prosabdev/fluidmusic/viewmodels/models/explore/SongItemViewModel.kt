package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.SongItemRepository


class SongItemViewModel(ctx : Context) : ViewModel() {

    private var repository: SongItemRepository? = SongItemRepository(ctx)

    suspend fun insert(songItem: SongItem?) : Long? {
        return repository?.insert(songItem)
    }
    suspend fun update(songItem: SongItem?) {
        repository?.update(songItem)
    }
    suspend fun delete(songItem: SongItem?) {
        repository?.delete(songItem)
    }
    suspend fun deleteAll() {
        repository?.deleteAll()
    }

    //Getters
    suspend fun getFirstSong() : SongItem? {
        return repository?.getFirstSong()
    }
    suspend fun getAtId(id: Long) : SongItem? {
        return repository?.getAtId(id)
    }
    suspend fun getAtUri(uri: String) : SongItem? {
        return repository?.getAtUri(uri)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<SongItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
    suspend fun getAllWithWhereClause(whereColumn: String?, columnValue: String?, order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<SongItem>>? {
        return repository?.getAllWithWhereClause(whereColumn, columnValue, order_name, asc_desc_mode)
    }
}