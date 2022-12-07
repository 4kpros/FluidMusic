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
                "CASE :orderBy WHEN 'name' THEN COALESCE(GenreItem.name, 'Unknown field') END, GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(GenreItem.year, 'Unknown field') END COLLATE NOCASE, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN GenreItem.lastUpdateDate END DESC, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END DESC, COALESCE(GenreItem.name, 'Unknown field'), COALESCE(GenreItem.name, 'Unknown field') DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN GenreItem.totalDuration END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberTracks' THEN GenreItem.numberTracks END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberArtists' THEN GenreItem.numberArtists END , COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbums' THEN GenreItem.numberAlbums END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN GenreItem.numberAlbumArtists END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberComposers' THEN GenreItem.numberComposers END, COALESCE(GenreItem.name, 'Unknown field'), GenreItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<GenreItem>>?
}