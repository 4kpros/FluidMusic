package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.explore.YearItem
import kotlinx.coroutines.flow.Flow

@Dao
interface YearItemDao {
    @Query("SELECT * FROM YearItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllYears(order_name: String = "year", asc_desc_mode: String = "ASC"): Flow<List<YearItem>>

    @Query("SELECT * FROM SongItem WHERE year = :year ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForYear(year: String, order_name: String = "year", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}