package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.roomdatabase.repositories.FolderUriTreeRepository

class FolderUriTreeViewModel(app: Application) : AndroidViewModel(app) {

    private var repository: FolderUriTreeRepository? = FolderUriTreeRepository(app)

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
    suspend fun getAll(order_by: String = "id") : LiveData<List<FolderUriTree>>? {
        return repository?.getAll(order_by)
    }
}