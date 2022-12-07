package com.prosabdev.fluidmusic.roomdatabase.dao.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem

@Dao
interface PlaylistItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistItem: PlaylistItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(playlistItems: List<PlaylistItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playlistItem: PlaylistItem?) : Long

    @Delete
    fun delete(playlistItem: PlaylistItem?)

    @Delete
    fun deleteMultiple(playlistItem: List<PlaylistItem>?) : List<Long>

    @Query("DELETE FROM PlaylistItem")
    fun deleteAll()

    @Query("DELETE FROM PlaylistItem WHERE id = :id")
    fun deleteAtId(id: Long) : Long

    @Query("UPDATE PlaylistItem SET " +
            "name = :name," +
            "isRealFile = :isRealFile " +
            "WHERE uri = :uri"
    )
    fun updateAtUri(
        uri: String,
        name: String,
        isRealFile: Boolean,
    ): Long

    @Query("SELECT MAX(id) FROM PlaylistItem WHERE name GLOB '*' || :playlistName || '*' ")
    fun getMaxIdLikeName(playlistName: String): Long

    @Query("SELECT * FROM PlaylistItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem WHERE name = :name LIMIT 1")
    fun getWithName(name: String): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'name' THEN PlaylistItem.name END ASC," +
            "CASE :order_by WHEN 'lastUpdateDate' THEN PlaylistItem.lastUpdateDate END ASC," +
            "CASE :order_by WHEN 'lastAddedDateToLibrary' THEN PlaylistItem.lastAddedDateToLibrary END ASC," +
            "CASE :order_by WHEN 'id' THEN PlaylistItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<PlaylistItem>>
}