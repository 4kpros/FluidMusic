package com.prosabdev.fluidmusic.viewmodels.models.queuemusic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.queuemusic.QueueMusicItem
import com.prosabdev.common.roomdatabase.repositories.queuemusic.QueueMusicItemRepository

class QueueMusicItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: QueueMusicItemRepository? = QueueMusicItemRepository(app)

    suspend fun insert(queueMusicItem: QueueMusicItem?) : Long? {
        return mRepository?.insert(queueMusicItem)
    }
    suspend fun update(queueMusicItem: QueueMusicItem?) {
        mRepository?.update(queueMusicItem)
    }
    suspend fun delete(queueMusicItem: QueueMusicItem?) {
        mRepository?.delete(queueMusicItem)
    }
    suspend fun deleteAll() {
        mRepository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : QueueMusicItem? {
        return mRepository?.getAtId(id)
    }
    suspend fun getAll() : LiveData<List<QueueMusicItem>>? {
        return mRepository?.getAll()
    }
}