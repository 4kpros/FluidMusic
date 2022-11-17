package com.prosabdev.fluidmusic.roomdatabase

import androidx.room.*
import com.prosabdev.fluidmusic.models.collections.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
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

    @Query("DELETE FROM " +ConstantValues.FLUID_MUSIC_SONG_TABLE)
    fun deleteAllFromSongs()

    @Query("SELECT * FROM " +ConstantValues.FLUID_MUSIC_SONG_TABLE+ " ORDER BY :order_name, :asc_desc_mode")
    fun getSongsList(order_name: String?, asc_desc_mode: String? = "ASC"): Flow<List<SongItem>>
}