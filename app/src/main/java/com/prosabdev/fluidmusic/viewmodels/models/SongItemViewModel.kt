package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.repositories.songitem.SongItemRepository


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
    suspend fun getAtUri(uri: String?) : SongItem? {
        return repository?.getAtUri(uri)
    }
    suspend fun getAllDirectlyLimit(orderBy: String?, limit: Int) : List<SongItem>? {
        return repository?.getAllDirectlyLimit(orderBy, limit)
    }
    suspend fun getAllDirectly(orderBy: String?) : List<SongItem>? {
        return repository?.getAllDirectly(orderBy)
    }
    suspend fun getAllDirectlyWhereEqual(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return repository?.getAllDirectlyWhereEqual(whereColumn, columnValue, orderBy)
    }
    suspend fun getAllDirectlyWhereLike(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return repository?.getAllDirectlyWhereLike(whereColumn, columnValue, orderBy)
    }
}