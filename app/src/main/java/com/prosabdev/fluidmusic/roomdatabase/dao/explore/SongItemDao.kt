package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.SongItem

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songItem: SongItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(songItems: ArrayList<SongItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(songItem: SongItem?)

    @Delete
    fun delete(songItem: SongItem?)

    @Delete
    fun deleteMultiple(songItem: ArrayList<SongItem>?)

    @Query("DELETE FROM SongItem")
    fun deleteAll()

    @Query("SELECT * FROM SongItem LIMIT 1")
    fun getFirstSong(): SongItem?

    @Query("SELECT * FROM SongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): SongItem?

    @Query("SELECT * FROM SongItem WHERE uri = :uri LIMIT 1")
    fun getAtUri(uri: String): SongItem?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown artist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown album', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown typeMime', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN COALESCE(SongItem.lastAddedDateToLibrary, 'Unknown lastAddedDateToLibrary', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN COALESCE(SongItem.lastUpdateDate, 'Unknown lastUpdateDate', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE LIMIT :limit"
    )
    fun getAllLimit(order_by: String, limit: Int = 50): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown artist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown album', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown typeMime', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN COALESCE(SongItem.lastAddedDateToLibrary, 'Unknown lastAddedDateToLibrary', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN COALESCE(SongItem.lastUpdateDate, 'Unknown lastUpdateDate', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAll(order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn = :columnValue " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown artist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown album', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown typeMime', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN COALESCE(SongItem.lastAddedDateToLibrary, 'Unknown lastAddedDateToLibrary', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN COALESCE(SongItem.lastUpdateDate, 'Unknown lastUpdateDate', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAllWhereEqual(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn GLOB '*' || :columnValue || '*' " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown artist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown album', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown albumArtist', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown typeMime', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN COALESCE(SongItem.lastAddedDateToLibrary, 'Unknown lastAddedDateToLibrary', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN COALESCE(SongItem.lastUpdateDate, 'Unknown lastUpdateDate', SongItem.fileName) END COLLATE NOCASE," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAllWhereLike(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?
}