package com.prosabdev.common.roomdatabase.repositories.equalizer

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.equalizer.EqualizerPresetItem
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.equalizer.EqualizerPresetItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EqualizerPresetItemRepository(ctx : Context) {

    private var mDao: EqualizerPresetItemDao? = AppDatabase.getDatabase(ctx).equalizerPresetItemDao()

    suspend fun insert(item: EqualizerPresetItem?): Long?{
        return withContext(Dispatchers.IO){
            mDao?.insert(item)
        }
    }

    suspend fun insertMultiple(itemList: List<EqualizerPresetItem>?): List<Long>?{
        return withContext(Dispatchers.IO){
            mDao?.insertMultiple(itemList)
        }
    }

    suspend fun update(item: EqualizerPresetItem?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.update(item)
        }
    }

    suspend fun delete(item: EqualizerPresetItem?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.delete(item)
        }
    }

    suspend fun deleteMultiple(itemList: List<EqualizerPresetItem>?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteMultiple(itemList)
        }
    }

    suspend fun deleteAll(): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }

    suspend fun deleteAtId(id: Long): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAtId(id)
        }
    }

    suspend fun deleteAtPresetName(presetName: String?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAtPresetName(presetName)
        }
    }

    suspend fun getAtId(id: Long): EqualizerPresetItem?{
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }

    suspend fun getAtPresetName(presetName: String?): EqualizerPresetItem?{
        return withContext(Dispatchers.IO){
            mDao?.getAtPresetName(presetName)
        }
    }

    suspend fun getAll(): LiveData<List<EqualizerPresetItem>?>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll()
        }
    }
}