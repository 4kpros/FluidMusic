package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.GenreItem

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): GenreItem?

    @Query(
        "SELECT * FROM GenreItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN COALESCE(GenreItem.name, 'Unknown field') END, GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN GenreItem.lastUpdateDate END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END, COALESCE(GenreItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN GenreItem.numberArtists END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbums' THEN GenreItem.numberAlbums END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN GenreItem.numberAlbumArtists END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberComposers' THEN GenreItem.numberComposers END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN GenreItem.numberTracks END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN GenreItem.totalDuration END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<GenreItem>>?
}