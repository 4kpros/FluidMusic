package com.prosabdev.fluidmusic.application

import android.app.Application
import com.prosabdev.common.roomdatabase.AppDatabase

class AppApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}