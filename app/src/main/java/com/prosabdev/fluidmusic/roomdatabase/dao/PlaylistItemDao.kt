package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.PlaylistItem
import com.prosabdev.fluidmusic.models.explore.SongItem

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

    @Query("SELECT * FROM PlaylistItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistItem?

    @Query("SELECT * FROM QueueMusicItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "id", asc_desc_mode: String = "ASC"): LiveData<List<PlaylistItem>>
}