package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.GenreItem

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): GenreItem?

    @Query(
        "SELECT * FROM GenreItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN GenreItem.name END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN GenreItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN GenreItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<GenreItem>>?
}