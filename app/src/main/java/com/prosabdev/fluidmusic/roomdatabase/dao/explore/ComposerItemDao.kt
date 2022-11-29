package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.ComposerItem

@Dao
interface ComposerItemDao {
    @Query("SELECT * FROM ComposerItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): ComposerItem?

    @Query(
        "SELECT * FROM ComposerItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN ComposerItem.name END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN ComposerItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN ComposerItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN ComposerItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<ComposerItem>>?
}