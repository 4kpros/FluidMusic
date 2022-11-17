package com.prosabdev.fluidmusic.viewmodels

import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.FolderUriTreeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FolderUriTreeViewModel(private val mFolderUriTreeDao: FolderUriTreeDao) : ViewModel()  {

    suspend fun insertFolderUriTree(it: FolderUriTree) = mFolderUriTreeDao.Insert(it)
    suspend fun getAllFolderUriTrees(): Flow<List<FolderUriTree>> = mFolderUriTreeDao.getAllFolderUriTrees()
    suspend fun getAllFolderUriTreesDirectly(): List<FolderUriTree> = mFolderUriTreeDao.getAllFolderUriTreesDirectly()
    suspend fun deleteAll() = mFolderUriTreeDao.deleteAll()
    suspend fun deleteItem(folderUriTree : FolderUriTree?) = mFolderUriTreeDao.Delete(folderUriTree)

}