package com.prosabdev.fluidmusic.roomdatabase.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.AppDatabase
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
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
        withContext(Dispatchers.IO){
            mDao?.update(folderUriTree)
        }
    }
    suspend fun delete(folderUriTree: FolderUriTree?) {
        withContext(Dispatchers.IO){
            mDao?.delete(folderUriTree)
        }
    }
    suspend fun deleteAtId(id: Long) {
        return withContext(Dispatchers.IO){
            mDao?.deleteAtId(id)
        }
    }
    suspend fun deleteAtUriTree(uri: String) {
        return withContext(Dispatchers.IO){
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
    //Getters
    suspend fun getAtId(id: Long) : FolderUriTree? {
        return withContext(Dispatchers.IO){
            mDao?.getAtId(id)
        }
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<FolderUriTree>>? {
        return withContext(Dispatchers.IO){
            mDao?.getAll(order_name, asc_desc_mode)
        }
    }
}