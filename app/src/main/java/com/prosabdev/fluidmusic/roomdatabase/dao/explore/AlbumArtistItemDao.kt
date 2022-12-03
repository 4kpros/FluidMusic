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
                "CASE :order_by WHEN 'name' THEN COALESCE(AlbumArtistItem.name, 'Unknown field') END COLLATE NOCASE, AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'artist' THEN COALESCE(AlbumArtistItem.artist, 'Unknown field') END COLLATE NOCASE, AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'album' THEN COALESCE(AlbumArtistItem.album, 'Unknown field') END COLLATE NOCASE, AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN AlbumArtistItem.lastUpdateDate END DESC, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END DESC, COALESCE(AlbumArtistItem.name, 'Unknown field') COLLATE NOCASE," +
                "CASE :order_by WHEN 'numberArtists' THEN AlbumArtistItem.numberArtists END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberComposers' THEN AlbumArtistItem.numberComposers END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :order_by WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(order_by: String): LiveData<List<AlbumArtistItem>>?
}