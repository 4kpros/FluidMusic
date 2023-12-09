package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.ComposerItem

@Dao
interface ComposerItemDao {
    @Query("SELECT * FROM ComposerItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): ComposerItem?

    @Query(
        "SELECT * FROM ComposerItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(ComposerItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(ComposerItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN ComposerItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN ComposerItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN ComposerItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN ComposerItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN ComposerItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN ComposerItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN ComposerItem.numberAlbumArtists END," +
                "COALESCE(NULLIF(ComposerItem.name,''), 'Unknown field')," +
                "ComposerItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<ComposerItem>>?

    @Query(
        "SELECT * FROM ComposerItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(ComposerItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(ComposerItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN ComposerItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN ComposerItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN ComposerItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN ComposerItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN ComposerItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN ComposerItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN ComposerItem.numberAlbumArtists END," +
                "COALESCE(NULLIF(ComposerItem.name,''), 'Unknown field')," +
                "ComposerItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String): List<ComposerItem>?
}