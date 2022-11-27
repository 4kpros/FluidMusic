package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree

@Dao
interface FolderUriTreeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(folderUriTree: FolderUriTree?) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMultiple(folderUriTrees: ArrayList<FolderUriTree>?) : List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(folderUriTree: FolderUriTree?)

    @Delete
    fun delete(folderUriTree: FolderUriTree?)

    @Delete
    fun deleteMultiple(folderUriTree: ArrayList<FolderUriTree>?)

    @Query("DELETE FROM FolderUriTree")
    fun deleteAll()

    @Query("DELETE FROM FolderUriTree WHERE id = :id")
    fun deleteAtId(id: Long)

    @Query("DELETE FROM FolderUriTree WHERE uriTree = :uriTree")
    fun deleteAtUriTree(uriTree: String)

    @Query("SELECT * FROM FolderUriTree WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): FolderUriTree?

    @Query("SELECT * FROM FolderUriTree WHERE uriTree = :uriTree LIMIT 1")
    fun getAtUriTree(uriTree: String): FolderUriTree?

    @Query("SELECT * FROM FolderUriTree ORDER BY :order_name, :asc_desc_mode")
    fun getAll(order_name: String = "id", asc_desc_mode: String = "ASC"): LiveData<List<FolderUriTree>>
    @Query("SELECT * FROM FolderUriTree ORDER BY :order_name, :asc_desc_mode")
    fun getAllDirect(order_name: String = "id", asc_desc_mode: String = "ASC"): List<FolderUriTree>

    @Query("UPDATE FolderUriTree SET lastModified = -1")
    fun resetAllFolderUriTreesLastModified()
}