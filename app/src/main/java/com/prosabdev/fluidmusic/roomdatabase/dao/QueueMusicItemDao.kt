package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.QueueMusicItem

@Dao
interface QueueMusicItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(queueMusicItem: QueueMusicItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(queueMusicItems: ArrayList<QueueMusicItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(queueMusicItem: QueueMusicItem?)

    @Delete
    fun delete(queueMusicItem: QueueMusicItem?)

    @Delete
    fun deleteMultiple(queueMusicItem: ArrayList<QueueMusicItem>?)

    @Query("DELETE FROM QueueMusicItem")
    fun deleteAll()

    @Query("DELETE FROM QueueMusicItem WHERE id = :id")
    fun deleteAtId(id: Long)

    @Query("SELECT * FROM QueueMusicItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): QueueMusicItem?

    @Query("SELECT * FROM QueueMusicItem " +
            "ORDER BY " +
            "CASE :order_by WHEN 'uriTree' THEN QueueMusicItem.songId END ASC," +
            "CASE :order_by WHEN 'path' THEN QueueMusicItem.addedDate END ASC," +
            "CASE :order_by WHEN 'id' THEN QueueMusicItem.id END ASC"
    )
    fun getAll(order_by: String): LiveData<List<QueueMusicItem>>?
}