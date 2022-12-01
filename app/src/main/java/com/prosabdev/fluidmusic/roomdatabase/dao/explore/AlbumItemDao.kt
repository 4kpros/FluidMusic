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
                "CASE :order_by WHEN 'name' THEN AlbumItem.name END ASC," +
                "CASE :order_by WHEN 'albumArtist' THEN AlbumItem.albumArtist END ASC," +
                "CASE :order_by WHEN 'artist' THEN AlbumItem.artist END ASC," +
                "CASE :order_by WHEN 'numberTracks' THEN AlbumItem.numberTracks END ASC," +
                "CASE :order_by WHEN 'totalDuration' THEN AlbumItem.totalDuration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN AlbumItem.lastAddedDateToLibrary END ASC"
    )
    fun getAll(order_by: String): LiveData<List<AlbumItem>>?
}