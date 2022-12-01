package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.QueueMusicItemRepository

class QueueMusicItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: QueueMusicItemRepository? = QueueMusicItemRepository(app)

    suspend fun insert(queueMusicItem: QueueMusicItem?) : Long? {
        return repository?.insert(queueMusicItem)
    }
    suspend fun update(queueMusicItem: QueueMusicItem?) {
        repository?.update(queueMusicItem)
    }
    suspend fun delete(queueMusicItem: QueueMusicItem?) {
        repository?.delete(queueMusicItem)
    }
    suspend fun deleteAll() {
        repository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : QueueMusicItem? {
        return repository?.getAtId(id)
    }
    suspend fun getAll(order_by: String = "id") : LiveData<List<QueueMusicItem>>? {
        return repository?.getAll(order_by)
    }
}