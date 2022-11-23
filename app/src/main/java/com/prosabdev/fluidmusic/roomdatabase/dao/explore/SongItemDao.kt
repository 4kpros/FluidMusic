package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.explore.SongItem

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songItem: SongItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(songItems: ArrayList<SongItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(songItem: SongItem?)

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

    @Query("SELECT * FROM SongItem ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC"): LiveData<List<SongItem>>?

    @Query("SELECT * FROM SongItem WHERE :whereColumn = :columnValue ORDER BY :order_name, :asc_desc_mode")
    fun getAllWithWhereClause(whereColumn: String?, columnValue: String?, order_name: String = "title", asc_desc_mode: String = "ASC"): LiveData<List<SongItem>>?
}