package com.prosabdev.common.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.common.models.view.ArtistItem

@Dao
interface ArtistItemDao {
    @Query("SELECT * FROM ArtistItem WHERE name = :name LIMIT 1")
    fun getAtName(name : String?): ArtistItem?

    @Query(
        "SELECT * FROM ArtistItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(ArtistItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(ArtistItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN ArtistItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN ArtistItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN ArtistItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN ArtistItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN ArtistItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN ArtistItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN ArtistItem.numberComposers END," +
                "COALESCE(NULLIF(ArtistItem.name,''), 'Unknown field')," +
                "ArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAll(orderBy: String?): LiveData<List<ArtistItem>>?

    @Query(
        "SELECT * FROM ArtistItem " +
                "ORDER BY " +
                "CASE :orderBy WHEN 'name' THEN COALESCE(NULLIF(ArtistItem.name,''), 'Unknown field') END COLLATE NOCASE," +
                "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(ArtistItem.year,''), '0') END COLLATE NOCASE DESC," +
                "CASE :orderBy WHEN 'lastUpdateDate' THEN ArtistItem.lastUpdateDate END DESC," +
                "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN ArtistItem.lastAddedDateToLibrary END DESC," +
                "CASE :orderBy WHEN 'totalDuration' THEN ArtistItem.totalDuration END," +
                "CASE :orderBy WHEN 'numberTracks' THEN ArtistItem.numberTracks END," +
                "CASE :orderBy WHEN 'numberAlbums' THEN ArtistItem.numberAlbums END," +
                "CASE :orderBy WHEN 'numberAlbumArtists' THEN ArtistItem.numberAlbumArtists END," +
                "CASE :orderBy WHEN 'numberComposers' THEN ArtistItem.numberComposers END," +
                "COALESCE(NULLIF(ArtistItem.name,''), 'Unknown field')," +
                "ArtistItem.lastAddedDateToLibrary DESC"
    )
    fun getAllDirectly(orderBy: String?): List<ArtistItem>?
}