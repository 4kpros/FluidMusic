package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.ArtistItem

@Dao
interface ArtistItemDao {
    @Query("SELECT * FROM ArtistItem WHERE artist = :name LIMIT 1")
    fun getAtName(name : String): ArtistItem?

    @Query("SELECT * FROM ArtistItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "artist", asc_desc_mode: String = "ASC"): LiveData<List<ArtistItem>>?
}