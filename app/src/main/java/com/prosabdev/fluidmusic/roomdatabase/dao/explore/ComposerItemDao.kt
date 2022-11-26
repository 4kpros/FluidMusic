package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.ComposerItemView

@Dao
interface ComposerItemDao {
    @Query("SELECT * FROM ComposerItemView WHERE composer = :name LIMIT 1")
    fun getAtName(name : String): ComposerItemView?

    @Query("SELECT * FROM ComposerItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "composer", asc_desc_mode: String = "ASC"): LiveData<List<ComposerItemView>>?
}