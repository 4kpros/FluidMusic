package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.roomdatabase.SongItemDao

class SongItemViewModelFactory(private val mSongItemDao : SongItemDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongItemViewModel(mSongItemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}