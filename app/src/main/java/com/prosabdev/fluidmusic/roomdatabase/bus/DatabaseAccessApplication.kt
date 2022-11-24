package com.prosabdev.fluidmusic.roomdatabase.bus

import android.app.Application
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase

class DatabaseAccessApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}