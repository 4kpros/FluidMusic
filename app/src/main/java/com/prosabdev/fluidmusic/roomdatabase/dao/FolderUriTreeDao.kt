package com.prosabdev.fluidmusic.roomdatabase.dao

import androidx.room.*
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.utils.ConstantValues
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderUriTreeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun Insert(folderUriTree: FolderUriTree?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertMultiple(folderUriTree: ArrayList<FolderUriTree?>?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun Update(folderUriTree: FolderUriTree?)

    @Delete
    fun Delete(folderUriTree: FolderUriTree?)

    @Query("DELETE FROM FolderUriTree WHERE id = :position")
    fun DeleteAtPosition(position: Int)

    @Query("DELETE FROM FolderUriTree")
    fun deleteAll()

    @Query("UPDATE FolderUriTree SET lastModified = -1")
    fun resetAllFolderUriTreesLastModified()

    @Query("SELECT * FROM FolderUriTree")
    fun getAllFolderUriTrees(): Flow<List<FolderUriTree>>

    @Query("SELECT * FROM FolderUriTree")
    fun getAllFolderUriTreesDirectly(): List<FolderUriTree>
}