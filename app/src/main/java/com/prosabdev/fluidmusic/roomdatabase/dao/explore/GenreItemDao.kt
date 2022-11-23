package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.GenreItem

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItem WHERE genre = :name LIMIT 1")
    fun getAtName(name : String): GenreItem?

    @Query("SELECT * FROM GenreItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "genre", asc_desc_mode: String = "ASC"): LiveData<List<GenreItem>>?
}