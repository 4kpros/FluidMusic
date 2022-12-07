package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.songitem.SongItemUriView
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songItem: SongItem?) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(songItem: SongItem?) : Long

    @Delete
    fun delete(songItem: SongItem?) : Long

    @Delete
    fun deleteMultiple(songItem: ArrayList<SongItem>?) : List<Long>

    @Query("DELETE FROM SongItem")
    fun deleteAll()

    @Query("UPDATE SongItem SET " +
            "isValid = :isValid " +
            "WHERE uri = :uri"
    )
    fun updateValidityAtUri(uri: String, isValid: Boolean): Long

    @Query("UPDATE SongItem SET " +
            "lastAddedDateToLibrary = :lastAddedDateToLibrary " +
            "WHERE uri = :uri"
    )
    fun updateLastAddedToLibraryAtUri(uri: String, lastAddedDateToLibrary: Long): Long

    @Query("UPDATE SongItem SET " +
            "playCount = :playCount," +
            "lastPlayed = :lastPlayed " +
            "WHERE uri = :uri"
    )
    fun updatePlayCountAtUri(uri: String, playCount: Int, lastPlayed: Long): Long

    @Query("UPDATE SongItem SET " +
            "uriTreeId = :uriTreeId," +
            "fileName = :fileName," +
            "artist = :artist," +
            "albumArtist = :albumArtist," +
            "composer = :composer," +
            "album = :album," +
            "genre = :genre," +
            "uriPath = :uriPath," +
            "folder = :folder," +
            "folderParent = :folderParent," +
            "folderUri = :folderUri," +
            "duration = :duration," +
            "language = :language," +
            "typeMime = :typeMime," +
            "sampleRate = :sampleRate," +
            "bitrate = :bitrate," +
            "size = :size," +
            "channelCount = :channelCount," +
            "fileExtension = :fileExtension," +
            "bitPerSample = :bitPerSample," +
            "lastUpdateDate = :lastUpdateDate," +
            "author = :author," +
            "diskNumber = :diskNumber," +
            "writer = :writer," +
            "cdTrackNumber = :cdTrackNumber," +
            "numberTracks = :numberTracks," +
            "comments = :comments," +
            "rating = :rating," +
            "hashedCovertArtSignature = :hashedCovertArtSignature, " +
            "isValid = :isValid " +
            "WHERE uri = :uri"
    )
    fun updateAtUri(
        uri: String,
        uriTreeId: Long,
        fileName: String?,
        title: String?,
        artist: String?,
        albumArtist: String?,
        composer: String?,
        album: String?,
        genre: String?,
        uriPath: String?,
        folder: String?,
        folderParent: String?,
        folderUri: String?,
        year: String?,
        duration: Long,
        language: String?,
        typeMime: String?,
        sampleRate: Int,
        bitrate: Double,
        size: Long,
        channelCount: Int,
        fileExtension: String?,
        bitPerSample: String?,
        lastUpdateDate: Long,
        lastAddedDateToLibrary: Long,
        author: String?,
        diskNumber: String?,
        writer: String?,
        cdTrackNumber: String?,
        numberTracks: String?,
        comments: String?,
        rating: Int,
        playCount: Int,
        lastPlayed: Long,
        hashedCovertArtSignature: Int,
        isValid: Boolean
    ): Long

    @Query("SELECT * FROM SongItem LIMIT 1")
    fun getFirstSong(): SongItem?

    @Query("SELECT * FROM SongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): SongItem?

    @Query("SELECT * FROM SongItem WHERE uri = :uri LIMIT 1")
    fun getAtUri(uri: String): SongItem?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
            "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END ASC LIMIT :limit"
    )
    fun getAllLimit(order_by: String, limit: Int = 50): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
            "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
            "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAllDirectly(order_by: String): List<SongItem>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn = :columnValue " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
            "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAllWhereEqual(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn GLOB '*' || :columnValue || '*' " +
            "ORDER BY " +
            "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
            "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAllWhereLike(whereColumn: String, columnValue: String?, order_by: String): LiveData<List<SongItem>>?


    @Query(
        "SELECT * FROM SongItemUriView " +
                "INNER JOIN SongItem ON SongItemUriView.id = SongItem.id " +
                "WHERE :whereColumn = :columnValue " +
                "ORDER BY " +
                "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
                "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
                "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
                "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
                "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
                "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
                "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
                "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
                "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
                "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
                "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
                "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAllOnlyUriDirectlyWhereEqual(whereColumn: String, columnValue: String?, order_by: String): List<SongItemUriView>?

    @Query(
        "SELECT * FROM SongItemUriView " +
                "INNER JOIN SongItem ON SongItemUriView.id = SongItem.id " +
                "WHERE :whereColumn GLOB '*' || :columnValue || '*' " +
                "ORDER BY " +
                "CASE :order_by WHEN 'title' THEN COALESCE(SongItem.title, SongItem.fileName) END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
                "CASE :order_by WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
                "CASE :order_by WHEN 'artist' THEN COALESCE(SongItem.artist, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'album' THEN COALESCE(SongItem.album, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'albumArtist' THEN COALESCE(SongItem.albumArtist, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'year' THEN COALESCE(SongItem.year, 'Unknown field') END COLLATE NOCASE DESC," +
                "CASE :order_by WHEN 'duration' THEN SongItem.duration END ASC," +
                "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
                "CASE :order_by WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
                "CASE :order_by WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
                "CASE :order_by WHEN 'genre' THEN COALESCE(SongItem.genre, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'size' THEN SongItem.size END ASC," +
                "CASE :order_by WHEN 'typeMime' THEN COALESCE(SongItem.typeMime, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'rating' THEN SongItem.rating END ASC," +
                "CASE :order_by WHEN 'playCount' THEN SongItem.playCount END ASC," +
                "CASE :order_by WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
                "CASE :order_by WHEN 'author' THEN COALESCE(SongItem.author, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'writer' THEN COALESCE(SongItem.writer, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'language' THEN COALESCE(SongItem.language, 'Unknown field') END COLLATE NOCASE ASC," +
                "CASE :order_by WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
                "CASE :order_by WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
                "CASE :order_by WHEN 'id' THEN SongItem.id END ASC"
    )
    fun getAllOnlyUriDirectlyWhereLike(whereColumn: String, columnValue: String?, order_by: String): List<SongItemUriView>?
}