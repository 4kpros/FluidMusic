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
                "CASE :order_by WHEN 'name' THEN COALESCE(FolderItem.name, 'Unknown field') END, FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN FolderItem.parentFolder END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN FolderItem.lastAddedDateToLibrary END, COALESCE(FolderItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN FolderItem.numberArtists END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbums' THEN FolderItem.numberAlbums END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN FolderItem.numberAlbumArtists END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberComposers' THEN FolderItem.numberComposers END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN FolderItem.numberTracks END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN FolderItem.totalDuration END, COALESCE(FolderItem.name, 'Unknown field'), FolderItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<FolderItem>>?
}