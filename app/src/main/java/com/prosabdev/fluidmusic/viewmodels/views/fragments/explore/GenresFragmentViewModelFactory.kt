package com.prosabdev.fluidmusic.viewmodels.views.fragments.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GenresFragmentViewModelFactory(): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenresFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenresFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}