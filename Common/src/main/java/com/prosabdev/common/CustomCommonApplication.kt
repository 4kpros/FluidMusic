package com.prosabdev.common

import android.app.Application
import com.prosabdev.common.roomdatabase.AppDatabase

class CustomCommonApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}