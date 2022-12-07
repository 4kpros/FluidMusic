package com.prosabdev.fluidmusic.roomdatabase.dao.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem

@Dao
interface PlaylistSongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistSongItem: PlaylistSongItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(playlistSongItems: List<PlaylistSongItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playlistSongItem: PlaylistSongItem?) : Int

    @Delete
    fun delete(playlistSongItem: PlaylistSongItem?) : Int

    @Delete
    fun deleteMultiple(playlistSongItem: List<PlaylistSongItem>?) : Int

    @Query("DELETE FROM PlaylistSongItem")
    fun deleteAll() : Int

    @Query("DELETE FROM PlaylistSongItem WHERE songUri = :songUri")
    fun deleteAtSongUri(songUri: String) : Int

    @Query("SELECT * FROM PlaylistSongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistSongItem?

    @Query("SELECT * FROM PlaylistSongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'songUri' THEN PlaylistSongItem.songUri END ASC," +
            "CASE :orderBy WHEN 'playlistId' THEN PlaylistSongItem.playlistId END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN PlaylistSongItem.lastAddedDateToLibrary END ASC," +
            "CASE :orderBy WHEN 'id' THEN PlaylistSongItem.id END ASC"
    )
    fun getAll(orderBy: String): LiveData<List<PlaylistSongItem>?>

    @Query("SELECT * FROM PlaylistSongItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'songId' THEN PlaylistSongItem.songUri END ASC," +
            "CASE :orderBy WHEN 'playlistId' THEN PlaylistSongItem.playlistId END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN PlaylistSongItem.lastAddedDateToLibrary END ASC," +
            "CASE :orderBy WHEN 'id' THEN PlaylistSongItem.id END ASC"
    )
    fun getAllDirectly(orderBy: String): List<PlaylistSongItem>?
}