package com.prosabdev.fluidmusic.roomdatabase

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

    @Query("DELETE FROM " + ConstantValues.FLUID_MUSIC_FOLDER_URI_TREE)
    fun deleteAll()

    @Query("SELECT * FROM " + ConstantValues.FLUID_MUSIC_FOLDER_URI_TREE)
    fun getAllFolderUriTrees(): Flow<List<FolderUriTree>>

    @Query("SELECT * FROM " + ConstantValues.FLUID_MUSIC_FOLDER_URI_TREE)
    fun getAllFolderUriTreesDirectly(): List<FolderUriTree>
}