package com.prosabdev.fluidmusic.roomdatabase.dao.queuemusic

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.queuemusic.QueueMusicItem

@Dao
interface QueueMusicItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(queueMusicItem: QueueMusicItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(queueMusicItems: List<QueueMusicItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(queueMusicItem: QueueMusicItem?) : Int

    @Delete
    fun delete(queueMusicItem: QueueMusicItem?) : Int

    @Delete
    fun deleteMultiple(queueMusicItem: List<QueueMusicItem>?) : Int

    @Query("DELETE FROM QueueMusicItem")
    fun deleteAll() : Int

    @Query("DELETE FROM QueueMusicItem WHERE id = :id")
    fun deleteAtId(id: Long) : Int

    @Query("SELECT * FROM QueueMusicItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): QueueMusicItem?

    @Query(
        "SELECT * FROM QueueMusicItem "
    )
    fun getAll(): LiveData<List<QueueMusicItem>>?
}