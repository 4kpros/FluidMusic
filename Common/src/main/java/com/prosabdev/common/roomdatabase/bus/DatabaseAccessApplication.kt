package com.prosabdev.common.roomdatabase.bus

import android.app.Application
import com.prosabdev.common.roomdatabase.AppDatabase

class DatabaseAccessApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}