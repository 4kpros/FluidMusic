package com.prosabdev.fluidmusic.roomdatabase.repositories.equalizer

import android.content.Context
import com.prosabdev.fluidmusic.models.equalizer.EqualizerPresetBandLevelItem
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.equalizer.EqualizerPresetBandLevelItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EqualizerPresetBandLevelItemRepository(ctx : Context) {

    private var mDao: EqualizerPresetBandLevelItemDao? = AppDatabase.getDatabase(ctx).equalizerPresetBandLevelItemDao()

    suspend fun insert(item: EqualizerPresetBandLevelItem?): Long?{
        return withContext(Dispatchers.IO){
            mDao?.insert(item)
        }
    }

    suspend fun insertMultiple(itemList: List<EqualizerPresetBandLevelItem>?): List<Long>?{
        return withContext(Dispatchers.IO){
            mDao?.insertMultiple(itemList)
        }
    }

    suspend fun update(item: EqualizerPresetBandLevelItem?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.update(item)
        }
    }

    suspend fun delete(item: EqualizerPresetBandLevelItem?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.delete(item)
        }
    }

    suspend fun deleteMultiple(itemList: List<EqualizerPresetBandLevelItem>?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteMultiple(itemList)
        }
    }

    suspend fun deleteAtId(id: Long): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAtId(id)
        }
    }

    suspend fun deleteBandAtPresetName(bandId: Int, presetName: String?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteBandAtPresetName(bandId, presetName)
        }
    }

    suspend fun deleteAllBandsAtPresetName(presetName: String?): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAllBandsAtPresetName(presetName)
        }
    }

    suspend fun deleteAll(): Int?{
        return withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }

    suspend fun getAtId(id: Long): EqualizerPresetBandLevelItem?{
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }

    suspend fun getBandAtPresetName(presetName: String?, bandId: Short): EqualizerPresetBandLevelItem?{
        return withContext(Dispatchers.IO){
            mDao?.getBandAtPresetName(presetName, bandId)
        }
    }

    suspend fun getAllAtPresetName(presetName: String?): List<EqualizerPresetBandLevelItem>?{
        return withContext(Dispatchers.IO){
            mDao?.getAllAtPresetName(presetName)
        }
    }
}