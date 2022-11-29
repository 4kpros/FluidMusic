package com.prosabdev.fluidmusic.roomdatabase.dao.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem

@Dao
interface PlaylistItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistItem: PlaylistItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(playlistItems: ArrayList<PlaylistItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playlistItem: PlaylistItem?)

    @Delete
    fun delete(playlistItem: PlaylistItem?)

    @Delete
    fun deleteMultiple(playlistItem: ArrayList<PlaylistItem>?)

    @Query("DELETE FROM PlaylistItem")
    fun deleteAll()

    @Query("DELETE FROM PlaylistItem WHERE id = :id")
    fun deleteAtId(id: Long)

    @Query("SELECT MAX(id) FROM PlaylistItem WHERE name GLOB '*' || :playlistName || '*' ")
    fun getMaxIdLikeName(playlistName: String): Long

    @Query("SELECT * FROM PlaylistItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem WHERE name = :name LIMIT 1")
    fun getWithName(name: String): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'name' THEN PlaylistItem.name END ASC," +
            "CASE :order_by WHEN 'addedDate' THEN PlaylistItem.addedDate END ASC," +
            "CASE :order_by WHEN 'id' THEN PlaylistItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<PlaylistItem>>
}