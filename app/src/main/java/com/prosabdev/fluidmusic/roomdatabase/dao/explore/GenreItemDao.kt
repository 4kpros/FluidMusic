package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.GenreItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreItemDao {
    @Query("SELECT * FROM GenreItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllGenres(order_name: String = "genre", asc_desc_mode: String = "ASC"): Flow<List<GenreItem>>

    @Query("SELECT * FROM SongItem WHERE genre = :genre ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForGenre(genre: String, order_name: String = "genre", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}