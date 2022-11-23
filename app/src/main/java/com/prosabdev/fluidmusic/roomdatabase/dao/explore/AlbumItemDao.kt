package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.AlbumItem

@Dao
interface AlbumItemDao {
    @Query("SELECT * FROM AlbumItem WHERE album = :name LIMIT 1")
    fun getAtName(name : String): AlbumItem?

    @Query("SELECT * FROM AlbumItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "album", asc_desc_mode: String = "ASC"): LiveData<List<AlbumItem>>?
}