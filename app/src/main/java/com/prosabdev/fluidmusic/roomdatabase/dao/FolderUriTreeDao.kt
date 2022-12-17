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
    fun update(folderUriTree: FolderUriTree?) : Int

    @Delete
    fun delete(folderUriTree: FolderUriTree?) : Int

    @Delete
    fun deleteMultiple(folderUriTree: ArrayList<FolderUriTree>?) : Int

    @Query("DELETE FROM FolderUriTree")
    fun deleteAll() : Int

    @Query("DELETE FROM FolderUriTree WHERE id = :id")
    fun deleteAtId(id: Long) : Int

    @Query("DELETE FROM FolderUriTree WHERE uriTree = :uriTree")
    fun deleteAtUriTree(uriTree: String?)

    @Query("SELECT * FROM FolderUriTree WHERE id = :id LIMIT 1")
    fun getAtId(id: Long): FolderUriTree?

    @Query("SELECT * FROM FolderUriTree WHERE uriTree = :uriTree LIMIT 1")
    fun getAtUriTree(uriTree: String?): FolderUriTree?

    @Query("SELECT * FROM FolderUriTree " +
            "ORDER BY " +
            "CASE :orderBy WHEN 'uriTree' THEN FolderUriTree.uriTree END ASC," +
            "CASE :orderBy WHEN 'path' THEN FolderUriTree.path END ASC," +
            "CASE :orderBy WHEN 'deviceName' THEN FolderUriTree.deviceName END ASC," +
            "CASE :orderBy WHEN 'lastModified' THEN FolderUriTree.lastModified END ASC," +
            "CASE :orderBy WHEN 'lastPathSegment' THEN FolderUriTree.lastPathSegment END ASC," +
            "CASE :orderBy WHEN 'normalizeScheme' THEN FolderUriTree.normalizeScheme END ASC," +
            "CASE :orderBy WHEN 'pathTree' THEN FolderUriTree.pathTree END ASC," +
            "CASE :orderBy WHEN 'id' THEN FolderUriTree.id END ASC"
    )
    fun getAll(orderBy: String?): LiveData<List<FolderUriTree>>?

    @Query("SELECT * FROM FolderUriTree")
    fun getAllDirectly(): List<FolderUriTree>?
}