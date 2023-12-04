package com.prosabdev.fluidmusic.viewmodels.models.equalizer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prosabdev.common.models.equalizer.EqualizerPresetBandLevelItem
import com.prosabdev.common.roomdatabase.repositories.equalizer.EqualizerPresetBandLevelItemRepository

class EqualizerPresetBandLevelItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: EqualizerPresetBandLevelItemRepository? = EqualizerPresetBandLevelItemRepository(app)

    suspend fun insert(item: EqualizerPresetBandLevelItem?): Long?{
        return repository?.insert(item)
    }

    suspend fun insertMultiple(itemList: List<EqualizerPresetBandLevelItem>?): List<Long>?{
        return repository?.insertMultiple(itemList)
    }

    suspend fun update(item: EqualizerPresetBandLevelItem?): Int?{
        return repository?.update(item)
    }

    suspend fun delete(item: EqualizerPresetBandLevelItem?): Int?{
        return repository?.delete(item)
    }

    suspend fun deleteMultiple(itemList: List<EqualizerPresetBandLevelItem>?): Int?{
        return repository?.deleteMultiple(itemList)
    }

    suspend fun deleteAtId(id: Long): Int?{
        return repository?.deleteAtId(id)
    }

    suspend fun deleteBandAtPresetName(bandId: Int, presetName: String?): Int?{
        return repository?.deleteBandAtPresetName(bandId, presetName)
    }

    suspend fun deleteAllBandsAtPresetName(presetName: String?): Int? {
        return repository?.deleteAllBandsAtPresetName(presetName)
    }

    suspend fun deleteAll(): Int?{
        return repository?.deleteAll()
    }

    suspend fun getAtId(id: Long): EqualizerPresetBandLevelItem?{
        return repository?.getAtId(id)
    }

    suspend fun getBandAtPresetName(presetName: String?, bandId: Short): EqualizerPresetBandLevelItem?{
        return repository?.getBandAtPresetName(presetName, bandId)
    }

    suspend fun getAllAtPresetName(presetName: String?): List<EqualizerPresetBandLevelItem>?{
        return repository?.getAllAtPresetName(presetName)
    }
}