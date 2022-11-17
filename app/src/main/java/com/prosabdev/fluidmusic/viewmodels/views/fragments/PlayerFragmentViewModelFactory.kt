package com.prosabdev.fluidmusic.viewmodels.views.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayerFragmentViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}