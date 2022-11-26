package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.AlbumArtistItemView

@Dao
interface AlbumArtistItemDao {
    @Query("SELECT * FROM AlbumArtistItemView WHERE albumArtist = :name LIMIT 1")
    fun getAtName(name : String): AlbumArtistItemView?

    @Query("SELECT * FROM AlbumArtistItemView ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "albumArtist", asc_desc_mode: String = "ASC"): LiveData<List<AlbumArtistItemView>>?
}