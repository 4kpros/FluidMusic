package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.QueueMusicItemDao
import kotlinx.coroutines.flow.Flow

class QueueMusicItemViewModel(private val mQueueMusicItemDao: QueueMusicItemDao) : ViewModel()  {

    suspend fun getAllQueueMusicList(): Flow<List<QueueMusicItem>> = mQueueMusicItemDao.getQueueMusicList()
}