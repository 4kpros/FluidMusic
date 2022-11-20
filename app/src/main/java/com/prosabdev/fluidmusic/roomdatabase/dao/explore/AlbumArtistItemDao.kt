package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.AlbumArtistItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumArtistItemDao {
    @Query("SELECT * FROM AlbumArtistItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllAlbumArtists(order_name: String = "albumArtist", asc_desc_mode: String = "ASC"): Flow<List<AlbumArtistItem>>

    @Query("SELECT * FROM SongItem WHERE albumArtist = :albumArtist ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForAlbumArtist(albumArtist: String, order_name: String = "albumArtist", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}