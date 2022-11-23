package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.QueueMusicItemRepository

class QueueMusicItemViewModel(ctx : Context) : ViewModel()  {

    private var repository: QueueMusicItemRepository? = QueueMusicItemRepository(ctx)

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
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<QueueMusicItem>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}