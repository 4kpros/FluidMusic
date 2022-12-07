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
    fun update(playlistSongItem: PlaylistSongItem?)

    @Delete
    fun delete(playlistSongItem: PlaylistSongItem?) : Long

    @Delete
    fun deleteMultiple(playlistSongItem: List<PlaylistSongItem>?) : List<Long>

    @Query("DELETE FROM PlaylistSongItem")
    fun deleteAll()

    @Query("DELETE FROM PlaylistSongItem WHERE songUri = :songUri")
    fun deleteAtSongUri(songUri: String) : Long

    @Query("SELECT * FROM PlaylistSongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistSongItem?

    @Query("SELECT * FROM PlaylistSongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'songUri' THEN PlaylistSongItem.songUri END ASC," +
            "CASE :order_by WHEN 'playlistId' THEN PlaylistSongItem.playlistId END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN PlaylistSongItem.lastAddedDateToLibrary END ASC," +
            "CASE :order_by WHEN 'id' THEN PlaylistSongItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<PlaylistSongItem>?>

    @Query("SELECT * FROM PlaylistSongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'songId' THEN PlaylistSongItem.songUri END ASC," +
            "CASE :order_by WHEN 'playlistId' THEN PlaylistSongItem.playlistId END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN PlaylistSongItem.lastAddedDateToLibrary END ASC," +
            "CASE :order_by WHEN 'id' THEN PlaylistSongItem.id END ASC"
    )
    fun getAllDirectly(order_by: String): List<PlaylistSongItem>?
}