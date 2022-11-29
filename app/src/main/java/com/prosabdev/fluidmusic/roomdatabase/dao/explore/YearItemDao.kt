package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.YearItem

@Dao
interface YearItemDao {

    @Query("SELECT * FROM YearItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): YearItem?

    @Query(
        "SELECT * FROM YearItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN YearItem.name END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN YearItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN YearItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN YearItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<YearItem>>?
}