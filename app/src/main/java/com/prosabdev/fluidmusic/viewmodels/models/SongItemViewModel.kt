package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.repositories.songitem.SongItemRepository

class SongItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: SongItemRepository? = SongItemRepository(app)

    suspend fun insert(songItem: SongItem?) : Long? {
        return mRepository?.insert(songItem ?: return null)
    }
    suspend fun update(songItem: SongItem?) {
        mRepository?.update(songItem ?: return)
    }
    suspend fun delete(songItem: SongItem?) {
        mRepository?.delete(songItem ?: return)
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
        return mRepository?.getAtUri(uri ?: return null)
    }
    suspend fun getAllDirectlyLimit(orderBy: String?, limit: Int) : List<SongItem>? {
        return mRepository?.getAllDirectlyLimit(orderBy ?: return null, limit)
    }
    suspend fun getAllDirectly(orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectly(orderBy ?: return null)
    }
    suspend fun getAllDirectlyWhereEqual(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectlyWhereEqual(whereColumn ?: return null, columnValue ?: return null, orderBy ?: return null)
    }
    suspend fun getAllDirectlyWhereLike(whereColumn: String?, columnValue: String?, orderBy: String?) : List<SongItem>? {
        return mRepository?.getAllDirectlyWhereLike(whereColumn ?: return null, columnValue ?: return null, orderBy ?: return null)
    }
}