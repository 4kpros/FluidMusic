package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.GenreItemView

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItemView WHERE genre = :name LIMIT 1")
    fun getAtName(name : String): GenreItemView?

    @Query("SELECT * FROM GenreItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "genre", asc_desc_mode: String = "ASC"): LiveData<List<GenreItemView>>?
}