package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.ComposerItem

@Dao
interface ComposerItemDao {
    @Query("SELECT * FROM ComposerItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): ComposerItem?

    @Query(
        "SELECT * FROM ComposerItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN COALESCE(ComposerItem.name, 'Unknown field') END, ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN ComposerItem.lastUpdateDate END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN ComposerItem.lastAddedDateToLibrary END, COALESCE(ComposerItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN ComposerItem.numberArtists END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbums' THEN ComposerItem.numberAlbums END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberAlbumArtists' THEN ComposerItem.numberAlbumArtists END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN ComposerItem.numberTracks END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN ComposerItem.totalDuration END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<ComposerItem>>?
}