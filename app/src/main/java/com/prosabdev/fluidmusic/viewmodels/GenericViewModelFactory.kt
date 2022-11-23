package com.prosabdev.fluidmusic.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.viewmodels.models.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.models.PlaylistItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.QueueMusicItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel

class GenericViewModelFactory(val ctx : Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderUriTreeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderUriTreeViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(PlaylistItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(QueueMusicItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QueueMusicItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(SongItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongItemViewModel(ctx) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}