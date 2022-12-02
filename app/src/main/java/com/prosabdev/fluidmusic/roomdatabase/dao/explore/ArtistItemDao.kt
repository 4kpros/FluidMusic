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
                "CASE :order_by WHEN 'name' THEN COALESCE(ArtistItem.name, 'Unknown field') END, ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN ArtistItem.lastUpdateDate END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN ArtistItem.lastAddedDateToLibrary END, COALESCE(ArtistItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN ArtistItem.numberArtists END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbums' THEN ArtistItem.numberAlbums END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN ArtistItem.numberAlbumArtists END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberComposers' THEN ArtistItem.numberComposers END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN ArtistItem.numberTracks END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN ArtistItem.totalDuration END, COALESCE(ArtistItem.name, 'Unknown field'), ArtistItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<ArtistItem>>?
}