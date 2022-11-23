package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.YearItem

@Dao
interface YearItemDao {

    @Query("SELECT * FROM YearItem WHERE year = :name LIMIT 1")
    fun getAtName(name : String): YearItem?

    @Query("SELECT * FROM YearItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "year", asc_desc_mode: String = "ASC"): LiveData<List<YearItem>>?
}