package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.YearItem

@Dao
interface YearItemDao {
    @Query("SELECT * FROM YearItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): YearItem?

    @Query(
        "SELECT * FROM YearItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(YearItem.name,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN YearItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN YearItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN YearItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN YearItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN YearItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN YearItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN YearItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN YearItem.numberComposers END," +
                "COALESCE(NULLIF(YearItem.name,''), 'Unknown field')," +
                "YearItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<YearItem>>?

    @Query(
        "SELECT * FROM YearItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(YearItem.name,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN YearItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN YearItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN YearItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN YearItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN YearItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN YearItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN YearItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN YearItem.numberComposers END," +
                "COALESCE(NULLIF(YearItem.name,''), 'Unknown field')," +
                "YearItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<YearItem>?
}