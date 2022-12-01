package com.prosabdev.fluidmusic.viewmodels.models.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.SongItemRepository


class SongItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: SongItemRepository? = SongItemRepository(app)

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
    suspend fun getAllLimit(order_by: String = "title", limit: Int) : LiveData<List<SongItem>>? {
        return repository?.getAllLimit(order_by, limit)
    }
    suspend fun getAll(order_by: String = "title") : LiveData<List<SongItem>>? {
        return repository?.getAll(order_by)
    }
    suspend fun getAllWhereEqual(whereColumn: String, columnValue: String?, order_by: String = "title") : LiveData<List<SongItem>>? {
        return repository?.getAllWhereEqual(whereColumn, columnValue, order_by)
    }
    suspend fun getAllWhereLike(whereColumn: String, columnValue: String?, order_by: String = "title") : LiveData<List<SongItem>>? {
        return repository?.getAllWhereLike(whereColumn, columnValue, order_by)
    }
}