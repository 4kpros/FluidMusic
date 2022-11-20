package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
import kotlinx.coroutines.flow.Flow

class FolderUriTreeViewModel(private val mFolderUriTreeDao: FolderUriTreeDao) : ViewModel()  {

    suspend fun insertFolderUriTree(it: FolderUriTree) = mFolderUriTreeDao.Insert(it)
    suspend fun getAllFolderUriTrees(): Flow<List<FolderUriTree>> = mFolderUriTreeDao.getAllFolderUriTrees()
    suspend fun getAllFolderUriTreesDirectly(): List<FolderUriTree> = mFolderUriTreeDao.getAllFolderUriTreesDirectly()
    suspend fun deleteAll() = mFolderUriTreeDao.deleteAll()
    suspend fun deleteItemAtPosition(position : Int) = mFolderUriTreeDao.DeleteAtPosition(position)
    suspend fun deleteItem(folderUriTree : FolderUriTree?) = mFolderUriTreeDao.Delete(folderUriTree)
    suspend fun resetAllFolderUriTreesLastModified() = mFolderUriTreeDao.resetAllFolderUriTreesLastModified()
    suspend fun updateItem(folderUriTree : FolderUriTree?) = mFolderUriTreeDao.Update(folderUriTree)


}