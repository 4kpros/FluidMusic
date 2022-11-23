package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
import com.prosabdev.fluidmusic.roomdatabase.repositories.FolderUriTreeRepository
import com.prosabdev.fluidmusic.roomdatabase.repositories.explore.SongItemRepository
import kotlinx.coroutines.flow.Flow

class FolderUriTreeViewModel(val ctx : Context) : ViewModel()  {

    private var repository: FolderUriTreeRepository? = FolderUriTreeRepository(ctx)

    suspend fun insertItem(folderUriTree: FolderUriTree) : Long = mFolderUriTreeDao.Insert(folderUriTree)
    suspend fun insertMultipleItems(folderUriTreeList: ArrayList<FolderUriTree?>?) : List<Long> = mFolderUriTreeDao.InsertMultiple(folderUriTreeList)

    suspend fun deleteItem(folderUriTree : FolderUriTree?) = mFolderUriTreeDao.Delete(folderUriTree)
    suspend fun deleteMultipleItems(folderUriTreeList: ArrayList<FolderUriTree?>?) = mFolderUriTreeDao.DeleteMultiple(folderUriTreeList)
    suspend fun deleteAll() = mFolderUriTreeDao.deleteAll()

    suspend fun getAllFolderUriTrees(): Flow<List<FolderUriTree>> = mFolderUriTreeDao.getAllFolderUriTrees()
    suspend fun getAllFolderUriTreesDirectly(): List<FolderUriTree> = mFolderUriTreeDao.getAllFolderUriTreesDirectly()
}