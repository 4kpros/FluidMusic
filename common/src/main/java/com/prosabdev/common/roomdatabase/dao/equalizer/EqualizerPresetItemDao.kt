package com.prosabdev.common.roomdatabase.dao.equalizer

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.common.models.equalizer.EqualizerPresetItem

@Dao
interface EqualizerPresetItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: EqualizerPresetItem?): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(itemList: List<EqualizerPresetItem>?): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: EqualizerPresetItem?): Int

    @Delete
    fun delete(item: EqualizerPresetItem?): Int

    @Delete
    fun deleteMultiple(itemList: List<EqualizerPresetItem>?): Int

    @Query("DELETE FROM EqualizerPresetItem")
    fun deleteAll(): Int

    @Query("DELETE FROM EqualizerPresetItem WHERE id = :id")
    fun deleteAtId(id: Long): Int

    @Query("DELETE FROM EqualizerPresetItem WHERE presetName = :presetName")
    fun deleteAtPresetName(presetName: String?): Int

    @Query("SELECT * FROM EqualizerPresetItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): EqualizerPresetItem?

    @Query("SELECT * FROM EqualizerPresetItem WHERE presetName = :presetName LIMIT 1")
    fun getAtPresetName(presetName: String?): EqualizerPresetItem?

    @Query("SELECT * FROM EqualizerPresetItem ORDER BY id")
    fun getAll(): LiveData<List<EqualizerPresetItem>?>?
}