package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.AlbumArtistItem

@Dao
interface AlbumArtistItemDao {
    @Query("SELECT * FROM AlbumArtistItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): AlbumArtistItem?

    @Query(
        "SELECT * FROM AlbumArtistItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN AlbumArtistItem.name END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<AlbumArtistItem>>?
}