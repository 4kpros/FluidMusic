package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.viewmodels.models.explore.*

class ModelsViewModelFactory(val ctx : Context) : ViewModelProvider.Factory {

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
        }else if(modelClass.isAssignableFrom(AlbumArtistItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumArtistItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(AlbumArtistItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumArtistItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(ArtistItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArtistItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(ComposerItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComposerItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(FolderItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(GenreItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenreItemViewModel(ctx) as T
        }else if(modelClass.isAssignableFrom(YearItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YearItemViewModel(ctx) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}