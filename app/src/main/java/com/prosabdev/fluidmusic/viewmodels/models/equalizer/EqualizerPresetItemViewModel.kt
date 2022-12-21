package com.prosabdev.fluidmusic.viewmodels.models.equalizer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.equalizer.EqualizerPresetItem
import com.prosabdev.fluidmusic.roomdatabase.repositories.FolderUriTreeRepository
import com.prosabdev.fluidmusic.roomdatabase.repositories.equalizer.EqualizerPresetItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EqualizerPresetItemViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: EqualizerPresetItemRepository? = EqualizerPresetItemRepository(app)

    suspend fun insert(item: EqualizerPresetItem?): Long?{
        return repository?.insert(item)
    }

    suspend fun insertMultiple(itemList: List<EqualizerPresetItem>?): List<Long>?{
        return repository?.insertMultiple(itemList)
    }

    suspend fun update(item: EqualizerPresetItem?): Int?{
        return repository?.update(item)
    }

    suspend fun delete(item: EqualizerPresetItem?): Int?{
        return repository?.delete(item)
    }

    suspend fun deleteMultiple(itemList: List<EqualizerPresetItem>?): Int?{
        return repository?.deleteMultiple(itemList)
    }

    suspend fun deleteAll(): Int?{
        return repository?.deleteAll()
    }

    suspend fun deleteAtId(id: Long): Int?{
        return repository?.deleteAtId(id)
    }

    suspend fun deleteAtPresetName(presetName: String?): Int?{
        return repository?.deleteAtPresetName(presetName)
    }

    suspend fun getAtId(id: Long): EqualizerPresetItem?{
        return repository?.getAtId(id)
    }

    suspend fun getAtPresetName(presetName: String?): EqualizerPresetItem?{
        return repository?.getAtPresetName(presetName)
    }
    suspend fun getAll(): LiveData<List<EqualizerPresetItem>?>?{
        return repository?.getAll()
    }
}