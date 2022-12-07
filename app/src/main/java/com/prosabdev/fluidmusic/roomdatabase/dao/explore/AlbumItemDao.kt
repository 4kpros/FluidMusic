package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.AlbumItem

@Dao
interface AlbumItemDao {
    @Query("SELECT * FROM AlbumItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): AlbumItem?

    @Query(
        "SELECT * FROM AlbumItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(AlbumItem.name, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(AlbumItem.artist, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(AlbumItem.albumArtist, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(AlbumItem.year, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumItem.numberComposers END"
    )
    fun getAll(orderBy: String): LiveData<List<AlbumItem>>?

    @Query(
        "SELECT * FROM AlbumItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(AlbumItem.name, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(AlbumItem.artist, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(AlbumItem.albumArtist, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(AlbumItem.year, 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumItem.numberComposers END"
    )
    fun getAllDirectly(orderBy: String): List<AlbumItem>?
}