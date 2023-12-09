package com.prosabdev.common.roomdatabase.dao.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.common.models.playlist.PlaylistItem

@Dao
interface PlaylistItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistItem: PlaylistItem) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(playlistItems: List<PlaylistItem>) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playlistItem: PlaylistItem) : Int

    @Delete
    fun delete(playlistItem: PlaylistItem) : Int

    @Delete
    fun deleteMultiple(playlistItem: List<PlaylistItem>) : Int

    @Query("DELETE FROM PlaylistItem")
    fun deleteAll() : Int

    @Query("DELETE FROM PlaylistItem WHERE id = :id")
    fun deleteAtId(id: Long) : Int

    @Query("UPDATE PlaylistItem SET " +
            "name = :name," +
            "isRealFile = :isRealFile " +
            "WHERE uri = :uri"
    )
    fun updateAtUri(
        uri: String,
        name: String,
        isRealFile: Boolean,
    ) : Int

    @Query("SELECT MAX(id) FROM PlaylistItem WHERE name GLOB '*' || :playlistName || '*' ")
    fun getMaxIdLikeName(playlistName: String): Long

    @Query("SELECT * FROM PlaylistItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem WHERE name = :name LIMIT 1")
    fun getWithName(name: String): PlaylistItem?

    @Query("SELECT * FROM PlaylistItem " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'name' THEN PlaylistItem.name END ASC," +
            "CASE :orderBy WHEN 'lastUpdateDate' THEN PlaylistItem.lastUpdateDate END ASC," +
            "CASE :orderBy WHEN 'lastAddedDateToLibrary' THEN PlaylistItem.lastAddedDateToLibrary END ASC," +
            "CASE :orderBy WHEN 'id' THEN PlaylistItem.id END ASC"
    )
    fun getAll(orderBy: String): LiveData<List<PlaylistItem>>
}