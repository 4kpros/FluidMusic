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
                "CASE :orderBy WHEN 'name' THEN COALESCE(ComposerItem.name, 'Unknown field') END, ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(ComposerItem.year, 'Unknown field') END COLLATE NOCASE, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN ComposerItem.lastUpdateDate END DESC, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN ComposerItem.lastAddedDateToLibrary END DESC, COALESCE(ComposerItem.name, 'Unknown field'), COALESCE(ComposerItem.name, 'Unknown field') DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN ComposerItem.totalDuration END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberTracks' THEN ComposerItem.numberTracks END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberArtists' THEN ComposerItem.numberArtists END , COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbums' THEN ComposerItem.numberAlbums END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN ComposerItem.numberAlbumArtists END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC"
//                "CASE :orderBy WHEN 'numberComposers' THEN ComposerItem.numberComposers END, COALESCE(ComposerItem.name, 'Unknown field'), ComposerItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<ComposerItem>>?
}