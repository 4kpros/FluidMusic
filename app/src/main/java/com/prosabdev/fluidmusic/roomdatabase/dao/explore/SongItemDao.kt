package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.*
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SongItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun Insert(songItem: SongItem?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertMultiple(songItem: ArrayList<SongItem?>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun Update(songItem: SongItem?)

    @Delete
    fun Delete(songItem: SongItem?)

    @Query("DELETE FROM SongItem")
    fun deleteAllFromSongs()

    @Query("SELECT * FROM SongItem ORDER BY :order_name, :asc_desc_mode")
    fun getSongsList(order_name: String = "title", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}