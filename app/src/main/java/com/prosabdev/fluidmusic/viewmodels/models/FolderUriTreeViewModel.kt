package com.prosabdev.fluidmusic.viewmodels.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.repositories.FolderUriTreeRepository

class FolderUriTreeViewModel(ctx : Context) : ViewModel()  {

    private var repository: FolderUriTreeRepository? = FolderUriTreeRepository(ctx)

    suspend fun insert(folderUriTree: FolderUriTree?) : Long? {
        return repository?.insert(folderUriTree)
    }
    suspend fun update(folderUriTree: FolderUriTree?) {
        repository?.update(folderUriTree)
    }
    suspend fun delete(folderUriTree: FolderUriTree?) {
        repository?.delete(folderUriTree)
    }
    suspend fun deleteAll() {
        repository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : FolderUriTree? {
        return repository?.getAtId(id)
    }
    suspend fun getAll(order_name: String = "title", asc_desc_mode: String = "ASC") : LiveData<List<FolderUriTree>>? {
        return repository?.getAll(order_name, asc_desc_mode)
    }
}