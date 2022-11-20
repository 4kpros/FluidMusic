package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.ComposerItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ComposerItemDao {
    @Query("SELECT * FROM ComposerItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllComposers(order_name: String = "composer", asc_desc_mode: String = "ASC"): Flow<List<ComposerItem>>

    @Query("SELECT * FROM SongItem WHERE composer = :composer ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForComposer(composer: String, order_name: String = "composer", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}