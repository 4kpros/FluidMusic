package com.prosabdev.common.roomdatabase.dao.equalizer

import androidx.room.*
import com.prosabdev.common.models.equalizer.EqualizerPresetBandLevelItem

@Dao
interface EqualizerPresetBandLevelItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: EqualizerPresetBandLevelItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(itemList: List<EqualizerPresetBandLevelItem>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: EqualizerPresetBandLevelItem): Int

    @Delete
    fun delete(item: EqualizerPresetBandLevelItem): Int

    @Delete
    fun deleteMultiple(itemList: List<EqualizerPresetBandLevelItem>): Int

    @Query("DELETE FROM EqualizerPresetBandLevelItem WHERE id = :id")
    fun deleteAtId(id: Long): Int

    @Query("DELETE FROM EqualizerPresetBandLevelItem WHERE bandId = :bandId AND presetName = :presetName")
    fun deleteBandAtPresetName(bandId: Int, presetName: String): Int

    @Query("DELETE FROM EqualizerPresetBandLevelItem WHERE presetName = :presetName")
    fun deleteAllBandsAtPresetName(presetName: String): Int

    @Query("DELETE FROM EqualizerPresetBandLevelItem")
    fun deleteAll(): Int

    @Query("SELECT * FROM EqualizerPresetBandLevelItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): EqualizerPresetBandLevelItem?

    @Query("SELECT * FROM EqualizerPresetBandLevelItem WHERE presetName = :presetName AND bandId = :bandId LIMIT 1")
    fun getBandAtPresetName(presetName: String, bandId: Short): EqualizerPresetBandLevelItem?

    @Query("SELECT * FROM EqualizerPresetBandLevelItem WHERE presetName = :presetName")
    fun getAllAtPresetName(presetName: String): List<EqualizerPresetBandLevelItem>?
}