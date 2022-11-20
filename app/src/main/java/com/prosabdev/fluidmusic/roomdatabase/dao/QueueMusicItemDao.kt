package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.room.*
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueMusicItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun Insert(queueMusicItem: QueueMusicItem?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun InsertMultiple(queueMusicItem: ArrayList<QueueMusicItem?>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun Update(queueMusicItem: QueueMusicItem?)

    @Delete
    fun Delete(queueMusicItem: QueueMusicItem?)

    @Query("DELETE FROM QueueMusicItem")
    fun DeleteAllFromQueueMusic()

    @Query("SELECT * FROM QueueMusicItem")
    fun getQueueMusicList(): Flow<List<QueueMusicItem>>
}