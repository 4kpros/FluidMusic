package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.AlbumItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumItemDao {
    @Query("SELECT * FROM AlbumItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllAlbums(order_name: String = "album", asc_desc_mode: String = "ASC"): Flow<List<AlbumItem>>

    @Query("SELECT * FROM SongItem WHERE album = :album ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForAlbum(album: String, order_name: String = "album", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}