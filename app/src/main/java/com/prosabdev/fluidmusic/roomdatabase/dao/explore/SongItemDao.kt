package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.SongItem
import java.util.concurrent.LinkedBlockingQueue

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songItem: SongItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(songItems: LinkedBlockingQueue<SongItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(songItem: SongItem?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateMultiple(songItems: LinkedBlockingQueue<SongItem>?)

//    @Query("UPDATE SongItem SET " +
//            "uriTreeId = :uriTreeId, " +
//            "uri = :uri, " +
//            "uriTreeId = :uriTreeId, " +
//            "fileName = :fileName, " +
//            "title = :title, " +
//            "artist = :artist, " +
//            "albumArtist = :albumArtist, " +
//            "composer = :composer, " +
//            "album = :album, " +
//            "genre = :genre, " +
//            "uriPath = :uriPath, " +
//            "folder = :folder, " +
//            "folderUri = :folderUri, " +
//            "year = :year, " +
//            "duration = :duration, " +
//            "language = :language, " +
//            "typeMime = :typeMime, " +
//            "sampleRate = :sampleRate, " +
//            "bitrate = :bitrate, " +
//            "size = :size, " +
//            "channelCount = :channelCount, " +
//            "fileExtension = :fileExtension, " +
//            "bitPerSample = :bitPerSample, " +
//            "lastUpdateDate = :lastUpdateDate =, " +
//            "lastAddedDateToLibrary = :lastAddedDateToLibrary, " +
//            "author = :author, " +
//            "diskNumber = :diskNumber, " +
//            "writer = :writer, " +
//            "cdTrackNumber = :cdTrackNumber, " +
//            "numberTracks = :numberTracks " +
//            "WHERE SongItem.uri = :uri"
//    )
//    fun updateWithUri(
//        uriTreeId: Long = 0,
//        uri: String? = null,
//        fileName: String? = null,
//        title: String? = null,
//        artist: String? = null,
//        albumArtist: String? = null,
//        composer: String? = null,
//        album: String? = null,
//        genre: String? = null,
//        uriPath: String? = null,
//        folder: String? = null,
//        folderUri: String? = null,
//        year: String? = null,
//        duration: Long = 0,
//        language: String? = null,
//        typeMime: String? = null,
//        sampleRate: Int = 0,
//        bitrate: Double = 0.0,
//        size: Long = 0,
//        channelCount: Int = 0,
//        fileExtension: String? = null,
//        bitPerSample: String? = null,
//        lastUpdateDate: String? = null,
//        lastAddedDateToLibrary: Long = 0,
//        author: String? = null,
//        diskNumber: String? = null,
//        writer: String? = null,
//        cdTrackNumber: String? = null,
//        numberTracks: String? = null,
//    )

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
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END LIMIT :limit"
    )
    fun getAllLimit(order_by: String, limit: Int = 50): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAll(order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn = :columnValue " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAllWhereEqual(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn GLOB '*' || :columnValue || '*' " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'duration' THEN COALESCE(SongItem.duration, 'Unknown field', SongItem.fileName) END, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'size' THEN COALESCE(SongItem.size, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field', SongItem.fileName) END COLLATE NOCASE, SongItem.lastAddedDateToLibrary DESC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END COLLATE NOCASE"
    )
    fun getAllWhereLike(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?
}