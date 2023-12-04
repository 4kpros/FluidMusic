package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.AlbumItem

@Dao
interface AlbumItemDao {
    @Query("SELECT * FROM AlbumItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): AlbumItem?

    @Query(
        "SELECT * FROM AlbumItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(AlbumItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(AlbumItem.artist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(AlbumItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(AlbumItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumItem.numberComposers END," +
                "COALESCE(NULLIF(AlbumItem.name,''), 'Unknown field')," +
                "AlbumItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<AlbumItem>>?

    @Query(
        "SELECT * FROM AlbumItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(AlbumItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(AlbumItem.artist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(AlbumItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(AlbumItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumItem.numberComposers END," +
                "COALESCE(NULLIF(AlbumItem.name,''), 'Unknown field')," +
                "AlbumItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<AlbumItem>?
}