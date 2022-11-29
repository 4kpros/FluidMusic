package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.ArtistItem

@Dao
interface ArtistItemDao {
    @Query("SELECT * FROM ArtistItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): ArtistItem?

    @Query(
        "SELECT * FROM ArtistItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN ArtistItem.name END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN ArtistItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN ArtistItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN ArtistItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<ArtistItem>>?
}