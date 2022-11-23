package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.roomdatabase.repositories.PlaylistItemRepository
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.SongItemRepository

class PlaylistItemViewModel(val ctx : Context) : ViewModel() {

    private var repository: PlaylistItemRepository? = PlaylistItemRepository(ctx)
}