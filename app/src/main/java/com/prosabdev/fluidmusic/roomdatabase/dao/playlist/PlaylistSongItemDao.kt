package com.prosabdev.fluidmusic.roomdatabase.dao.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem

@Dao
interface PlaylistSongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistSongItem: PlaylistSongItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(playlistSongItems: ArrayList<PlaylistSongItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playlistSongItem: PlaylistSongItem?)

    @Delete
    fun delete(playlistSongItem: PlaylistSongItem?)

    @Delete
    fun deleteMultiple(playlistSongItem: ArrayList<PlaylistSongItem>?)

    @Query("DELETE FROM PlaylistSongItem")
    fun deleteAll()

    @Query("DELETE FROM PlaylistSongItem WHERE id = :id")
    fun deleteAtId(id: Long)

    @Query("SELECT * FROM PlaylistSongItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistSongItem?

    @Query("SELECT * FROM PlaylistSongItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'songId' THEN PlaylistSongItem.songId END ASC," +
            "CASE :order_by WHEN 'playlistId' THEN PlaylistSongItem.playlistId END ASC," +
            "CASE :order_by WHEN 'addedDate' THEN PlaylistSongItem.addedDate END ASC," +
            "CASE :order_by WHEN 'id' THEN PlaylistSongItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<PlaylistSongItem>>
}