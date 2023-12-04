package com.prosabdev.fluidmusic.viewmodels.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModel(
    application: Application,
) : AndroidViewModel(application) {

    class Factory(
        private val app: Application,
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(app) as T
        }
    }

    companion object {
        private const val TAG = "MainActivityVM"
    }
}

