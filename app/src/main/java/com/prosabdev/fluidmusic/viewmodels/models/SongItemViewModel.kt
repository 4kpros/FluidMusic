package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.repositories.songitem.SongItemRepository


class SongItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: SongItemRepository? = SongItemRepository(app)

    suspend fun insert(songItem: SongItem?) : Long? {
        return mRepository?.insert(songItem)
    }
    suspend fun update(songItem: SongItem?) {
        mRepository?.update(songItem)
    }
    suspend fun delete(songItem: SongItem?) {
        mRepository?.delete(songItem)
    }
    suspend fun deleteAll() {
        mRepository?.deleteAll()
    }
    //Getters
    suspend fun getFirstSong() : SongItem? {
        return mRepository?.getFirstSong()
    }
    suspend fun getAtId(id: Long) : SongItem? {
        return mRepository?.getAtId(id)
    }
    suspend fun getAtUri(uri: String?) : SongItem? {
        return mRepository?.getAtUri(uri)
    }
    suspend fun getAllDirectlyLimit(orderBy: String?, limit: Int) : List<SongItem>? {
        return mRepository?.getAllDirectlyLimit(orderBy, limit)
    }
    suspend fun getAllDirectly(orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectly(orderBy)
    }
    suspend fun getAllDirectlyWhereEqual(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectlyWhereEqual(whereColumn, columnValue, orderBy)
    }
    suspend fun getAllDirectlyWhereLike(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectlyWhereLike(whereColumn, columnValue, orderBy)
    }
}