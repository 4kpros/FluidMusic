package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.YearItem

@Dao
interface YearItemDao {

    @Query("SELECT * FROM YearItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): YearItem?

    @Query(
        "SELECT * FROM YearItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN COALESCE(YearItem.name, 'Unknown field') END, YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN YearItem.lastUpdateDate END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN YearItem.lastAddedDateToLibrary END, COALESCE(YearItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN YearItem.numberArtists END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbums' THEN YearItem.numberAlbums END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN YearItem.numberAlbumArtists END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberComposers' THEN YearItem.numberComposers END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN YearItem.numberTracks END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN YearItem.totalDuration END, COALESCE(YearItem.name, 'Unknown field'), YearItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<YearItem>>?
}