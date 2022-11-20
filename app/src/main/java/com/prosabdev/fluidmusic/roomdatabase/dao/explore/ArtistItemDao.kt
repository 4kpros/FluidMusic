package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.ArtistItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistItemDao {
    @Query("SELECT * FROM ArtistItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllArtists(order_name: String = "artist", asc_desc_mode: String = "ASC"): Flow<List<ArtistItem>>

    @Query("SELECT * FROM SongItem WHERE artist = :artist ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForArtist(artist: String, order_name: String = "artist", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}