package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.FolderItem

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM FolderItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): FolderItem?

    @Query(
        "SELECT * FROM FolderItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'name' THEN FolderItem.name END ASC," +
            "CASE :order_by WHEN 'numberTracks' THEN FolderItem.numberTracks END ASC," +
            "CASE :order_by WHEN 'totalDuration' THEN FolderItem.totalDuration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN FolderItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<FolderItem>>?
}