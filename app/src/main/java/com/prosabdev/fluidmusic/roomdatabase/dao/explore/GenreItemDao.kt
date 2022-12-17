package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.GenreItem

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): GenreItem?

    @Query(
        "SELECT * FROM GenreItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(GenreItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(GenreItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN GenreItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN GenreItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN GenreItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN GenreItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN GenreItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN GenreItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN GenreItem.numberComposers END," +
                "COALESCE(NULLIF(GenreItem.name,''), 'Unknown field')," +
                "GenreItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<GenreItem>>?

    @Query(
        "SELECT * FROM GenreItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(GenreItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(GenreItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN GenreItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN GenreItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN GenreItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN GenreItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN GenreItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN GenreItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN GenreItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN GenreItem.numberComposers END," +
                "COALESCE(NULLIF(GenreItem.name,''), 'Unknown field')," +
                "GenreItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<GenreItem>?
}