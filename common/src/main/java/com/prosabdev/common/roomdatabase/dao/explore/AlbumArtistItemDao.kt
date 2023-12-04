package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.AlbumArtistItem

@Dao
interface AlbumArtistItemDao {
    @Query("SELECT * FROM AlbumArtistItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): AlbumArtistItem?

    @Query(
        "SELECT * FROM AlbumArtistItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(AlbumArtistItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(AlbumArtistItem.artist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(AlbumArtistItem.album,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(AlbumArtistItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumArtistItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumArtistItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumArtistItem.numberComposers END," +
                "COALESCE(NULLIF(AlbumArtistItem.name,''), 'Unknown field')," +
                "AlbumArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<AlbumArtistItem>>?

    @Query(
        "SELECT * FROM AlbumArtistItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(AlbumArtistItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(AlbumArtistItem.artist,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(AlbumArtistItem.album,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(AlbumArtistItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN AlbumArtistItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN AlbumArtistItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN AlbumArtistItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN AlbumArtistItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberArtists' THEN AlbumArtistItem.numberArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN AlbumArtistItem.numberComposers END," +
                "COALESCE(NULLIF(AlbumArtistItem.name,''), 'Unknown field')," +
                "AlbumArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<AlbumArtistItem>?
}