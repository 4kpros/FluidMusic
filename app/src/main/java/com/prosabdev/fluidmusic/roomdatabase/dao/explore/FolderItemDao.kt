package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.FolderItem

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM FolderItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): FolderItem?

    @Query(
        "SELECT * FROM FolderItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(FolderItem.name, 'Unknown field') END, FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(FolderItem.year, 'Unknown field') END COLLATE NOCASE, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN FolderItem.lastUpdateDate END DESC, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN FolderItem.lastAddedDateToLibrary END DESC, COALESCE(FolderItem.name, 'Unknown field'), COALESCE(FolderItem.name, 'Unknown field') DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN FolderItem.totalDuration END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberTracks' THEN FolderItem.numberTracks END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberArtists' THEN FolderItem.numberArtists END , COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbums' THEN FolderItem.numberAlbums END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN FolderItem.numberAlbumArtists END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberComposers' THEN FolderItem.numberComposers END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<FolderItem>>?
}