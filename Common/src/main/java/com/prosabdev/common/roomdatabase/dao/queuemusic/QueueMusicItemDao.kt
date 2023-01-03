package com.prosabdev.common.roomdatabase.dao.queuemusic

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.common.models.queuemusic.QueueMusicItem

@Dao
interface QueueMusicItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(queueMusicItem: QueueMusicItem?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(queueMusicItems: List<QueueMusicItem>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(queueMusicItem: QueueMusicItem?) : Int

    @Query("UPDATE QueueMusicItem SET " +
            "playOrder = :newPlayOrder " +
            "WHERE playOrder = :oldPlayOrder"
    )
    fun updatePlayOrder(oldPlayOrder: Int, newPlayOrder: Int) : Int

    @Delete
    fun delete(queueMusicItem: QueueMusicItem?) : Int

    @Delete
    fun deleteMultiple(queueMusicItem: List<QueueMusicItem>?) : Int

    @Query("DELETE FROM QueueMusicItem")
    fun deleteAll() : Int

    @Query("DELETE FROM QueueMusicItem WHERE id = :id")
    fun deleteAtId(id: Long) : Int

    @Query("DELETE FROM QueueMusicItem WHERE songUri = :uri")
    fun deleteAtSongUri(uri: String?) : Int

    @Query("SELECT * FROM QueueMusicItem WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): QueueMusicItem?

    @Query("SELECT MAX(playOrder) FROM QueueMusicItem LIMIT 1")
    fun getMaxPlayOrderAtId(): Int

    @Query(
        "SELECT * FROM QueueMusicItem "
    )
    fun getAll(): LiveData<List<QueueMusicItem>>?
}