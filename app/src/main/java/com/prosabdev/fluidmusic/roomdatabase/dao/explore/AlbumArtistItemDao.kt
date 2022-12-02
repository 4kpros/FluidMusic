package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.view.AlbumArtistItem

@Dao
interface AlbumArtistItemDao {
    @Query("SELECT * FROM AlbumArtistItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String): AlbumArtistItem?

    @Query(
        "SELECT * FROM AlbumArtistItem " +
            "ORDER BY " +
                "CASE :order_by WHEN 'name' THEN COALESCE(AlbumArtistItem.name, 'Unknown field') END, AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'artist' THEN COALESCE(AlbumArtistItem.artist, AlbumArtistItem.name, 'Unknown field') END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'albumArtist' THEN COALESCE(AlbumArtistItem.album, AlbumArtistItem.name, 'Unknown field') END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN AlbumArtistItem.lastUpdateDate END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END, COALESCE(AlbumArtistItem.name, 'Unknown field')," +
                "CASE :order_by WHEN 'numberArtists' THEN AlbumArtistItem.numberArtists END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberComposers' THEN AlbumArtistItem.numberComposers END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary," +
                "CASE :order_by WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary"
    )
    fun getAll(order_by: String): LiveData<List<AlbumArtistItem>>?
}