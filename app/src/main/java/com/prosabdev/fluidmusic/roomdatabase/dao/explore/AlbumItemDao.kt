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
                "CASE :order_by WHEN 'name' THEN COALESCE(AlbumItem.name, 'Unknown field') END COLLATE NOCASE, AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'artist' THEN COALESCE(AlbumItem.artist, 'Unknown field') END COLLATE NOCASE, AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'albumArtist' THEN COALESCE(AlbumItem.albumArtist, 'Unknown field') END COLLATE NOCASE, AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN AlbumItem.lastUpdateDate END DESC, COALESCE(AlbumItem.name, 'Unknown field'), AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END DESC, COALESCE(AlbumItem.name, 'Unknown field') COLLATE NOCASE," +
                "CASE :order_by WHEN 'numberArtists' THEN AlbumItem.numberArtists END, COALESCE(AlbumItem.name, 'Unknown field'), AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberComposers' THEN AlbumItem.numberComposers END, COALESCE(AlbumItem.name, 'Unknown field'), AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberTracks' THEN AlbumItem.numberTracks END, COALESCE(AlbumItem.name, 'Unknown field'), AlbumItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'totalDuration' THEN AlbumItem.totalDuration END, COALESCE(AlbumItem.name, 'Unknown field'), AlbumItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(order_by: String): LiveData<List<AlbumItem>>?
}