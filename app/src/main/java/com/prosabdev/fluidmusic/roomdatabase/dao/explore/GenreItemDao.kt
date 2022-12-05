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
                "CASE :order_by WHEN 'name' THEN COALESCE(GenreItem.name, 'Unknown field') END, GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'year' THEN COALESCE(GenreItem.year, 'Unknown field') END COLLATE NOCASE, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN GenreItem.lastUpdateDate END DESC, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END DESC, COALESCE(GenreItem.name, 'Unknown field'), COALESCE(GenreItem.name, 'Unknown field') DESC," +
                "CASE :order_by WHEN 'totalDuration' THEN GenreItem.totalDuration END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberTracks' THEN GenreItem.numberTracks END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberArtists' THEN GenreItem.numberArtists END , COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberAlbums' THEN GenreItem.numberAlbums END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN GenreItem.numberAlbumArtists END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberComposers' THEN GenreItem.numberComposers END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(order_by: String): LiveData<List<GenreItem>>?
}