package com.prosabdev.fluidmusic.roomdatabase.dao.explore

import androidx.room.Dao
import androidx.room.Query
import com.prosabdev.fluidmusic.models.explore.FolderItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderItemDao {
    @Query("SELECT * FROM FolderItem ORDER BY :order_name, :asc_desc_mode")
    fun getAllFolders(order_name: String = "folder", asc_desc_mode: String = "ASC"): Flow<List<FolderItem>>

    @Query("SELECT * FROM SongItem WHERE folder = :folder AND uriTreeId = :uriTreeId ORDER BY :order_name, :asc_desc_mode")
    fun getAllSongsForFolder(folder: String, uriTreeId: Int, order_name: String = "folder", asc_desc_mode: String = "ASC"): Flow<List<SongItem>>
}