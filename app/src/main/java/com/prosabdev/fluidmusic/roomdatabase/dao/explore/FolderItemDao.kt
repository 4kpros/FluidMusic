package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.FolderItemView

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM FolderItemView WHERE folder = :name LIMIT 1")
    fun getAtName(name : String): FolderItemView?

    @Query("SELECT * FROM FolderItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "folder", asc_desc_mode: String = "ASC"): LiveData<List<FolderItemView>>?
}