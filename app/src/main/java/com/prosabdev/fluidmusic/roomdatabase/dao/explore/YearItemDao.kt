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
                "CASE :orderBy WHEN 'name' THEN COALESCE(YearItem.name, 'Unknown field') END DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN YearItem.lastUpdateDate END DESC, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN YearItem.lastAddedDateToLibrary END DESC, COALESCE(YearItem.name, 'Unknown field') DESC, COALESCE(YearItem.name, 'Unknown field') DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN YearItem.totalDuration END, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberTracks' THEN YearItem.numberTracks END, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberArtists' THEN YearItem.numberArtists END , COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbums' THEN YearItem.numberAlbums END, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN YearItem.numberAlbumArtists END, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberComposers' THEN YearItem.numberComposers END, COALESCE(YearItem.name, 'Unknown field') DESC, YearItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<YearItem>>?
}