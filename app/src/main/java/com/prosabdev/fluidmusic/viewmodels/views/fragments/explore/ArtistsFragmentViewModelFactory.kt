package com.prosabdev.fluidmusic.viewmodels.views.fragments.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArtistsFragmentViewModelFactory(): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArtistsFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}