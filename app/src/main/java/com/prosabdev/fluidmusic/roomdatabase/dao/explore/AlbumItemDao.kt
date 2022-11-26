package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.AlbumItemView

@Dao
interface AlbumItemDao {
    @Query("SELECT * FROM AlbumItemView WHERE album = :name LIMIT 1")
    fun getAtName(name : String): AlbumItemView?

    @Query("SELECT * FROM AlbumItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "album", asc_desc_mode: String = "ASC"): LiveData<List<AlbumItemView>>?
}