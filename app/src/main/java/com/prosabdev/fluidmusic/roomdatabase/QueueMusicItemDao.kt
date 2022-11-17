package com.prosabdev.fluidmusic.roomdatabase

import androidx.room.*
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.utils.ConstantValues
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

    @Query("DELETE FROM " + ConstantValues.FLUID_MUSIC_QUEUE_MUSIC_TABLE)
    fun DeleteAllFromQueueMusic()

    @Query("SELECT * FROM " + ConstantValues.FLUID_MUSIC_QUEUE_MUSIC_TABLE)
    fun getQueueMusicList(): Flow<List<QueueMusicItem>>
}