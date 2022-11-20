package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.roomdatabase.dao.QueueMusicItemDao

class QueueMusicItemViewModelFactory(private val mQueueMusicItemDao : QueueMusicItemDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QueueMusicItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QueueMusicItemViewModel(mQueueMusicItemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}