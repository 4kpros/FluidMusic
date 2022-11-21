package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
import kotlinx.coroutines.flow.Flow

class FolderUriTreeViewModel(private val mFolderUriTreeDao: FolderUriTreeDao) : ViewModel()  {

    suspend fun insertItem(folderUriTree: FolderUriTree) = mFolderUriTreeDao.Insert(folderUriTree)
    suspend fun insertMultipleItems(folderUriTreeList: ArrayList<FolderUriTree?>?) = mFolderUriTreeDao.InsertMultiple(folderUriTreeList)

    suspend fun deleteItem(folderUriTree : FolderUriTree?) = mFolderUriTreeDao.Delete(folderUriTree)
    suspend fun deleteMultipleItems(folderUriTreeList: ArrayList<FolderUriTree?>?) = mFolderUriTreeDao.DeleteMultiple(folderUriTreeList)
    suspend fun deleteAll() = mFolderUriTreeDao.deleteAll()

    suspend fun getAllFolderUriTrees(): Flow<List<FolderUriTree>> = mFolderUriTreeDao.getAllFolderUriTrees()
    suspend fun getAllFolderUriTreesDirectly(): List<FolderUriTree> = mFolderUriTreeDao.getAllFolderUriTreesDirectly()
}