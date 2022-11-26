package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.ArtistItemView

@Dao
interface ArtistItemDao {
    @Query("SELECT * FROM ArtistItemView WHERE artist = :name LIMIT 1")
    fun getAtName(name : String): ArtistItemView?

    @Query("SELECT * FROM ArtistItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "artist", asc_desc_mode: String = "ASC"): LiveData<List<ArtistItemView>>?
}