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
                "CASE :orderBy WHEN 'name' THEN COALESCE(AlbumArtistItem.name, 'Unknown field') END, AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(AlbumArtistItem.artist, 'Unknown field') END COLLATE NOCASE, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'album' THEN COALESCE(AlbumArtistItem.album, 'Unknown field') END COLLATE NOCASE, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(AlbumArtistItem.year, 'Unknown field') END COLLATE NOCASE, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumArtistItem.lastUpdateDate END DESC, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END DESC, COALESCE(AlbumArtistItem.name, 'Unknown field'), COALESCE(AlbumArtistItem.name, 'Unknown field') DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumArtistItem.numberArtists END , COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
//                "CASE :orderBy WHEN 'numberAlbums' THEN AlbumArtistItem.numberAlbums END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
//                "CASE :orderBy WHEN 'numberAlbumArtists' THEN AlbumArtistItem.numberAlbumArtists END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumArtistItem.numberComposers END, COALESCE(AlbumArtistItem.name, 'Unknown field'), AlbumArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String): LiveData<List<AlbumArtistItem>>?
}