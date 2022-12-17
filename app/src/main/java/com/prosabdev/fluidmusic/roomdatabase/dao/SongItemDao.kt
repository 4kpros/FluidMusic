package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.room.*
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.songitem.SongItemUri

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songItem: SongItem?) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(songItem: SongItem?) : Int

    @Delete
    fun delete(songItem: SongItem?) : Int

    @Query("DELETE FROM SongItem WHERE uri = :uri")
    fun deleteAtUri(uri: String?) : Int

    @Delete
    fun deleteMultiple(songItem: List<SongItem>?) : Int

    @Query("DELETE FROM SongItem")
    fun deleteAll() : Int

    @Query("UPDATE SongItem SET " +
            "isValid = :isValid " +
            "WHERE uri = :uri"
    )
    fun updateValidityAtUri(uri: String?, isValid: Boolean): Int

    @Query("UPDATE SongItem SET " +
            "lastAddedDateToLibrary = :lastAddedDateToLibrary " +
            "WHERE uri = :uri"
    )
    fun updateLastAddedToLibraryAtUri(uri: String?, lastAddedDateToLibrary: Long): Int

    @Query("UPDATE SongItem SET " +
            "playCount = :playCount," +
            "lastPlayed = :lastPlayed " +
            "WHERE uri = :uri"
    )
    fun updatePlayCountAtUri(uri: String?, playCount: Int, lastPlayed: Long): Int

    @Query("UPDATE SongItem SET " +
            "uriTreeId = :uriTreeId," +
            "fileName = :fileName," +
            "title = :title," +
            "artist = :artist," +
            "albumArtist = :albumArtist," +
            "composer = :composer," +
            "album = :album," +
            "genre = :genre," +
            "uriPath = :uriPath," +
            "folder = :folder," +
            "folderParent = :folderParent," +
            "folderUri = :folderUri," +
            "year = :year," +
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
        uri: String?,
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
        author: String?,
        diskNumber: String?,
        writer: String?,
        cdTrackNumber: String?,
        numberTracks: String?,
        comments: String?,
        rating: Int,
        hashedCovertArtSignature: Int,
        isValid: Boolean
    ): Int

    @Query("SELECT * FROM SongItem LIMIT 1")
    fun getFirstSong(): SongItem?

    @Query("SELECT * FROM SongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): SongItem?

    @Query("SELECT * FROM SongItem WHERE uri = :uri LIMIT 1")
    fun getAtUri(uri: String?): SongItem?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'title' THEN COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :orderBy WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(SongItem.artist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(SongItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(SongItem.year,''), '0') END COLLATE NOCASE DESC," +
            "CASE :orderBy WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :orderBy WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :orderBy WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :orderBy WHEN 'genre' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :orderBy WHEN 'typeMime' THEN COALESCE(NULLIF(SongItem.typeMime,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :orderBy WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :orderBy WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :orderBy WHEN 'author' THEN COALESCE(NULLIF(SongItem.author,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'writer' THEN COALESCE(NULLIF(SongItem.writer,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'language' THEN COALESCE(NULLIF(SongItem.language,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :orderBy WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :orderBy WHEN 'id' THEN SongItem.id END ASC," +
            "COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC LIMIT :limit"
    )
    fun getAllDirectlyLimit(orderBy: String?, limit: Int = 50): List<SongItem>?

    @Query("SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'title' THEN COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :orderBy WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(SongItem.artist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(SongItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(SongItem.year,''), '0') END COLLATE NOCASE DESC," +
            "CASE :orderBy WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :orderBy WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :orderBy WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :orderBy WHEN 'genre' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :orderBy WHEN 'typeMime' THEN COALESCE(NULLIF(SongItem.typeMime,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :orderBy WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :orderBy WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :orderBy WHEN 'author' THEN COALESCE(NULLIF(SongItem.author,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'writer' THEN COALESCE(NULLIF(SongItem.writer,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'language' THEN COALESCE(NULLIF(SongItem.language,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :orderBy WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :orderBy WHEN 'id' THEN SongItem.id END ASC," +
            "COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC"
    )
    fun getAllDirectly(orderBy: String?): List<SongItem>?

    @Query("SELECT * FROM " +
            "(" +
            "SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'title' THEN COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :orderBy WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(SongItem.artist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(SongItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(SongItem.year,''), '0') END COLLATE NOCASE DESC," +
            "CASE :orderBy WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :orderBy WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :orderBy WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :orderBy WHEN 'genre' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :orderBy WHEN 'typeMime' THEN COALESCE(NULLIF(SongItem.typeMime,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :orderBy WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :orderBy WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :orderBy WHEN 'author' THEN COALESCE(NULLIF(SongItem.author,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'writer' THEN COALESCE(NULLIF(SongItem.writer,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'language' THEN COALESCE(NULLIF(SongItem.language,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :orderBy WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :orderBy WHEN 'id' THEN SongItem.id END ASC," +
            "COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC" +
            ") as tempAllSong " +
            "WHERE CASE :whereColumn " +
            "WHEN 'album' THEN tempAllSong.album = :columnValue " +
            "WHEN 'albumArtist' THEN tempAllSong.albumArtist = :columnValue  " +
            "WHEN 'artist' THEN tempAllSong.artist = :columnValue " +
            "WHEN 'composer' THEN tempAllSong.composer = :columnValue " +
            "WHEN 'folder' THEN tempAllSong.folder = :columnValue " +
            "WHEN 'genre' THEN tempAllSong.genre = :columnValue  " +
            "WHEN 'year' THEN tempAllSong.year = :columnValue " +
            "END "
    )
    fun getAllDirectlyWhereEqual(whereColumn: String?, columnValue: String?, orderBy: String?): List<SongItem>?

    @Query("SELECT * FROM " +
            "(" +
            "SELECT * FROM SongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'title' THEN COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'fileName' THEN SongItem.fileName END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'cdTrackNumber' THEN SongItem.cdTrackNumber END ASC," +
            "CASE :orderBy WHEN 'diskNumber' THEN SongItem.diskNumber END ASC," +
            "CASE :orderBy WHEN 'artist' THEN COALESCE(NULLIF(SongItem.artist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'album' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'albumArtist' THEN COALESCE(NULLIF(SongItem.albumArtist,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'year' THEN COALESCE(NULLIF(SongItem.year,''), '0') END COLLATE NOCASE DESC," +
            "CASE :orderBy WHEN 'duration' THEN SongItem.duration END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN SongItem.lastAddedDateToLibrary END DESC," +
            "CASE :orderBy WHEN 'lastUpdateDate' THEN SongItem.lastUpdateDate END DESC," +
            "CASE :orderBy WHEN 'path' THEN SongItem.uriPath END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'pathCaseSensitive' THEN SongItem.uriPath END ASC," +
            "CASE :orderBy WHEN 'genre' THEN COALESCE(NULLIF(SongItem.album,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'size' THEN SongItem.size END ASC," +
            "CASE :orderBy WHEN 'typeMime' THEN COALESCE(NULLIF(SongItem.typeMime,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'rating' THEN SongItem.rating END ASC," +
            "CASE :orderBy WHEN 'playCount' THEN SongItem.playCount END ASC," +
            "CASE :orderBy WHEN 'lastPlayed' THEN SongItem.lastPlayed END ASC," +
            "CASE :orderBy WHEN 'author' THEN COALESCE(NULLIF(SongItem.author,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'writer' THEN COALESCE(NULLIF(SongItem.writer,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'language' THEN COALESCE(NULLIF(SongItem.language,''), 'Unknown field') END COLLATE NOCASE ASC," +
            "CASE :orderBy WHEN 'sampleRate' THEN SongItem.sampleRate END ASC," +
            "CASE :orderBy WHEN 'bitrate' THEN SongItem.bitrate END ASC," +
            "CASE :orderBy WHEN 'id' THEN SongItem.id END ASC," +
            "COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC" +
            ") as tempAllSong " +
            "WHERE CASE :whereColumn " +
                "WHEN 'album' THEN tempAllSong.album GLOB '*' || :columnValue || '*' " +
                "WHEN 'albumArtist' THEN tempAllSong.albumArtist GLOB '*' || :columnValue || '*' " +
                "WHEN 'artist' THEN tempAllSong.artist GLOB '*' || :columnValue || '*' " +
                "WHEN 'composer' THEN tempAllSong.composer GLOB '*' || :columnValue || '*'" +
                "WHEN 'folder' THEN tempAllSong.folder GLOB '*' || :columnValue || '*' " +
                "WHEN 'genre' THEN tempAllSong.genre GLOB '*' || :columnValue || '*' " +
                "WHEN 'year' THEN tempAllSong.year GLOB '*' || :columnValue || '*' " +
            "END"
    )
    fun getAllDirectlyWhereLike(whereColumn: String?, columnValue: String?, orderBy: String?): List<SongItem>?

    @Query(
        "SELECT songItem.id as id, " +
                "SongItem.uriTreeId as uriTreeId, " +
                "SongItem.uri as uri " +
                "FROM SongItem " +
                "WHERE CASE :whereColumn " +
                "WHEN 'album' THEN songItem.album = :columnValue " +
                "WHEN 'albumArtist' THEN songItem.albumArtist = :columnValue  " +
                "WHEN 'artist' THEN songItem.artist = :columnValue " +
                "WHEN 'composer' THEN songItem.composer = :columnValue " +
                "WHEN 'folder' THEN songItem.folder = :columnValue " +
                "WHEN 'genre' THEN songItem.genre = :columnValue  " +
                "WHEN 'year' THEN songItem.year = :columnValue " +
                "END " +
                "COLLATE NOCASE ORDER BY COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC"
    )
    fun getAllOnlyUriDirectlyWhereEqual(whereColumn: String?, columnValue: String?): List<SongItemUri>?

    @Query(
        "SELECT songItem.id as id, " +
                "SongItem.uriTreeId as uriTreeId, " +
                "SongItem.uri as uri " +
                "FROM SongItem " +
                "WHERE CASE :whereColumn " +
                "WHEN 'album' THEN songItem.album GLOB '*' || :columnValue || '*' " +
                "WHEN 'albumArtist' THEN songItem.albumArtist GLOB '*' || :columnValue || '*' " +
                "WHEN 'artist' THEN songItem.artist GLOB '*' || :columnValue || '*' " +
                "WHEN 'composer' THEN songItem.composer GLOB '*' || :columnValue || '*'" +
                "WHEN 'folder' THEN songItem.folder GLOB '*' || :columnValue || '*' " +
                "WHEN 'genre' THEN songItem.genre GLOB '*' || :columnValue || '*' " +
                "WHEN 'year' THEN songItem.year GLOB '*' || :columnValue || '*' " +
                "END " +
                "COLLATE NOCASE ORDER BY COALESCE(NULLIF(SongItem.title,''), SongItem.fileName) ASC"
    )
    fun getAllOnlyUriDirectlyWhereLike(whereColumn: String?, columnValue: String?): List<SongItemUri>?
}