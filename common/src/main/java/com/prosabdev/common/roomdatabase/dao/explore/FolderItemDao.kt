package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.FolderItem

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM FolderItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): FolderItem?

    @Query(
        "SELECT * FROM FolderItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(FolderItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(FolderItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN FolderItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN FolderItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN FolderItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN FolderItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN FolderItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN FolderItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN FolderItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN FolderItem.numberComposers END," +
                "COALESCE(NULLIF(FolderItem.name,''), 'Unknown field')," +
                "FolderItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<FolderItem>>?

    @Query(
        "SELECT * FROM FolderItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(FolderItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(FolderItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN FolderItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN FolderItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN FolderItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN FolderItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN FolderItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN FolderItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN FolderItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN FolderItem.numberComposers END," +
                "COALESCE(NULLIF(FolderItem.name,''), 'Unknown field')," +
                "FolderItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<FolderItem>?
}