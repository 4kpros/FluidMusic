package com.prosabdev.fluidmusic.viewmodels.views.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainFragmentViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}