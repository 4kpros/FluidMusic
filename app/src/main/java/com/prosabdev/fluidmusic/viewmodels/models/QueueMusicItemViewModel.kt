package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.roomdatabase.dao.QueueMusicItemDao
import com.prosabdev.fluidmusic.roomdatabase.repositories.QueueMusicItemRepository
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.SongItemRepository
import kotlinx.coroutines.flow.Flow

class QueueMusicItemViewModel(val ctx : Context) : ViewModel()  {

    private var repository: QueueMusicItemRepository? = QueueMusicItemRepository(ctx)

}