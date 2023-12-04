package com.prosabdev.common.roomdatabase.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.FolderUriTree
import com.prosabdev.common.roomdatabase.AppDatabase
import com.prosabdev.common.roomdatabase.dao.FolderUriTreeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderUriTreeRepository(ctx : Context) {

    private var mDao: FolderUriTreeDao? = AppDatabase.getDatabase(ctx).folderUriTreeDao()

    suspend fun insert(folderUriTree: FolderUriTree?) : Long? {
        return withContext(Dispatchers.IO){
            mDao?.insert(folderUriTree)
        }
    }
    suspend fun update(folderUriTree: FolderUriTree?) {
        return withContext(Dispatchers.IO){
            mDao?.update(folderUriTree)
        }
    }
    suspend fun delete(folderUriTree: FolderUriTree?) {
        return withContext(Dispatchers.IO){
            mDao?.delete(folderUriTree)
        }
    }
    suspend fun deleteAtId(id: Long) {
        withContext(Dispatchers.IO){
            mDao?.deleteAtId(id)
        }
    }
    suspend fun deleteAtUriTree(uri: String?) {
        withContext(Dispatchers.IO){
            mDao?.deleteAtUriTree(uri)
        }
    }
    suspend fun deleteMultiple(songList: ArrayList<FolderUriTree>?) {
        withContext(Dispatchers.IO){
            mDao?.deleteMultiple(songList)
        }
    }
    suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            mDao?.deleteAll()
        }
    }
    suspend fun getAtId(id: Long) : FolderUriTree? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll(orderBy: String?) : LiveData<List<FolderUriTree>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(orderBy)
        }
    }
}