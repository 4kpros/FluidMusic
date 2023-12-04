package com.prosabdev.fluidmusic.viewmodels.models.equalizer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.equalizer.EqualizerPresetItem
import com.prosabdev.common.roomdatabase.repositories.equalizer.EqualizerPresetItemRepository

class EqualizerPresetItemViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: EqualizerPresetItemRepository? = EqualizerPresetItemRepository(app)

    suspend fun insert(item: EqualizerPresetItem?): Long?{
        return mRepository?.insert(item)
    }

    suspend fun insertMultiple(itemList: List<EqualizerPresetItem>?): List<Long>?{
        return mRepository?.insertMultiple(itemList)
    }

    suspend fun update(item: EqualizerPresetItem?): Int?{
        return mRepository?.update(item)
    }

    suspend fun delete(item: EqualizerPresetItem?): Int?{
        return mRepository?.delete(item)
    }

    suspend fun deleteMultiple(itemList: List<EqualizerPresetItem>?): Int?{
        return mRepository?.deleteMultiple(itemList)
    }

    suspend fun deleteAll(): Int?{
        return mRepository?.deleteAll()
    }

    suspend fun deleteAtId(id: Long): Int?{
        return mRepository?.deleteAtId(id)
    }

    suspend fun deleteAtPresetName(presetName: String?): Int?{
        return mRepository?.deleteAtPresetName(presetName)
    }

    suspend fun getAtId(id: Long): EqualizerPresetItem?{
        return mRepository?.getAtId(id)
    }

    suspend fun getAtPresetName(presetName: String?): EqualizerPresetItem?{
        return mRepository?.getAtPresetName(presetName)
    }
    suspend fun getAll(): LiveData<List<EqualizerPresetItem>?>?{
        return mRepository?.getAll()
    }
}